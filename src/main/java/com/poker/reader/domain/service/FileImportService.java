package com.poker.reader.domain.service;

import com.poker.reader.configuration.PokerReaderProperties;
import com.poker.reader.domain.model.FileSection;
import com.poker.reader.domain.model.PokerFile;
import com.poker.reader.domain.model.PokerLine;
import com.poker.reader.domain.repository.PokerFileRepository;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.copy.CopyManager;
import org.postgresql.jdbc.PgConnection;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class FileImportService {

    private final DataSource dataSource;
    private final PokerFileRepository pokerFileRepository;
    private final PokerReaderProperties pokerReaderProperties;

    public void importFile(@NonNull final String filename, @NonNull final List<String> lines) {
        long start = System.currentTimeMillis();

        List<String> normalisedLines = removeInvalidLines(lines);
        List<PokerLine> listOfPokerLines = extractLinesOfFile(normalisedLines);
        persistData(filename, listOfPokerLines);

        log.info("Imported file {} in {} ms", filename, (System.currentTimeMillis() - start));
    }

    private List<PokerLine> extractLinesOfFile(@NonNull final List<String> normalisedLines) {
        long start = System.currentTimeMillis();

        List<PokerLine> listOfPokerLines = new ArrayList<>();
        FileSection currentSection = FileSection.HEADER;

        for(String line : normalisedLines) {
            if (line.contains("PokerStars Hand #"))        currentSection = FileSection.HEADER;
            else if (line.contains("*** HOLE CARDS ***"))  currentSection = FileSection.PRE_FLOP;
            else if (line.contains("*** FLOP ***"))        currentSection = FileSection.FLOP;
            else if (line.contains("*** TURN ***"))        currentSection = FileSection.TURN;
            else if (line.contains("*** RIVER ***"))       currentSection = FileSection.RIVER;
            else if (line.contains("*** SHOW DOWN ***"))   currentSection = FileSection.SHOWDOWN;
            else if (line.contains("*** SUMMARY ***"))     currentSection = FileSection.SUMMARY;

            listOfPokerLines.add(PokerLine.builder().section(currentSection.name()).line(line).build());
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

    private void persistData(@NonNull final String filename, @NonNull final List<PokerLine> listOfPokerLines) {
        long start = System.currentTimeMillis();

        if (pokerFileRepository.existsByFileName(filename)) {
            log.info("File already processed {}", filename);
            return;
        }

        //save poker file
        var pokerFile =
                pokerFileRepository.save(PokerFile
                        .builder()
                        .fileName(filename)
                        .isProcessed(false)
                        .createdAt(LocalDateTime.now())
                        .build());

        //save lines
        saveLines(listOfPokerLines, pokerFile);

        long end = System.currentTimeMillis();
        log.info("Persisted {} ms", (end - start));
    }

    private void saveLines(List<PokerLine> listOfPokerLines, PokerFile pokerFile) {
        try (PgConnection unwrapped = dataSource.getConnection().unwrap(PgConnection.class)){

            final String COPY = "COPY pokerline (poker_file_id, line_number, section,  line)"
                    + " FROM STDIN WITH (FORMAT TEXT, ENCODING 'UTF-8', DELIMITER '\t',"
                    + " HEADER false)";

            CopyManager copyManager = unwrapped.getCopyAPI();

            int lineNumber = 1;
            StringBuilder sb = new StringBuilder();
            for(PokerLine pokerLine : listOfPokerLines) {
                sb.append(pokerFile.getPokerFileId()).append("\t");
                sb.append(lineNumber).append("\t");
                sb.append(pokerLine.getSection()).append("\t");
                sb.append(pokerLine.getLine()).append("\n");

                lineNumber++;
                if (lineNumber % pokerReaderProperties.getBatchSize() == 0) {
                    InputStream is = new ByteArrayInputStream(sb.toString().getBytes());
                    copyManager.copyIn(COPY, is);
                    sb.setLength(0);
                }
            }

            if (sb.length() > 0) {
                InputStream is = new ByteArrayInputStream(sb.toString().getBytes());
                copyManager.copyIn(COPY, is);
                sb.setLength(0);
            }

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}