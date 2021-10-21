package com.poker.reader.domain.service;

import com.poker.reader.domain.model.HandConsolidation;
import com.poker.reader.domain.repository.HandConsolidationRepository;
import com.poker.reader.view.rs.dto.PlayerDetailsDto;
import com.poker.reader.view.rs.dto.StatsDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class StatsService {

    private final HandConsolidationRepository handConsolidationRepository;

    public void loadStats(long tournamentId, long handId, List<PlayerDetailsDto> playerDetailsDtoList) {
        Map<String, List<HandConsolidation>>  handsFromPlayerMap =
                handConsolidationRepository
                        .getHandsFromPlayersUntilHandIdByTournamentOrderByNickname(handId, tournamentId)
                        .stream()
                        .collect(
                               Collectors.groupingBy(HandConsolidation::getNickname, HashMap::new, Collectors.toCollection(ArrayList::new))
                        );

        playerDetailsDtoList.forEach(playerDetailsDto -> playerDetailsDto.setStatsDto(
                loadStats(handsFromPlayerMap.get(playerDetailsDto.getPlayerDetailsDtoProjection().getNickname()))));
    }

    private StatsDto loadStats(List<HandConsolidation> handConsolidationsFromOnePlayerList) {

        int size = handConsolidationsFromOnePlayerList.size();
        int noActionCount = 0;
        int sbCount = 0;
        int bbCount = 0;
        int utgCount = 0;
        int mpCount = 0;
        int ljCount = 0;
        int hjCount = 0;
        int coCount = 0;
        int btnCount = 0;

        for(HandConsolidation hand : handConsolidationsFromOnePlayerList) {
            if (isNoAction(hand)) noActionCount++;
            switch (hand.getPokerPosition()) {
                case "SB":
                    sbCount++;
                    break;
                case "BB":
                    bbCount++;
                    break;
                case "UTG":
                    utgCount++;
                    break;
                case "MP":
                    mpCount++;
                    break;
                case "LJ":
                    ljCount++;
                    break;
                case "HJ":
                    hjCount++;
                    break;
                case "CO":
                    coCount++;
                    break;
                case "BTN":
                    btnCount++;
                    break;
            }
        }
        return
                StatsDto
                        .builder()
                        .noActionCount(noActionCount)
                        .noActionPerc(perc(noActionCount, size))
                        .sbCount(sbCount)
                        .bbCount(bbCount)
                        .utgCount(utgCount)
                        .mpCount(mpCount)
                        .ljCount(ljCount)
                        .hjCount(hjCount)
                        .coCount(coCount)
                        .btnCount(btnCount)
                        .build();
    }

    private String perc(int count, int size) {
        return ((100 * count / size) + "%");
    }

    private boolean isNoAction(HandConsolidation hand) {
        String foldRound = Objects.nonNull(hand.getFoldRound()) ? hand.getFoldRound() : "";
        boolean noBet = Objects.nonNull(hand.getNoBet()) ? hand.getNoBet() : false;

        return (foldRound.equals("PREFLOP") && noBet)
                ||
                (foldRound.equals("PREFLOP") && (hand.getPokerPosition().equals("SB") || hand.getPokerPosition().equals("BB")));
    }
}
