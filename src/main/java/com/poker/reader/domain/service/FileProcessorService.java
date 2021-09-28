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
        log.info("Saving player, position, stack...");
        pokerLineRepository.savePlayerPosition();
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
        log.info("Saving cards of players...");
        pokerLineRepository.saveCardsOfPlayer();
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        startOp = System.currentTimeMillis();
        log.info("Saving blind positions of players...");
        pokerLineRepository.saveBlindPositions();
        log.info("Processed in {} ms", (System.currentTimeMillis() - startOp));

        String message = String.format("Processed %d lines in %d ms",
                pokerLineRepository.count(), (System.currentTimeMillis() - start));

        log.info(message);

        return message;
    }

    private void createCards() {
        if (cardsRepository.count() == 0) {
            cardsRepository.saveAll(CardsGenerator.generateCards());
        }
    }
}