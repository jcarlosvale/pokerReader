package com.poker.reader.domain.service;

import com.poker.reader.domain.model.*;
import com.poker.reader.domain.repository.*;
import com.poker.reader.domain.util.CardUtil;
import com.poker.reader.view.rs.dto.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.poker.reader.domain.util.Chen.calculateChenFormulaFrom;

@Log4j2
@RequiredArgsConstructor
@Component
public class FileHtmlProcessorService {

    private static final String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm:ss";
    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;
    private final PlayerPositionRepository playerPositionRepository;
    private final CardsOfPlayerRepository cardsOfPlayerRepository;
    private final HandRepository handRepository;
    private final PokerLineRepository pokerLineRepository;
    private final String HERO = "jcarlos.vale";


    public Page<PlayerDto> findPaginatedPlayers(Pageable pageable, boolean isMonitoring) {

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();

        Page<Player> pagePlayers = playerRepository.findAll(pageable);
        List<PlayerDto> playerDtoList = new ArrayList<>();

        for(Player player: pagePlayers.getContent()) {
            playerDtoList.add(extractPlayerDtoInfo(player, isMonitoring));
        }

        return new PageImpl<>(playerDtoList, PageRequest.of(currentPage, pageSize), playerRepository.count());
    }

    private PlayerDto extractPlayerDtoInfo(@NonNull Player player, boolean isMonitoring) {
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
            Cards cards = cardsOfPlayer.getCards();
            avgChen += cards.getChen();
            rawCardsList.add(cards.getDescription());
            normalisedCardsSet.add(cards.getNormalised());
        }
        List<String> normalisedCardsList = new ArrayList<>(normalisedCardsSet);
        normalisedCardsList.sort((o1, o2) -> calculateChenFormulaFrom(o2) - calculateChenFormulaFrom(o1));

        if (showDowns > 0) avgChen = avgChen / showDowns;
        LocalDateTime createdAt = player.getCreatedAt();
        String rawCards = CardUtil.convertListToString(rawCardsList);
        String normalisedCards = CardUtil.convertListToString(normalisedCardsList);
        String css = "d-none";
        if ((!isMonitoring) || (showDowns > 5)) {
            css = classNameFromChenValue(avgChen);
        }
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
                        .css(css)
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
                                        .hands(handRepository.countAllByTournament(tournament))
                                        .createdAt(tournament.getCreatedAt())
                                        .build())
                        .collect(Collectors.toList());

        return new PageImpl<>(tournamentsDtoList, PageRequest.of(currentPage, pageSize), tournamentRepository.count());
    }

    public Long getLasHand(Long tournamentId) {
        return handRepository.findMostRecent(tournamentId).getHandId();
    }

    public List<PlayerDto> getPlayersToMonitorFromHand(Long handId) {
        return
        playerPositionRepository
                .findByHandId(handId)
                .stream()
                .filter(Predicate.not(playerPosition -> playerPosition.getPlayer().getNickname().equals(HERO)))
                .map(playerPosition -> extractPlayerDtoInfo(playerPosition.getPlayer(), true))
                .collect(Collectors.toList());
    }

    private static String classNameFromChenValue(long avgChenValue) {
        if (avgChenValue >= 10) return "bg-primary";
        if (avgChenValue >= 8) return "bg-success";
        if (avgChenValue >= 5) return "table-warning";
        return "bg-danger";
    }

    public StackDto calculateAvgStack(Long handId) {
        List<PlayerPosition> playerPositions = playerPositionRepository.findByHandId(handId);
        Hand hand = handRepository.getById(handId);
        long avgStack = playerPositions.stream()
                .collect(Collectors.averagingLong(PlayerPosition::getStack))
                .longValue();
        Optional<PlayerPosition> playerPositionOfHero = playerPositions.stream()
                .filter(playerPosition -> playerPosition.getPlayer().getNickname().equals(HERO))
                .findFirst();
        long stackFromHero = playerPositionOfHero.isPresent() ? playerPositionOfHero.get().getStack() : 0L;
        int blinds = (int) (stackFromHero / hand.getBigBlind());
        long minBlinds = 15 * hand.getBigBlind();
        String recommendation = analyseStack(stackFromHero, avgStack, blinds);
        return StackDto.builder()
                .avgStack(avgStack)
                .stackFromHero(stackFromHero)
                .blinds(blinds)
                .minBlinds(minBlinds)
                .recommendation(recommendation)
                .build();
    }

    private String analyseStack(long stackFromHero, long avgStack, int blinds) {
        if(blinds <= 10) {
            return "ALL IN, LESS THAN 10 BLINDS";
        }
        if ((stackFromHero < avgStack) && (blinds <= 15)){
            return "ALL IN, LESS THAN AVERAGE BLINDS < 15";
        }
        if (stackFromHero > avgStack) {
            return "ABOVE AVG STACK";
        }
        return "PLAY!";
    }

    public List<HandDto> getHandsFromTournament(Long tournamentId) {
        checkNotNull(tournamentId, "tournamentId must be not null");
        Optional<Tournament> tournamentOptional = tournamentRepository.findById(tournamentId);
        return tournamentOptional
                .map(tournament -> handRepository
                        .findAllByTournamentOrderByPlayedAt(tournament)
                        .stream()
                        .map(this::toHandDto)
                        .collect(Collectors.toList()))
                .orElseGet(List::of);
    }

    private HandDto toHandDto(@NonNull Hand hand) {
        return HandDto
                .builder()
                .handId(hand.getHandId())
                .level(hand.getLevel())
                .blinds(hand.getSmallBlind() + "/" + hand.getBigBlind())
                .playedAt(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).format(hand.getPlayedAt()))
                .build();
    }

    public HandDto getHandInfo(Long handId) {
        return
                handRepository
                        .findById(handId)
                        .map(this::toHandDto)
                        .orElseGet(() -> HandDto.builder().build());
    }

    public String getRawDataFrom(Long handId) {
        return
            pokerLineRepository
                    .getAllByHandIdOrderByLineNumber(handId)
                    .stream()
                    .map(PokerLine::getLine)
                    .collect(Collectors.joining("<br>"));
    }

    public List<PlayerPositionDto> getPlayersFromHand(Long handId) {
        Hand hand = handRepository.getById(handId);
        List<PlayerPosition> playersPositions = playerPositionRepository.findByHandId(handId);
        return
                playersPositions
                        .stream()
                        .map(playerPosition -> toPlayerPositionDto(playerPosition, hand))
                        .collect(Collectors.toList());
    }

    private PlayerPositionDto toPlayerPositionDto(PlayerPosition playerPosition, Hand hand) {
        PlayerPositionDto playerPositionDto =
                PlayerPositionDto
                        .builder()
                        .nickname(playerPosition.getPlayer().getNickname())
                        .position(String.valueOf(playerPosition.getPosition()))
                        .stack(playerPosition.getStack())
                        .blinds(playerPosition.getStack() / hand.getBigBlind())
                        .build();

        CardsOfPlayer cardsOfPlayer = playerPosition.getCardsOfPlayer();
        if(Objects.nonNull(cardsOfPlayer)) {
            Cards cards = cardsOfPlayer.getCards();
            playerPositionDto.setCards(cards.getDescription());
            playerPositionDto.setChen(cards.getChen());
            playerPositionDto.setCss(classNameFromChenValue(cards.getChen()));
        }

        return playerPositionDto;
    }
}
