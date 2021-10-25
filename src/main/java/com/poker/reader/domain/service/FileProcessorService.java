package com.poker.reader.domain.service;

import com.poker.reader.domain.model.HandPosition;
import com.poker.reader.domain.model.TablePosition;
import com.poker.reader.domain.repository.CardsRepository;
import com.poker.reader.domain.repository.HandPositionRepository;
import com.poker.reader.domain.repository.PokerLineRepository;
import com.poker.reader.domain.util.CardsGenerator;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.postgresql.copy.CopyManager;
import org.postgresql.jdbc.PgConnection;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@Service
@Log4j2
@RequiredArgsConstructor
public class FileProcessorService {

    private final DataSource dataSource;
    private final CardsRepository cardsRepository;
    private final PokerLineRepository pokerLineRepository;
    private final HandPositionRepository handPositionRepository;

    public String processFilesFromDatabase() {
        long start = System.currentTimeMillis();

        long startOp = System.currentTimeMillis();
        log.info("Creating cards...");
        createCards();
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving players...");
        pokerLineRepository.saveNewPlayers();
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving tournaments...");
        pokerLineRepository.saveNewTournaments();
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving hands...");
        pokerLineRepository.saveNewHands();
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving boards...");
        pokerLineRepository.saveBoard();
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving pot...");
        pokerLineRepository.savePot();
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving player, position, stack...");
        pokerLineRepository.savePlayerPosition();
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving cards of players...");
        pokerLineRepository.saveCardsOfPlayer();
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving blind positions of players...");
        pokerLineRepository.saveBlindPositions();
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving fold positions...");
        pokerLineRepository.saveFoldPosition();
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving win positions...");
        pokerLineRepository.saveWinPositions();
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving lose positions...");
        pokerLineRepository.saveLosePositions();
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving hand consolidation...");
        pokerLineRepository.saveHandConsolidation();
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving hand positions...");
        pokerLineRepository.saveHandPosition();
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Deleting table positions...");
        pokerLineRepository.deleteTablePosition();
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Process table positions...");
        processTablePosition();
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Update hand consolidation...");
        pokerLineRepository.updateHandConsolidation();
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        String message = String.format("Processed %d lines in %d ms",
                pokerLineRepository.count(), (System.currentTimeMillis() - start));

        log.info(message);

        return message;
    }

    public void processLastHandFromPokerFile(String filename) {
        long handId = pokerLineRepository.getLastHandFromFile(filename);
        processFilesFromDatabase(handId);
    }

    private void processFilesFromDatabase(long handId) {
        long start = System.currentTimeMillis();
        long startOp = System.currentTimeMillis();

        log.info("Saving players...");
        pokerLineRepository.saveNewPlayersFromHand(handId);
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving tournaments...");
        pokerLineRepository.saveNewTournamentsFromHand(handId);
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving hands...");
        pokerLineRepository.saveNewHandsFromHand(handId);
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving board...");
        pokerLineRepository.saveBoardFromHand(handId);
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving pot...");
        pokerLineRepository.savePotFromHand(handId);
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving player, position, stack...");
        pokerLineRepository.savePlayerPositionFromHand(handId);
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving cards of players...");
        pokerLineRepository.saveCardsOfPlayerFromHand(handId);
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving blind positions of players...");
        pokerLineRepository.saveBlindPositionsFromHand(handId);
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving fold positions...");
        pokerLineRepository.saveFoldPosition(handId);
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving win positions...");
        pokerLineRepository.saveWinPositions(handId);
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving lose positions...");
        pokerLineRepository.saveLosePositions(handId);
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving hand consolidation...");
        pokerLineRepository.saveHandConsolidation(handId);
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving hand positions...");
        pokerLineRepository.saveHandPosition(handId);
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Process table positions...");
        processTablePosition(handId);
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Update hand consolidation...");
        pokerLineRepository.updateHandConsolidation(handId);
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        String message = String.format("Processed hand %d in %d ms",
                handId, (System.currentTimeMillis() - start));

        log.info(message);
    }

    private void processTablePosition(long handId) {
        Optional<HandPosition> handPositionOptional = handPositionRepository.findById(handId);
        List<TablePosition> tablePositionList = new ArrayList<>();
        handPositionOptional.ifPresent(handPosition -> tablePositionList.addAll(generateTablePositionsFromHandPosition(handPosition)));
        saveTablePositions(tablePositionList);
    }

