package com.poker.reader.domain.service;

import com.poker.reader.configuration.PokerReaderProperties;
import com.poker.reader.domain.model.*;
import com.poker.reader.domain.repository.*;
import com.poker.reader.domain.repository.dto.ShowCardDto;
import com.poker.reader.domain.util.CardsGenerator;
import com.poker.reader.domain.util.Converter;
import com.poker.reader.domain.util.Util;
import com.zaxxer.hikari.HikariDataSource;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.copy.CopyManager;
import org.postgresql.jdbc.PgConnection;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@Service
@Log4j2
@RequiredArgsConstructor
public class FileProcessorService {

    private final DataSource dataSource;

    private final TournamentRepository tournamentRepository;
    private final HandRepository handRepository;
    private final PlayerRepository playerRepository;
    private final SeatRepository seatRepository;
    private final CardsRepository cardsRepository;
    private final PokerFileRepository pokerFileRepository;
    private final PokerLineRepository pokerLineRepository;

    private final PokerReaderProperties pokerReaderProperties;



    public String processFilesFromDatabase() {
        long start = System.currentTimeMillis();

        tournamentRepository.saveNewTournaments();
        playerRepository.saveNewPlayers();
        handRepository.saveNewHands();

        List<Cards> cards = findOrCreateCards();
        List<Long> notProcessedFilesId = pokerFileRepository.getPokerFileNotProcessedIds();

        processCardsFromPlayer();


        String message = String.format("Processed %d/%d tournaments in %d ms",
                notProcessedFilesId.size(), pokerFileRepository.count(), (System.currentTimeMillis() - start));
        log.info(message);

        return message;
    }

    private void processCardsFromPlayer() {
        List<ShowCardDto> showCardDtoList =
                pokerLineRepository.getShowedCardsFromShowDown();
        showCardDtoList.addAll(pokerLineRepository.getShowedCardsFromSummary());
        saveSeatAsBatch(showCardDtoList);
    }

