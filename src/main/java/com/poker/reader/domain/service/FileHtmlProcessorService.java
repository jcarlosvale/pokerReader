package com.poker.reader.domain.service;

import com.poker.reader.domain.model.Cards;
import com.poker.reader.domain.model.CardsOfPlayer;
import com.poker.reader.domain.model.Hand;
import com.poker.reader.domain.model.Player;
import com.poker.reader.domain.repository.*;
import com.poker.reader.domain.util.CardUtil;
import com.poker.reader.view.rs.dto.PlayerDto;
import com.poker.reader.view.rs.dto.TournamentDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.poker.reader.domain.util.Chen.calculateChenFormulaFrom;

@Log4j2
@RequiredArgsConstructor
@Component
public class FileHtmlProcessorService {

    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;
    private final PlayerPositionRepository playerPositionRepository;
    private final CardsOfPlayerRepository cardsOfPlayerRepository;
    private final CardsRepository cardsRepository;
    private final HandRepository handRepository;


    public Page<PlayerDto> findPaginatedPlayers(Pageable pageable) {

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();

        Page<Player> pagePlayers = playerRepository.findAll(pageable);
        List<PlayerDto> playerDtoList = new ArrayList<>();

        for(Player player: pagePlayers.getContent()) {
            playerDtoList.add(extractPlayerDtoInfo(player));
        }

        return new PageImpl<>(playerDtoList, PageRequest.of(currentPage, pageSize), playerRepository.count());
    }

    private PlayerDto extractPlayerDtoInfo(@NonNull Player player) {
        String nickname = player.getNickname();
        List<CardsOfPlayer> cardsOfPlayerList =
                cardsOfPlayerRepository.getAllByNickname(nickname);
        List<String> rawCardsList = new ArrayList<>();
        Set<String> normalisedCardsSet = new HashSet<>();
        int totalHands = playerPositionRepository.countHandsOfPlayer(nickname);
        int showDowns = cardsOfPlayerList.size();
        int showDowStat = (int)(100.0 * showDowns / totalHands);
        int avgChen = 0;
        for(CardsOfPlayer cardsOfPlayer: cardsOfPlayerList) {
            Cards cards = cardsRepository.getById(cardsOfPlayer.getCards());
            avgChen += cards.getChen();
            rawCardsList.add(cards.getDescription());
            normalisedCardsSet.add(cards.getNormalised());
        }
        List<String> normalisedCardsList = new ArrayList<>(normalisedCardsSet);
        normalisedCardsList.sort((o1, o2) -> calculateChenFormulaFrom(o2) - calculateChenFormulaFrom(o1));

        if (showDowns > 0) avgChen = avgChen / showDowns;
        else avgChen = 0;
        LocalDateTime createdAt = player.getCreatedAt();
        String rawCards = CardUtil.convertListToString(rawCardsList);
        String normalisedCards = CardUtil.convertListToString(normalisedCardsList);

        return
                PlayerDto.builder()
                        .nickname(nickname)
                        .totalHands(totalHands)
                        .showdowns(showDowns)
                        .showdownStat(showDowStat)
                        .avgChen(avgChen)
                        .createdAt(createdAt)
                        .cards(normalisedCards)
                        .rawCards(rawCards)
                        .css(classNameFromChenValue(avgChen))
                        .build();
    }

    public Page<TournamentDto> findPaginatedTournaments(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();

        var tournamentsDtoList =
                tournamentRepository
                        .findAll(pageable)
                        .getContent()
                        .stream()
                        .map(tournament ->
                                TournamentDto
                                        .builder()
                                        .tournamentId(tournament.getTournamentId())
                                        .fileName(tournament.getFileName())
                                        .createdAt(tournament.getCreatedAt())
                                        .build())
                        .collect(Collectors.toList());

        return new PageImpl<>(tournamentsDtoList, PageRequest.of(currentPage, pageSize), tournamentRepository.count());

    }

    public List<PlayerDto> getLastPlayersFromTournament(Long tournamentId) {
        Hand hand = handRepository.findMostRecent(tournamentId);
        return
        playerPositionRepository
                .findByHandId(hand.getHandId())
                .stream()
                .filter(Predicate.not(playerPosition -> playerPosition.getNickname().equals("jcarlos.vale")))
                .map(playerPosition -> {
                    return extractPlayerDtoInfo(playerRepository.getById(playerPosition.getNickname()));
                })
                .collect(Collectors.toList());
    }

    private static String classNameFromChenValue(long avgChenValue) {
        if (avgChenValue >= 10) return "bg-primary";
        if (avgChenValue >= 8) return "bg-success";
        if (avgChenValue >= 5) return "table-warning";
        return "bg-danger";
    }
}