    private void createCards() {
        if (cardsRepository.count() == 0) {
            cardsRepository.saveAll(CardsGenerator.generateCards());
        }
    }

    private void processTablePosition() {
        List<HandPosition> handPositionList = handPositionRepository.findAll();
        List<TablePosition> tablePositionList = new ArrayList<>();
        for(HandPosition handPosition : handPositionList) {
            tablePositionList.addAll(generateTablePositionsFromHandPosition(handPosition));
        }
        saveTablePositions(tablePositionList);
    }

    private void saveTablePositions(List<TablePosition> tablePositionList) {
        log.info("Active: " + ((HikariDataSource)dataSource).getHikariPoolMXBean().getActiveConnections());

        try {

            final String COPY = "COPY table_position (hand, position, poker_position)"
                    + " FROM STDIN WITH (FORMAT TEXT, ENCODING 'UTF-8', DELIMITER '\t',"
                    + " HEADER false)";

            Connection connection = dataSource.getConnection();
            PgConnection unwrapped = connection.unwrap(PgConnection.class);
            CopyManager copyManager = unwrapped.getCopyAPI();

            StringBuilder sb = new StringBuilder();
            for(TablePosition tablePosition : tablePositionList) {
                sb.append(tablePosition.getHand()).append("\t");
                sb.append(tablePosition.getPosition()).append("\t");
                sb.append(tablePosition.getPokerPosition()).append("\n");
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

    private List<TablePosition> generateTablePositionsFromHandPosition(HandPosition handPosition) {

        List<TablePosition> tablePositionList = new ArrayList<>();

        int numberOfPlayers = handPosition.getNumberOfPlayers();
        long hand = handPosition.getHand();
        int buttonPosition = handPosition.getButton();

        Set<Integer> positions =
                Arrays.stream(handPosition.getPositions().split(","))
                        .map(Integer::valueOf)
                        .collect(Collectors.toSet());

        if (numberOfPlayers == 2) {
            tablePositionList.add(TablePosition.builder().hand(hand).position(buttonPosition).pokerPosition("BTN").build());
            positions.remove(buttonPosition);
            checkArgument(positions.size() == 1, "inconsistent positions size");
            positions.forEach(integer -> tablePositionList.add(TablePosition.builder().hand(hand).position(integer).pokerPosition("BB").build()));
        }
        else {
            tablePositionList.add(TablePosition.builder().hand(hand).position(buttonPosition).pokerPosition("BTN").build());
            int minPosition = handPosition.getMinPos();
            int maxPosition = handPosition.getMaxPos();

            LinkedList<Integer> linkedList = new LinkedList<>();

            //BTN last position
            linkedList.add(buttonPosition);
            //left from BTN position
            for(int i = buttonPosition-1; i>=minPosition; i--) {
                if(positions.contains(i)) linkedList.addFirst(i);
            }
            //right from BTN position
            for(int i = maxPosition; i > buttonPosition; i--) {
                if(positions.contains(i)) linkedList.addFirst(i);
            }
            int indexBtn = linkedList.size()-1;

            switch (numberOfPlayers) {
                case 10:
                    tablePositionList.add(TablePosition.builder().hand(hand).position(linkedList.get(indexBtn-7)).pokerPosition("UTG").build());
                case 9:
                    tablePositionList.add(TablePosition.builder().hand(hand).position(linkedList.get(indexBtn-6)).pokerPosition("UTG").build());
                case 8:
                    tablePositionList.add(TablePosition.builder().hand(hand).position(linkedList.get(indexBtn-5)).pokerPosition("UTG").build());
                case 7:
                    tablePositionList.add(TablePosition.builder().hand(hand).position(linkedList.get(indexBtn-4)).pokerPosition("MP").build());
                case 6:
                    tablePositionList.add(TablePosition.builder().hand(hand).position(linkedList.get(indexBtn-3)).pokerPosition("LJ").build());
                case 5:
                    tablePositionList.add(TablePosition.builder().hand(hand).position(linkedList.get(indexBtn-2)).pokerPosition("HJ").build());
                case 4:
                    tablePositionList.add(TablePosition.builder().hand(hand).position(linkedList.get(indexBtn-1)).pokerPosition("CO").build());
                default:
                    tablePositionList.add(TablePosition.builder().hand(hand).position(linkedList.get(1)).pokerPosition("BB").build());
                    tablePositionList.add(TablePosition.builder().hand(hand).position(linkedList.get(0)).pokerPosition("SB").build());
            }
        }

        return tablePositionList;
    }
}