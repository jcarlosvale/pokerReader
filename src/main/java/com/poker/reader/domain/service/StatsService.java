package com.poker.reader.domain.service;

import com.poker.reader.domain.model.HandConsolidation;
import com.poker.reader.domain.repository.HandConsolidationRepository;
import com.poker.reader.view.rs.dto.PlayerDetailsDto;
import com.poker.reader.view.rs.dto.StatsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
        int seqNoAction = 0;
        int noActionSB = 0;
        int seqNoActionSB = 0;
        int noActionBB = 0;
        int seqNoActionBB = 0;
        int actionBTN = 0;
        int seqActionBTN = 0;
        int sbCount = 0;
        int bbCount = 0;
        int utgCount = 0;
        int mpCount = 0;
        int ljCount = 0;
        int hjCount = 0;
        int coCount = 0;
        int btnCount = 0;

        for(HandConsolidation hand : handConsolidationsFromOnePlayerList) {
//            if (hand.getHand() == 222698160975L && hand.getNickname().equals("GutPewPew")) {
//                log.info("debug");
//            }
            if (isNoAction(hand)){
                noActionCount++;
                seqNoAction++;
                switch (hand.getPokerPosition()) {
                    case "SB":
                        noActionSB++;
                        seqNoActionSB++;
                        break;
                    case "BB":
                        noActionBB++;
                        seqNoActionBB++;
                        break;
                    case "BTN":
                        seqActionBTN = 0;
                        break;
                }
            } else {
                seqNoAction = 0;
                switch (hand.getPokerPosition()) {
                    case "SB":
                        seqNoActionSB = 0;
                        break;
                    case "BB":
                        seqNoActionBB = 0;
                        break;
                    case "BTN":
                        actionBTN++;
                        seqActionBTN++;
                        break;
                }
            }
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
                        .total(size)
                        .noActionCount(noActionCount)
                        .seqNoAction(seqNoAction)
                        .noActionPerc(perc(noActionCount, size))
                        .noActionSB(noActionSB)
                        .seqNoActionSB(seqNoActionSB)
                        .percNoActionSB(perc(noActionSB, sbCount))
                        .noActionBB(noActionBB)
                        .seqNoActionBB(seqNoActionBB)
                        .percNoActionBB(perc(noActionBB, bbCount))
                        .actionBTN(actionBTN)
                        .seqActionBTN(seqActionBTN)
                        .percActionBTN(perc(actionBTN, btnCount))
                        .sbCount(sbCount)
                        .bbCount(bbCount)
                        .utgCount(utgCount)
                        .mpCount(mpCount)
                        .ljCount(ljCount)
                        .hjCount(hjCount)
                        .coCount(coCount)
                        .btnCount(btnCount)
                        .labelNoActionMonitoring(perc(noActionCount, size))
                        .labelNoActionSBMonitoring(perc(noActionSB, sbCount))
                        .labelNoActionBBMonitoring(perc(noActionBB, bbCount))
                        .labelActionBTNMonitoring(perc(actionBTN, btnCount))
                        .build();
    }

    private String perc(int count, int size) {
        if (size == 0) return null;
        //return ((count + "/" + size) + " " + (100 * count / size) + "%");
        return (100 * count / size) + "%";
    }

    //TODO: need to fix, example http://localhost:8080/hand/222698059143
    private boolean isNoAction(HandConsolidation hand) {
        String foldRound = Objects.nonNull(hand.getFoldRound()) ? hand.getFoldRound() : "";
        boolean noBet = Objects.nonNull(hand.getNoBet()) ? hand.getNoBet() : false;

        return (foldRound.equals("PREFLOP") && noBet)
                ||
                (foldRound.equals("PREFLOP") && (hand.getPokerPosition().equals("SB") || hand.getPokerPosition().equals("BB")));
    }
}
