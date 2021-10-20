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

        playerDetailsDtoList.forEach(playerDetailsDto -> playerDetailsDto.setStatsDto(loadStats(playerDetailsDto,
                handsFromPlayerMap.get(playerDetailsDto.getPlayerDetailsDtoProjection().getNickname()))));
    }

    private StatsDto loadStats(PlayerDetailsDto playerDetailsDto, List<HandConsolidation> handConsolidations) {

        int size = handConsolidations.size();
        int noActionCount = 0;
        int sbCount = 0;
        int bbCount = 0;
        int buttonCount = 0;

        for(HandConsolidation hand : handConsolidations) {
            if (isNoAction(hand, playerDetailsDto.getPokerTablePosition())) noActionCount++;
            if (isPlace(hand, "small blind")) sbCount++;
            if (isPlace(hand, "big blind")) bbCount++;
            if (isPlace(hand, "button")) buttonCount++;
        }

        return
                StatsDto
                        .builder()
                        .noActionCount(noActionCount)
                        .noActionPerc(perc(noActionCount, size))
                        .sbCount(sbCount)
                        .bbCount(bbCount)
                        .buttonCount(buttonCount)
                        .build();
    }

    private boolean isPlace(HandConsolidation hand, String place) {
        if (Objects.nonNull(hand.getPlace())) {
            return hand.getPlace().equals(place);
        }
        return false;
    }

    private String perc(int count, int size) {
        return ((100 * count / size) + "%");
    }

    private boolean isNoAction(HandConsolidation hand, String pokerTablePosition) {
        String foldRound = Objects.nonNull(hand.getFoldRound()) ? hand.getFoldRound() : "";
        boolean noBet = Objects.nonNull(hand.getNoBet()) ? hand.getNoBet() : false;

        return (foldRound.equals("PREFLOP") && noBet)
                ||
                (foldRound.equals("PREFLOP") && (pokerTablePosition.equals("SB") || pokerTablePosition.equals("BB")));
    }
}
