package com.poker.reader.domain.service;

import static com.google.common.base.Preconditions.checkNotNull;

import com.poker.reader.domain.model.Hand;
import com.poker.reader.domain.model.PokerLine;
import com.poker.reader.domain.repository.HandConsolidationRepository;
import com.poker.reader.domain.repository.HandRepository;
import com.poker.reader.domain.repository.PlayerRepository;
import com.poker.reader.domain.repository.PokerLineRepository;
import com.poker.reader.domain.repository.TournamentRepository;
import com.poker.reader.domain.repository.projection.HandDtoProjection;
import com.poker.reader.domain.repository.projection.PlayerDtoProjection;
import com.poker.reader.domain.repository.projection.TournamentDtoProjection;
import com.poker.reader.view.rs.dto.PageDto;
import com.poker.reader.view.rs.dto.PlayerMonitoredDto;
import com.poker.reader.view.rs.dto.RecommendationDto;
import com.poker.reader.view.rs.dto.StatsDto;
import com.poker.reader.view.rs.model.ModelTournamentMonitored;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

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

        Page<TournamentDtoProjection> tournamentsDtoList = handConsolidationRepository.getAllTournamentsDto(pageable);

        return new PageImpl<>(tournamentsDtoList.getContent(), PageRequest.of(currentPage, pageSize), tournamentRepository.count());
    }

    public List<HandDtoProjection> getHandsFromTournament(Long tournamentId) {
        checkNotNull(tournamentId, "tournamentId must be not null");
        return handConsolidationRepository.getHandsFromTournament(tournamentId);
    }

    public String getRawDataFrom(Long handId) {
        return
            pokerLineRepository
                    .getAllByHandIdOrderByLineNumber(handId)
                    .stream()
                    .map(PokerLine::getLine)
                    .collect(Collectors.joining("<br>"));
    }

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

    public ModelTournamentMonitored getTournamentMonitoredModel(Long tournamentId) {

        long handId = handConsolidationRepository.getLasHandFromTournament(tournamentId);
        Hand hand = handRepository.getById(handId);

        List<PlayerMonitoredDto> playerMonitoredDtoList = getPlayerMonitoredListFromTournamentAndHand(tournamentId, handId);
        int avgStack = (int) playerMonitoredDtoList.stream().mapToInt(PlayerMonitoredDto::getStackOfPlayer).average().orElseThrow();

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

    private List<PlayerMonitoredDto> getPlayerMonitoredListFromTournamentAndHand(long tournamentId, long handId) {
        log.info("Retrieving tournament {} hand {}", tournamentId, handId);

        List<PlayerDtoProjection> playerDtoProjectionList = handConsolidationRepository.getPlayersDtoFromHand(handId);
        List<StatsDto> statsList = statsService.loadStats(tournamentId, handId);

        int avgStack = (int) statsList.stream().mapToInt(StatsDto::getStackOfPlayer).average().orElseThrow();
        List<PlayerMonitoredDto> playerMonitoredDtoList = mergeIntoPlayerMonitored(playerDtoProjectionList, statsList);
        playerMonitoredDtoList.forEach(playerMonitoredDto -> {
            Analyse.analyseCssPlayer(playerMonitoredDto, avgStack);
            Analyse.analyseTitlePlayer(playerMonitoredDto, avgStack);
        });

        return playerMonitoredDtoList;
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
                                            .avgChen(playerDtoProjection.getAvgChen())
                                            .showdowns(playerDtoProjection.getShowdowns())
                                            .totalHands(playerDtoProjection.getTotalHands())
                                            .showdownPerc(playerDtoProjection.getShowdownStat())
                                            .cards(playerDtoProjection.getCards())

                                            .nickname(statsDto.getNickname())
                                            .position(statsDto.getPosition())
                                            .sbCount(statsDto.getSbCount())
                                            .bbCount(statsDto.getBbCount())
                                            .btnCount(statsDto.getBtnCount())
                                            .chen(statsDto.getChen())
                                            .handDescription(statsDto.getHandDescription())
                                            .cardsOnHand(statsDto.getCards())
                                            .place(statsDto.getPlace())
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
                                            .actionBTNCount(statsDto.getActionBTNCount())
                                            .foldBBCount(statsDto.getFoldBBCount())
                                            .foldSBCount(statsDto.getFoldSBCount())
                                            .noActionCount(statsDto.getNoActionCount())
                                            .totalHandsTournament(statsDto.getTotal())
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

    public List<PlayerMonitoredDto> getPlayerMonitoredListFromTournamentAndHand(long handId) {
        Hand hand = handRepository.getById(handId);
        return getPlayerMonitoredListFromTournamentAndHand(hand.getTournament().getTournamentId(), hand.getHandId());
    }
}
