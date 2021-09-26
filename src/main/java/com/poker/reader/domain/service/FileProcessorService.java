package com.poker.reader.domain.service;

import com.poker.reader.domain.model.Cards;
import com.poker.reader.domain.repository.CardsRepository;
import com.poker.reader.domain.repository.PokerLineRepository;
import com.poker.reader.domain.repository.SeatRepository;
import com.poker.reader.domain.repository.dto.PlayerAtPositionDto;
import com.poker.reader.domain.repository.dto.ShowCardDto;
import com.poker.reader.domain.util.CardsGenerator;
import com.poker.reader.domain.util.Converter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

@Service
@Log4j2
@RequiredArgsConstructor
public class FileProcessorService {

    private final DataSource dataSource;

    private final SeatRepository seatRepository;
    private final CardsRepository cardsRepository;
    private final PokerLineRepository pokerLineRepository;

    public String processFilesFromDatabase() {
        long start = System.currentTimeMillis();

        pokerLineRepository.saveNewTournaments();
        pokerLineRepository.saveNewPlayers();
        pokerLineRepository.saveNewHands();
        findOrCreateCards();

        Map<String, PlayerAtPositionDto> playerPositionByHandMap = getPlayersPositionsByNotProcessedHands();
        saveSeatAsBatch(pokerLineRepository.getNewSeats(), playerPositionByHandMap);


        Long notProcessedLines = pokerLineRepository.countNotProcessedLines();

        pokerLineRepository.updateToProcessedLines();

        String message = String.format("Processed %d/%d lines in %d ms",
                notProcessedLines, pokerLineRepository.count(), (System.currentTimeMillis() - start));

        log.info(message);

        return message;
    }

    private Map<String, PlayerAtPositionDto> getPlayersPositionsByNotProcessedHands() {
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
        return playerAtPositionByHandMap;
    }

    private void saveSeatAsBatch(List<ShowCardDto> showCardDtoList,
                                 Map<String, PlayerAtPositionDto> playerPositionByHandMap) {
        log.info("Active connections: " + ((HikariDataSource)dataSource).getHikariPoolMXBean().getActiveConnections());

        try {

            final String COPY = "COPY seats (seat_id, raw_cards, cards_id, hand_id, nickname)"
                    + " FROM STDIN WITH (FORMAT TEXT, ENCODING 'UTF-8', DELIMITER '\t', NULL 'null', "
                    + " HEADER false)";

            Connection connection = dataSource.getConnection();
            PgConnection unwrapped = connection.unwrap(PgConnection.class);
            CopyManager copyManager = unwrapped.getCopyAPI();

            int lineNumber = 1;
            StringBuilder sb = new StringBuilder();
            for(ShowCardDto showCardDto : showCardDtoList) {

                //get nickname
                String nickname =
                        playerPositionByHandMap.get(showCardDto.getHand()).getPlayerAtPosition(showCardDto.getPosition());
                checkArgument(nickname != null, "hand = " + showCardDto.getHand());

                sb.append(lineNumber).append("\t");
                if (showCardDto.getCards() == null) {
                    sb.append("null").append("\t");
                    sb.append("null").append("\t");
                } else {
                    sb.append(showCardDto.getCards()).append("\t");
                    sb.append(Converter.toCard(showCardDto.getCards()).getDescription()).append("\t");
                }
                sb.append(showCardDto.getHand()).append("\t");
                sb.append(nickname).append("\n");

                lineNumber++;
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
}