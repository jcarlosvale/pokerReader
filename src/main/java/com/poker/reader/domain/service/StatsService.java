package com.poker.reader.domain.service;

import com.poker.reader.domain.model.HandConsolidation;
import com.poker.reader.domain.repository.HandConsolidationRepository;
import com.poker.reader.domain.util.Util;
import com.poker.reader.view.rs.dto.StatsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        Integer position = null;
        String nickname = null;

        for(HandConsolidation hand : handConsolidationsFromOnePlayerList) {

            boolean isNoAction = isNoAction(hand);
            place = hand.getPlace();

            if (isNoAction){
                noActionCount++;
                seqNoAction++;
                if (isPlace(place,"small blind")) {
                    noActionSB++;
                    seqNoActionSB++;
                }
                else if (isPlace(place,"big blind")) {
                    noActionBB++;
                    seqNoActionBB++;
                }
                else if (isPlace(place,"button")) {
                    seqActionBTN = 0;
                }
            } else {
                seqNoAction = 0;
                if (isPlace(place,"small blind")) {
                    seqNoActionSB = 0;
                }
                else if (isPlace(place,"big blind")) {
                    seqNoActionBB = 0;
                }
                else if (isPlace(place,"button")) {
                    actionBTN++;
                    seqActionBTN++;
                }
            }

            if (isPlace(place,"small blind")) {
                sbCount++;
            }
            else if (isPlace(place,"big blind")) {
                bbCount++;
            }
            else if (isPlace(place,"button")) {
                btnCount++;
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
            isButton = "button".equals(hand.getPlace());
            isSmallBlind ="small blind".equals(hand.getPlace());
            isBigBlind = "big blind".equals(hand.getPlace());
            stackOfPlayer = hand.getStackOfPlayer();
            blindsCount = hand.getStackOfPlayer() / hand.getBigBlind();
            isWinner = hand.getWinPot() != null;
            isLose = hand.getWinPot() == null;
            handDescription = hand.getWinHandDescription() != null ? hand.getWinHandDescription() : hand.getLoseHandDescription();
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
                        .btnCount(btnCount)
                        .labelNoAction(seqNoAction + " " + perc(noActionCount, size))
                        .labelFoldSB(seqNoActionSB + " " + perc(noActionSB, sbCount))
                        .labelFoldBB(seqNoActionBB + " " + perc(noActionBB, bbCount))
                        .labelActionBTN(seqActionBTN + " " + perc(actionBTN, btnCount))
                        .build();
    }

    private boolean isPlace(String place, String position) {
        return Objects.nonNull(place) && place.equals(position);
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
                (foldRound.equals("PREFLOP") && ("small blind".equals(hand.getPlace()) || "big blind".equals(hand.getPlace())));
    }
}
