package com.poker.reader.domain.service;

import com.poker.reader.domain.repository.CardsRepository;
import com.poker.reader.domain.repository.PokerLineRepository;
import com.poker.reader.domain.util.CardsGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class FileProcessorService {

    private final CardsRepository cardsRepository;
    private final PokerLineRepository pokerLineRepository;

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

        String message = String.format("Processed hand %d in %d ms",
                handId, (System.currentTimeMillis() - start));

        log.info(message);
    }

    private void createCards() {
        if (cardsRepository.count() == 0) {
            cardsRepository.saveAll(CardsGenerator.generateCards());
        }
    }
}