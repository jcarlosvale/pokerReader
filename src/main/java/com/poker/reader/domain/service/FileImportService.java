package com.poker.reader.domain.service;

import static com.google.common.base.Preconditions.checkArgument;

import com.poker.reader.domain.model.FileSection;
import com.poker.reader.domain.model.PokerLine;
import com.poker.reader.domain.repository.PokerLineRepository;
import com.poker.reader.domain.util.Util;
import com.zaxxer.hikari.HikariDataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.copy.CopyManager;
import org.postgresql.jdbc.PgConnection;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class FileImportService {

    private final DataSource dataSource;
    private final PokerLineRepository pokerLineRepository;
    private final Set<Long> handIdsCache;

    public FileImportService(DataSource dataSource,
                             PokerLineRepository pokerLineRepository) {
        this.dataSource = dataSource;
        this.pokerLineRepository = pokerLineRepository;
        this.handIdsCache = pokerLineRepository.getDistinctHandIds();
    }

    public boolean importFile(@NonNull final String filename, @NonNull final List<String> lines) {
        long start = System.currentTimeMillis();

        List<String> normalisedLines = removeInvalidLines(lines);
        List<PokerLine> listOfPokerLines = extractLinesOfFile(normalisedLines, filename);
        Optional<Long> tournamentOptional = persistData(listOfPokerLines);

        tournamentOptional.ifPresent(tournamentId -> {
            checkArgument(listOfPokerLines.size() == pokerLineRepository.countByTournamentId(tournamentId));
            log.info("Imported file {} in {} ms", filename, (System.currentTimeMillis() - start));
        });

        return tournamentOptional.isPresent();
    }

    private List<PokerLine> extractLinesOfFile(@NonNull final List<String> normalisedLines,
                                               @NonNull final String filename) {
        long start = System.currentTimeMillis();

        List<PokerLine> listOfPokerLines = new ArrayList<>();
        FileSection currentSection = FileSection.HEADER;
        Long handId = null;
        Long tournamentId = null;
        Integer tableId = null;
        long lineNumber = 1L;
        LocalDateTime playedAt = null;

        for(int i = 0; i < normalisedLines.size(); i++) {
            String line = normalisedLines.get(i);
            if (line.contains("PokerStars Hand #")) {
                currentSection = FileSection.HEADER;
                handId = Long.valueOf(StringUtils.substringBetween(line, "PokerStars Hand #", ": Tournament ").trim());
                tournamentId = Long.valueOf(StringUtils.substringBetween(line, ": Tournament #", ", ").trim());
                String strDateTime = StringUtils.substringBetween(line, "[", "]").trim();
                playedAt = Util.toLocalDateTime(strDateTime);

                //GET TABLE ID NEXT LINE
                String tableLine = normalisedLines.get(i+1);
                tableId = Integer.valueOf(StringUtils.substringBetween(tableLine, "Table '" + tournamentId + " ", "'").trim());  //table id
            }
            else if (line.contains("PokerStars Home Game Hand #")) {
                return List.of();  //dont process home games
            }
            else if (line.contains("*** HOLE CARDS ***"))  currentSection = FileSection.PRE_FLOP;
            else if (line.contains("*** FLOP ***"))        currentSection = FileSection.FLOP;
            else if (line.contains("*** TURN ***"))        currentSection = FileSection.TURN;
            else if (line.contains("*** RIVER ***"))       currentSection = FileSection.RIVER;
            else if (line.contains("*** SHOW DOWN ***"))   currentSection = FileSection.SHOWDOWN;
            else if (line.contains("*** SUMMARY ***"))     currentSection = FileSection.SUMMARY;

            listOfPokerLines.add(
                    PokerLine.builder()
                            .tournamentId(tournamentId)
                            .lineNumber(lineNumber)
                            .handId(handId)
                            .tableId(tableId)
                            .playedAt(playedAt)
                            .section(currentSection.name())
                            .line(line)
                            .filename(filename)
                            .build());
            lineNumber++;
        }

        long end = System.currentTimeMillis();
        log.info("Extracted lines {} ms", (end - start));

        return listOfPokerLines;
    }

    private List<String> removeInvalidLines(@NonNull final List<String> lines) {
        return lines
        .stream()
        .map(String::trim)
        .filter(StringUtils::isNotBlank)
        .collect(Collectors.toList());
    }

    private Optional<Long> persistData(@NonNull final List<PokerLine> listOfPokerLines) {
        long start = System.currentTimeMillis();

        if (listOfPokerLines.isEmpty()) return Optional.empty();

        List<PokerLine> listOfPokerLinesToInsert = filterLinesToInsert(listOfPokerLines);

        if (listOfPokerLinesToInsert.isEmpty()) {
            log.info("File already processed {}", listOfPokerLines.get(0).getFilename());
            return Optional.empty();
        } else {
            //save lines
            saveLines(listOfPokerLinesToInsert);
            long end = System.currentTimeMillis();
            log.info("Persisted {} ms", (end - start));

            return Optional.of(listOfPokerLines.get(0).getTournamentId());
        }
    }

    private List<PokerLine> filterLinesToInsert(List<PokerLine> listOfPokerLines) {
        Set<Long> distinctHandsFromFile =
                listOfPokerLines.stream().map(PokerLine::getHandId).collect(Collectors.toSet());

        Set<Long> handIdsToInsert =
        distinctHandsFromFile.stream().filter(Predicate.not(handIdsCache::contains)).collect(Collectors.toSet());

        handIdsCache.addAll(handIdsToInsert);
        return
        listOfPokerLines.stream().filter(pokerLine -> handIdsToInsert.contains(pokerLine.getHandId())).collect(Collectors.toList());
    }

    private void saveLines(@NonNull final List<PokerLine> listOfPokerLines) {
        log.info("Active: " + ((HikariDataSource)dataSource).getHikariPoolMXBean().getActiveConnections());

        try {

            final String COPY = "COPY pokerline (tournament_id, line_number, played_at, section, line, table_id, hand_id, filename)"
                    + " FROM STDIN WITH (FORMAT TEXT, ENCODING 'UTF-8', DELIMITER '\t',"
                    + " HEADER false)";

            Connection connection = dataSource.getConnection();
            PgConnection unwrapped = connection.unwrap(PgConnection.class);
            CopyManager copyManager = unwrapped.getCopyAPI();

            StringBuilder sb = new StringBuilder();
            for(PokerLine pokerLine : listOfPokerLines) {
                sb.append(pokerLine.getTournamentId()).append("\t");
                sb.append(pokerLine.getLineNumber()).append("\t");
                sb.append(pokerLine.getPlayedAt()).append("\t");
                sb.append(pokerLine.getSection()).append("\t");
                sb.append(pokerLine.getLine()).append("\t");
                sb.append(pokerLine.getTableId()).append("\t");
                sb.append(pokerLine.getHandId()).append("\t");
                sb.append(pokerLine.getFilename()).append("\n");
            }

            if (sb.length() > 0) {
                InputStream is = new ByteArrayInputStream(sb.toString().getBytes());
                copyManager.copyIn(COPY, is);
                sb.setLength(0);
            }
            connection.close();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}