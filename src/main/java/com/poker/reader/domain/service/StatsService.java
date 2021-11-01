package com.poker.reader.domain.service;

import com.poker.reader.domain.model.HandConsolidation;
import com.poker.reader.domain.repository.HandConsolidationRepository;
import com.poker.reader.domain.util.Util;
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

    public List<StatsDto> loadStats(long tournamentId, long handId) {

        List<StatsDto> statsDtoList = new ArrayList<>();

        Map<String, List<HandConsolidation>> handsByPlayerMap = getHandsByPlayer(tournamentId, handId);

        handsByPlayerMap.forEach((nickname, handConsolidationList) -> statsDtoList.add(processStats(handConsolidationList)));

        return statsDtoList;
    }

    private HashMap<String, List<HandConsolidation>> getHandsByPlayer(long tournamentId, long handId) {
        return handConsolidationRepository
                .getHandsFromPlayersUntilHandIdByTournamentOrderByNickname(handId, tournamentId)
                .stream()
                .collect(
                        Collectors.groupingBy(HandConsolidation::getNickname, HashMap::new, Collectors.toCollection(ArrayList::new))
                );
    }

    private StatsDto processStats(List<HandConsolidation> handConsolidationsFromOnePlayerList) {
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
        long tournamentId = 0;
        long handId = 0;
        String level = null;
        String playedAt = null;
        String boardShowdown = null;
        String blinds = null;
        int pot = 0;
        Integer chen = null;
        String cards = null;
        boolean isButton = false;
        boolean isSmallBlind = false;
        boolean isBigBlind = false;
        int stackOfPlayer = 0;
        Integer blindsCount = 0;
        boolean isWinner = false;
        boolean isLose = false;
        String handDescription = null;
        String place = null;
        String pokerPosition = null;
        Integer position = null;
        String nickname = null;

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
            tournamentId = hand.getTournamentId();
            handId = hand.getHand();
            level = hand.getLevel();
            playedAt = Util.toLocalDatetime(hand.getPlayedAt());
            boardShowdown = hand.getBoard();
            blinds = hand.getSmallBlind() + "/" + hand.getBigBlind();
            pot = hand.getTotalPot();
            chen = hand.getChen();
            cards = hand.getCardsDescription();
            isButton = Objects.nonNull(hand.getPlace()) && hand.getPlace().equals("button");
            isSmallBlind = Objects.nonNull(hand.getPlace()) && hand.getPlace().equals("small blind");
            isBigBlind = Objects.nonNull(hand.getPlace()) && hand.getPlace().equals("big blind");
            stackOfPlayer = hand.getStackOfPlayer();
            blindsCount = hand.getStackOfPlayer() / hand.getBigBlind();
            isWinner = hand.getWinPot() != null;
            isLose = hand.getWinPot() == null;
            handDescription = hand.getWinHandDescription() != null ? hand.getWinHandDescription() : hand.getLoseHandDescription();
            place = hand.getPlace();
            pokerPosition = hand.getPokerPosition();
            position = hand.getPosition();
            nickname = hand.getNickname();
        }
        return
                StatsDto
                        .builder()
                        .tournamentId(tournamentId)
                        .handId(handId)
                        .level(level)
                        .playedAt(playedAt)
                        .boardShowdown(boardShowdown)
                        .blinds(blinds)
                        .pot(pot)
                        .chen(chen)
                        .cards(cards)
                        .isButton(isButton)
                        .isSmallBlind(isSmallBlind)
                        .isBigBlind(isBigBlind)
                        .stackOfPlayer(stackOfPlayer)
                        .blindsCount(blindsCount)
                        .isWinner(isWinner)
                        .isLose(isLose)
                        .handDescription(handDescription)
                        .place(place)
                        .pokerPosition(pokerPosition)
                        .position(position)
                        .nickname(nickname)
                        .total(size)
                        .noActionCount(noActionCount)
                        .noActionSeq(seqNoAction)
                        .noActionPerc(perc(noActionCount, size))
                        .foldSBCount(noActionSB)
                        .foldSBSeq(seqNoActionSB)
                        .foldSBPerc(perc(noActionSB, sbCount))
                        .foldBBCount(noActionBB)
                        .foldBBSeq(seqNoActionBB)
                        .foldBBPerc(perc(noActionBB, bbCount))
                        .actionBTNCount(actionBTN)
                        .actionBTNSeq(seqActionBTN)
                        .actionBTNPerc(perc(actionBTN, btnCount))
                        .sbCount(sbCount)
                        .bbCount(bbCount)
                        .utgCount(utgCount)
                        .mpCount(mpCount)
                        .ljCount(ljCount)
                        .hjCount(hjCount)
                        .coCount(coCount)
                        .btnCount(btnCount)
                        .labelNoActionMonitoring(seqNoAction + " " + perc(noActionCount, size))
                        .labelNoActionSBMonitoring(seqNoActionSB + " " + perc(noActionSB, sbCount))
                        .labelNoActionBBMonitoring(seqNoActionBB + " " + perc(noActionBB, bbCount))
                        .labelActionBTNMonitoring(seqActionBTN + " " + perc(actionBTN, btnCount))
                        .build();
    }

    private Integer perc(int count, int size) {
        if (size == 0) return null;
        return (100 * count / size);
        //return ((count + "/" + size) + " " + (100 * count / size) + "%");
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
