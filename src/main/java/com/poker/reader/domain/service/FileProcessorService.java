package com.poker.reader.domain.service;

import com.poker.reader.domain.model.Cards;
import com.poker.reader.domain.repository.CardsRepository;
import com.poker.reader.domain.repository.PokerLineRepository;
import com.poker.reader.domain.repository.dto.PlayerAtPositionDto;
import com.poker.reader.domain.util.CardsGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
public class FileProcessorService {

    private final DataSource dataSource;

    private final CardsRepository cardsRepository;
    private final PokerLineRepository pokerLineRepository;

    public String processFilesFromDatabase() {
        long start = System.currentTimeMillis();

        findOrCreateCards();
        pokerLineRepository.saveNewTournaments();
        pokerLineRepository.saveNewPlayers();
        pokerLineRepository.saveNewHands();
        pokerLineRepository.savePlayerPosition();
        pokerLineRepository.saveCardsOfPlayer();


        Long notProcessedLines = pokerLineRepository.countNotProcessedLines();

        pokerLineRepository.updateToProcessedLines();

        String message = String.format("Processed %d/%d lines in %d ms",
                notProcessedLines, pokerLineRepository.count(), (System.currentTimeMillis() - start));

        log.info(message);

        return message;
    }

    private Map<String, PlayerAtPositionDto> getPlayersPositionsByNotProcessedHands() {
        /*
        final Map<String, PlayerAtPositionDto> playerAtPositionByHandMap = new HashMap<>();
        seatRepository
                .getPlayerInPositionByHand()
                .forEach(playerPositionAtHandDto -> {
                    String hand = playerPositionAtHandDto.getHand();
                    int position = playerPositionAtHandDto.getPosition();
                    String nickname = playerPositionAtHandDto.getPlayer();
                    PlayerAtPositionDto playerAtPositionMap = playerAtPositionByHandMap.computeIfAbsent(hand,
                            s -> new PlayerAtPositionDto());
                    playerAtPositionMap.putPlayerAtPosition(position, nickname);
                });
        return playerAtPositionByHandMap;*/
        return null;
    }

    private List<Cards> findOrCreateCards() {
        if (cardsRepository.count() == 0) {
            return cardsRepository.saveAll(CardsGenerator.generateCards());
        } else {
            return cardsRepository.findAll();
        }
    }
}