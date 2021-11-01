package com.poker.reader.domain.service;

import com.poker.reader.domain.model.Hand;
import com.poker.reader.domain.model.PokerLine;
import com.poker.reader.domain.repository.*;
import com.poker.reader.domain.repository.projection.HandDtoProjection;
import com.poker.reader.domain.repository.projection.PlayerDetailsDtoProjection;
import com.poker.reader.domain.repository.projection.PlayerDtoProjection;
import com.poker.reader.domain.repository.projection.TournamentDtoProjection;
import com.poker.reader.view.rs.dto.PageDto;
import com.poker.reader.view.rs.dto.PlayerMonitoredDto;
import com.poker.reader.view.rs.dto.RecommendationDto;
import com.poker.reader.view.rs.dto.StatsDto;
import com.poker.reader.view.rs.model.ModelTournamentMonitored;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@Log4j2
@RequiredArgsConstructor
@Component
public class FileHtmlProcessorService {

    private static final String HERO = "jcarlos.vale";

    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;
    private final HandRepository handRepository;
    private final PokerLineRepository pokerLineRepository;
    private final HandConsolidationRepository handConsolidationRepository;
    private final StatsService statsService;


    public Page<PlayerDtoProjection> findPaginatedPlayers(Pageable pageable) {

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();

        List<PlayerDtoProjection> playerDtoList = handConsolidationRepository.getAllPlayerDto(pageable).toList();

        return new PageImpl<>(playerDtoList, PageRequest.of(currentPage, pageSize), playerRepository.count());
    }

    public Page<TournamentDtoProjection> findPaginatedTournaments(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();

        var tournamentsDtoList =
                handConsolidationRepository
                        .getAllTournamentsDto(pageable);

        return new PageImpl<>(tournamentsDtoList.getContent(), PageRequest.of(currentPage, pageSize), tournamentRepository.count());
    }

    public Integer calculateAvgStackFromLastHandOfTournament(Long tournamentId) {
        return handConsolidationRepository.calculateAvgStackFromLastHandOfTournament(tournamentId);
    }

    public List<HandDtoProjection> getHandsFromTournament(Long tournamentId) {
        checkNotNull(tournamentId, "tournamentId must be not null");
        return handConsolidationRepository.getHandsFromTournament(tournamentId);
    }

    private String extractBoardShowdownFrom(String board) {
        int countCards = board.split(" ").length;
        if (countCards == 5) return "RIVER";
        if (countCards == 4) return "TURN";
        if (countCards == 3) return "FLOP";
        return "";
    }

    public String getRawDataFrom(Long handId) {
        return
            pokerLineRepository
                    .getAllByHandIdOrderByLineNumber(handId)
                    .stream()
                    .map(PokerLine::getLine)
                    .collect(Collectors.joining("<br>"));
    }
/*
    public List<PlayerDetailsDto> getPlayersDetailsFromHand(Long handId) {

        List<PlayerDetailsDtoProjection> playerDetailsDtoProjectionList = handConsolidationRepository.getPlayersDetailsFromHand(handId);

        return
                playerDetailsDtoProjectionList
                        .stream()
                        .map(playerDetailsDtoProjection -> toPlayerDetailsDto(playerDetailsDtoProjection))
                        .collect(Collectors.toList());
    }
*/
    private Optional<Integer> getPositionByPlace(List<PlayerDetailsDtoProjection> playerDetailsDtoProjectionList, String place) {
        return
                playerDetailsDtoProjectionList
                        .stream()
                        .filter(handConsolidation -> handConsolidation.getPlace() != null && handConsolidation.getPlace().equals(place))
                        .map(PlayerDetailsDtoProjection::getPosition)
                        .findFirst();
    }
/*
    private PlayerDetailsDto toPlayerDetailsDto(PlayerDetailsDtoProjection playerDetailsDtoProjection) {
        return
                PlayerDetailsDto
                        .builder()
                        .playerDetailsDtoProjection(playerDetailsDtoProjection)
                        .pokerTablePosition(playerDetailsDtoProjection.getPokerPosition())
                        .cssChen(classNameFromChenValue(playerDetailsDtoProjection.getChen()))
                        .cssNickname(classNameFromWinnerOrLoser(playerDetailsDtoProjection))
                        .build();
    }
*/
    /*
    private String classNameFromWinnerOrLoser(PlayerDetailsDtoProjection playerDetailsDtoProjection) {
        if (playerDetailsDtoProjection.getIsWinner()) {
            return "table-success";
        }
        if (playerDetailsDtoProjection.getIsLose()) {
            return "table-danger";
        }
        return "table-warning";
    }
*/
    public PlayerDtoProjection findPlayer(String nickname) {
        return handConsolidationRepository.getPlayerDtoByNickname(nickname);
    }

