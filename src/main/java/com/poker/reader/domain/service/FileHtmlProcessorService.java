package com.poker.reader.domain.service;

import static com.google.common.base.Preconditions.checkNotNull;

import com.poker.reader.domain.model.PokerLine;
import com.poker.reader.domain.repository.HandConsolidationRepository;
import com.poker.reader.domain.repository.HandRepository;
import com.poker.reader.domain.repository.PlayerRepository;
import com.poker.reader.domain.repository.PokerLineRepository;
import com.poker.reader.domain.repository.TournamentRepository;
import com.poker.reader.domain.repository.projection.HandDtoProjection;
import com.poker.reader.domain.repository.projection.PlayerDetailsDtoProjection;
import com.poker.reader.domain.repository.projection.PlayerDtoProjection;
import com.poker.reader.domain.repository.projection.TournamentDtoProjection;
import com.poker.reader.domain.util.CardUtil;
import com.poker.reader.view.rs.dto.HandDto;
import com.poker.reader.view.rs.dto.PageDto;
import com.poker.reader.view.rs.dto.PlayerDetailsDto;
import com.poker.reader.view.rs.dto.PlayerDto;
import com.poker.reader.view.rs.dto.PlayerMonitoredDto;
import com.poker.reader.view.rs.dto.RecommendationDto;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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

    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;
    private final HandRepository handRepository;
    private final PokerLineRepository pokerLineRepository;
    private final String HERO = "jcarlos.vale";
    private final HandConsolidationRepository handConsolidationRepository;


    public Page<PlayerDto> findPaginatedPlayers(Pageable pageable, boolean isMonitoring) {

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();

        List<PlayerDto> playerDtoList =
                handConsolidationRepository
                        .getAllPlayerDto(pageable)
                        .stream()
                        .map(playerDtoProjection -> toPlayerDto(playerDtoProjection, isMonitoring))
                        .collect(Collectors.toList());

        return new PageImpl<>(playerDtoList, PageRequest.of(currentPage, pageSize), playerRepository.count());
    }

    private PlayerDto toPlayerDto(PlayerDtoProjection playerDtoProjection, boolean isMonitoring) {
        String css = classNameFromChenValue(playerDtoProjection.getAvgChen());

        if (isMonitoring) {
            if (playerDtoProjection.getNickname().equals(HERO)) {
                css = "d-none";
            }
        }

        return
                PlayerDto.builder()
                        .nickname(playerDtoProjection.getNickname())
                        .totalHands(playerDtoProjection.getTotalHands())
                        .showdowns(playerDtoProjection.getShowdowns())
                        .showdownStat(playerDtoProjection.getShowdownStat())
                        .avgChen(playerDtoProjection.getAvgChen())
                        .createdAt(playerDtoProjection.getCreatedAt())
                        .cards(CardUtil.sort(playerDtoProjection.getCards()))
                        .rawCards(playerDtoProjection.getRawCards())
                        .css(css)
                        .build();
    }

    public Page<TournamentDtoProjection> findPaginatedTournaments(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();

        var tournamentsDtoList =
                handConsolidationRepository
                        .getAllTournamentsDto(pageable);

        return new PageImpl<>(tournamentsDtoList.getContent(), PageRequest.of(currentPage, pageSize), tournamentRepository.count());
    }

    private static String classNameFromChenValue(Integer avgChenValue) {
        if (avgChenValue == null) return "";
        if (avgChenValue >= 10) return "bg-primary";
        if (avgChenValue >= 8) return "bg-success";
        if (avgChenValue >= 5) return "table-warning";
        return "bg-danger";
    }

    public int calculateAvgStackFromLastHandOfTournament(Long tournamentId) {
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

    public HandDto extractHandDto(List<PlayerDetailsDto> playerDetailsDtoList) {
        if (playerDetailsDtoList.isEmpty()) return HandDto.builder().build();
        PlayerDetailsDtoProjection playerDetailsDtoProjection = playerDetailsDtoList.get(0).getPlayerDetailsDtoProjection();
        return HandDto
                .builder()
                .tournamentId(playerDetailsDtoProjection.getTournamentId())
                .handId(playerDetailsDtoProjection.getHandId())
                .level(playerDetailsDtoProjection.getLevel())
                .blinds(playerDetailsDtoProjection.getBlinds())
                .players(playerDetailsDtoList.size())
                .pot(playerDetailsDtoProjection.getPot())
                .board(playerDetailsDtoProjection.getBoard())
                .boardShowdown(playerDetailsDtoProjection.getBoardShowdown())
                .playedAt(playerDetailsDtoProjection.getPlayedAt())
                .build();
    }

    public String getRawDataFrom(Long handId) {
        return
            pokerLineRepository
                    .getAllByHandIdOrderByLineNumber(handId)
                    .stream()
                    .map(PokerLine::getLine)
                    .collect(Collectors.joining("<br>"));
    }

    public List<PlayerDetailsDto> getPlayersDetailsFromHand(Long handId) {

        List<PlayerDetailsDtoProjection> playerDetailsDtoProjectionList = handConsolidationRepository.getPlayersDetailsFromHand(handId);

        return
                playerDetailsDtoProjectionList
                        .stream()
                        .map(playerDetailsDtoProjection -> toPlayerDetailsDto(playerDetailsDtoProjection))
                        .collect(Collectors.toList());
    }

    private Optional<Integer> getPositionByPlace(List<PlayerDetailsDtoProjection> playerDetailsDtoProjectionList, String place) {
        return
                playerDetailsDtoProjectionList
                        .stream()
                        .filter(handConsolidation -> handConsolidation.getPlace() != null && handConsolidation.getPlace().equals(place))
                        .map(PlayerDetailsDtoProjection::getPosition)
                        .findFirst();
    }

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

    private String classNameFromWinnerOrLoser(PlayerDetailsDtoProjection playerDetailsDtoProjection) {
        if (playerDetailsDtoProjection.getIsWinner()) {
            return "table-success";
        }
        if (playerDetailsDtoProjection.getIsLose()) {
            return "table-danger";
        }
        return "table-warning";
    }

    public PlayerDto findPlayer(String nickname, boolean isMonitoring) {
        Optional<PlayerDtoProjection> player = handConsolidationRepository.getPlayerDtoByNickname(nickname);
        if (player.isPresent()) {
            return toPlayerDto(player.get(), isMonitoring);
        }
        return PlayerDto.builder().build();
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

    public RecommendationDto getRecommendation(List<PlayerMonitoredDto> playerMonitoredDtoList) {

        Optional<PlayerMonitoredDto> playerMonitoredDtoOptional =
                playerMonitoredDtoList.stream()
                        .filter(player -> player.getPlayerDto().getNickname().equals(HERO))
                        .findFirst();

        if (playerMonitoredDtoOptional.isPresent()) {

            PlayerMonitoredDto playerMonitoredDto = playerMonitoredDtoOptional.get();
            var tournamentId = playerMonitoredDto.getStackDtoProjection().getTournamentId();
            var avgStack = calculateAvgStackFromLastHandOfTournament(tournamentId);
            var stackFromHero = playerMonitoredDto.getStackDtoProjection().getStackOfPlayer();
            var blinds = playerMonitoredDto.getStackDtoProjection().getBlinds();
            return
                    RecommendationDto
                            .builder()
                            .nickname(playerMonitoredDto.getPlayerDto().getNickname())
                            .tournamentId(playerMonitoredDto.getStackDtoProjection().getTournamentId())
                            .handId(playerMonitoredDto.getStackDtoProjection().getHandId())
                            .minBlinds(15 * playerMonitoredDto.getStackDtoProjection().getBigBlind())
                            .avgStack(avgStack)
                            .stack(stackFromHero)
                            .blinds(blinds)
                            .recommendation(analyseStack(stackFromHero, avgStack, blinds))
                            .build();
        }
        return RecommendationDto.builder().build();
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

    public long getLastHandFromTournament(Long tournamentId) {
        return pokerLineRepository.getLastHandFromTournament(tournamentId);
    }
}