    private void saveSeatAsBatch(List<ShowCardDto> showCardDtoList) {
        log.info("Active connections: " + ((HikariDataSource)dataSource).getHikariPoolMXBean().getActiveConnections());

        try {

            final String COPY = "COPY seats (seat_id, raw_cards, cards_id, nickname)"
                    + " FROM STDIN WITH (FORMAT TEXT, ENCODING 'UTF-8', DELIMITER '\t',"
                    + " HEADER false)";

            Connection connection = dataSource.getConnection();
            PgConnection unwrapped = connection.unwrap(PgConnection.class);
            CopyManager copyManager = unwrapped.getCopyAPI();

            int lineNumber = 1;
            StringBuilder sb = new StringBuilder();
            for(ShowCardDto showCardDto : showCardDtoList) {

                sb.append(lineNumber).append("\t");
                sb.append(showCardDto.getCards()).append("\t");
                sb.append(Converter.toCard(showCardDto.getCards()).getDescription()).append("\t");
                sb.append(showCardDto.getPlayer()).append("\n");

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
            connection.close();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Cards> findOrCreateCards() {
        if (cardsRepository.count() == 0) {
            return cardsRepository.saveAll(CardsGenerator.generateCards());
        } else {
            return cardsRepository.findAll();
        }
    }

    public void processFile(String fileName, final List<String> lines) {
        extractHands(lines, fileName)
                .stream()
                .filter(Predicate.not(linesOfHand -> handRepository.existsById(linesOfHand.getHandId())))
                .forEach(this::processHand);
    }

    private void processHand(@NonNull LinesOfHand linesOfHand) {
        long start = System.currentTimeMillis();

        Tournament tournament =
                findOrCreateTournament(linesOfHand.getTournamentId(), linesOfHand.getFilename());

        Hand hand = new Hand(
                        linesOfHand.getHandId(),
                        linesOfHand.getPlayedAt(),
                        LocalDateTime.now(),
                        tournament);

        var headerLines = linesOfHand.getLinesFromSection(FileSection.HEADER);
        var playersSet = extractPlayers(headerLines);

        var showDownLines = linesOfHand.getLinesFromSection(FileSection.SHOWDOWN);
        var mapOfPlayersAndCards = extractCardsFromPlayers(showDownLines);

        //save players and seats
        var seatList =
        playersSet
                .stream()
                .map(nickname -> {
                    Player player = findOrCreatePlayer(nickname, hand.getPlayedAt());
                    if (mapOfPlayersAndCards.containsKey(nickname)) {
                        var rawCards = mapOfPlayersAndCards.get(nickname);
                        var cards = findOrCreateCards(rawCards);
                        mapOfPlayersAndCards.remove(nickname);
                        return Seat.builder()
                                .rawCards(rawCards)
                                //.hand(hand)
                                .player(player)
                                .cards(cards)
                                .build();
                    } else {
                        return Seat.builder()
                                .rawCards(null)
                                //.hand(hand)
                                .player(player)
                                .cards(null)
                                .build();
                    }
                })
                .collect(Collectors.toList());

        checkArgument(mapOfPlayersAndCards.isEmpty(), "inconsistent file processor");

        long end = System.currentTimeMillis();
//        log.info("Processed hand" + (end - start) + "ms");
        start = System.currentTimeMillis();

        handRepository.save(hand);
        seatRepository.saveAll(seatList);

        end = System.currentTimeMillis();
//        log.info("Saved hand" + (end - start) + "ms");
    }

    private Cards findOrCreateCards(String rawCards) {
        var cards = Converter.toCard(rawCards);
        return
                cardsRepository
                        .findById(cards.getDescription())
                        .orElse(cardsRepository.save(cards));
    }

    private Player findOrCreatePlayer(@NonNull String nickname, @NonNull LocalDateTime playedAt) {
        return
                playerRepository
                        .findById(nickname)
                        .orElse(
                                playerRepository.save(
                                        Player
                                                .builder()
                                                .nickname(nickname)
                                                .createdAt(LocalDateTime.now())
                                                .build()
                                )
                        );
    }

    public Tournament findOrCreateTournament(@NonNull String tournamentId, @NonNull String fileName) {
        return
        tournamentRepository
                .findById(tournamentId)
                .orElse(
                        tournamentRepository.save(
                                Tournament
                                        .builder()
                                        .tournamentId(tournamentId)
                                        .fileName(fileName)
                                        .createdAt(LocalDateTime.now())
                                        .build()));
    }

    private Map<String, String> extractCardsFromPlayers(List<String> lines) {
        if(CollectionUtils.isEmpty(lines)) return Map.of();
        return
                lines
                        .stream()
                        .filter(line -> line.contains(": shows ["))
                        .collect(
                                Collectors.toMap(
                                        line -> StringUtils.substringBefore(line,": shows ["),
                                        line -> line.substring(line.lastIndexOf("[") + 1, line.lastIndexOf("]"))));
    }

    private List<LinesOfHand> extractHands(@NonNull List<String> lines, String fileName) {
        List<LinesOfHand> hands = new ArrayList<>();
        LinesOfHand linesOfHand = null;
        FileSection currentSection = null;
        String tournament = null;
        for(String line: lines) {
            if (line.trim().isEmpty()) continue;
            if (line.contains("PokerStars Hand #") && line.contains(": Tournament #")) {
                currentSection = FileSection.HEADER;
                if (linesOfHand != null) {
                    hands.add(linesOfHand);
                }

                String handId = StringUtils.substringBetween(line, "PokerStars Hand #", ": Tournament ");
                String tournamentId = StringUtils.substringBetween(line, ": Tournament #", ", ");
                String strDateTime = StringUtils.substringBetween(line, "[", "]");
                LocalDateTime playedAt = Util.toLocalDateTime(strDateTime);

                if(tournament == null) {
                    tournament = tournamentId;
                } else {
                    checkArgument(tournamentId.equals(tournament), "invalid file tournament!");
                }
                linesOfHand = new LinesOfHand(handId, tournamentId, fileName, playedAt);
            }
            FileSection fileSection = extractSection(line);
            if (Objects.nonNull(fileSection)) {
                currentSection = fileSection;
            }
            if (linesOfHand != null) {
                linesOfHand.addLine(currentSection, line);
            }
        }
        if (linesOfHand != null) {
            hands.add(linesOfHand);
        }
        return hands;
    }

    public static FileSection extractSection(@NonNull final String line) {
        if (line.contains("PokerStars Hand #")) return FileSection.HEADER;
        if (line.contains("*** HOLE CARDS ***"))  return FileSection.PRE_FLOP;
        if (line.contains("*** FLOP ***"))        return FileSection.FLOP;
        if (line.contains("*** TURN ***"))        return FileSection.TURN;
        if (line.contains("*** RIVER ***"))       return FileSection.RIVER;
        if (line.contains("*** SHOW DOWN ***"))   return FileSection.SHOWDOWN;
        if (line.contains("*** SUMMARY ***"))     return FileSection.SUMMARY;
        return null;
    }

    public Set<String> extractPlayers(final List<String> lines) {
        if (CollectionUtils.isEmpty(lines)) {
            return Set.of();
        } else {
            return lines
                    .stream()
                    .filter(line -> line.startsWith("Seat ") && line.contains(" in chips"))
                    .map(seatLine -> StringUtils.substringBefore(seatLine, "in chips"))
                    .map(seatLine -> seatLine.substring(seatLine.indexOf(":")+1, seatLine.lastIndexOf("(")).trim()
                    )
                    .collect(Collectors.toSet());
        }
    }
}