    public PageDto createHandPaginationFromTournament(long handId, long tournamentId) {
        List<Long> handsIds = handRepository.findAllHandIdByTournamentId(tournamentId);
        PageDto pageDto = PageDto.builder().totalPages(handsIds.size()).build();
        int index = Collections.binarySearch(handsIds, handId);
        //current
        pageDto.setCurrentPageId(handId);
        pageDto.setCurrentPageNumber(index);
        //previous
        if (index - 1 < 0) {
            pageDto.setPreviousPageId(handId);
            pageDto.setPreviousPageNumber(-1);
        } else {
            pageDto.setPreviousPageId(handsIds.get(index-1));
            pageDto.setPreviousPageNumber(index-1);
        }
        //next
        if (index+1 == handsIds.size()) {
            pageDto.setNextPageId(handId);
            pageDto.setNextPageNumber(-1);
        } else {
            pageDto.setNextPageId(handsIds.get(index+1));
            pageDto.setNextPageNumber(index+1);
        }
        return pageDto;
    }
/*
    public List<PlayerMonitoredDto> getPlayersToMonitorFromLastHandOfTournament(Long tournamentId,
            Map<String, PlayerDetailsDto> playerDetailsDtoMap) {
        return
                handConsolidationRepository
                        .getPlayersStacksFromLastHandOfTournament(tournamentId)
                        .stream()
                        .map(stackDtoProjection -> {
                            PlayerDto playerDto = findPlayer(stackDtoProjection.getNickname(), true);
                            return new PlayerMonitoredDto(playerDto, stackDtoProjection, playerDetailsDtoMap.get(stackDtoProjection.getNickname()));
                        })
                        .collect(Collectors.toList());
    }
*/
    private Integer calculateAvgStack(List<StatsDto> statsDtoList) {
        return (int) statsDtoList.stream().mapToInt(StatsDto::getStackOfPlayer).average().orElseThrow();
    }

    public ModelTournamentMonitored getTournamentMonitoredModel(Long tournamentId) {

        Hand hand = handRepository.findMostRecent(tournamentId);
        long handId = hand.getHandId();

        log.info("Retrieving tournament {} hand {}", tournamentId, handId);

        List<PlayerDtoProjection> playerDtoProjectionList = handConsolidationRepository.getPlayersDtoFromHand(handId);
        List<StatsDto> statsList = statsService.loadStats(tournamentId, handId);

        int avgStack = calculateAvgStack(statsList);
        List<PlayerMonitoredDto> playerMonitoredDtoList = mergeIntoPlayerMonitored(playerDtoProjectionList, statsList);
        playerMonitoredDtoList.forEach(playerMonitoredDto -> Analyse.analysePlayer(playerMonitoredDto, avgStack));

        PlayerMonitoredDto hero = remove(playerMonitoredDtoList, HERO);
        RecommendationDto recommendationDto = Analyse.analyseStack(hero.getStackOfPlayer(), avgStack,hero.getBlindsCount());

        return ModelTournamentMonitored.builder()
                .tournamentId(tournamentId)
                .handId(handId)
                .stackOfHero(hero.getStackOfPlayer())
                .minBlindsRecommendation(Analyse.MIN_BLINDS * hand.getBigBlind())
                .recommendation(recommendationDto.getRecommendation())
                .cssRecommendation(recommendationDto.getCss())
                .avgStack(avgStack)
                .blindsCount(hero.getBlindsCount())
                .playerMonitoredDtoList(playerMonitoredDtoList)
                .build();
    }

    private PlayerMonitoredDto remove(List<PlayerMonitoredDto> playerMonitoredDtoList, String hero) {
        int index = -1;
        for (int i = 0; i < playerMonitoredDtoList.size(); i++) {
            if (playerMonitoredDtoList.get(i).getNickname().equals(hero)) {
                index = i;
                break;
            }
        }
        PlayerMonitoredDto playerMonitoredDtoHero = playerMonitoredDtoList.get(index);
        playerMonitoredDtoList.remove(index);
        return playerMonitoredDtoHero;
    }

    private List<PlayerMonitoredDto> mergeIntoPlayerMonitored(List<PlayerDtoProjection> playerDtoProjectionList, List<StatsDto> statsList) {

        statsList.sort(Comparator.comparing(StatsDto::getPosition));
        Map<String, PlayerDtoProjection> playerDtoProjectionMap = toMap(playerDtoProjectionList);

        return
                statsList.stream()
                        .map(statsDto -> {
                            PlayerDtoProjection playerDtoProjection = playerDtoProjectionMap.get(statsDto.getNickname());
                            return
                                    PlayerMonitoredDto.builder()
                                            .nickname(statsDto.getNickname())
                                            .avgChen(playerDtoProjection.getAvgChen())
                                            .stackOfPlayer(statsDto.getStackOfPlayer())
                                            .blindsCount(statsDto.getBlindsCount())
                                            .noActionSeq(statsDto.getNoActionSeq())
                                            .noActionPerc(statsDto.getNoActionPerc())
                                            .foldSBSeq(statsDto.getFoldSBSeq())
                                            .foldSBPerc(statsDto.getFoldSBPerc())
                                            .foldBBSeq(statsDto.getFoldBBSeq())
                                            .foldBBPerc(statsDto.getFoldBBPerc())
                                            .actionBTNSeq(statsDto.getActionBTNSeq())
                                            .actionBTNPerc(statsDto.getActionBTNPerc())
                                            .showdowns(playerDtoProjection.getShowdowns())
                                            .totalHands(playerDtoProjection.getTotalHands())
                                            .showdownPerc(playerDtoProjection.getShowdownStat())
                                            .cards(playerDtoProjection.getCards())
                                            .build();
                        })
                        .collect(Collectors.toList());
    }

    private Map<String, PlayerDtoProjection> toMap(List<PlayerDtoProjection> playerDtoProjectionList) {
        return
                playerDtoProjectionList
                        .stream()
                        .collect(Collectors.toMap(PlayerDtoProjection::getNickname, Function.identity()));
    }

    public HandDtoProjection getHand(@NonNull Long handId) {
        return handConsolidationRepository.getHand(handId);
    }
}
