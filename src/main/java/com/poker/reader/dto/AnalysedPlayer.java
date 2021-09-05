package com.poker.reader.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Data
@Builder
public class AnalysedPlayer {
    private String player;
    private List<RawCardsDto> rawCards;
    private List<String> normalisedCards;
    private List<Integer> chenValues;
    private long chenAverage;

    @Override
    public String toString() {
        StringBuilder results = new StringBuilder();
        results.append("\n");
        results.append(player).append(":");

        if (!CollectionUtils.isEmpty(normalisedCards)) {
            //normalised cards
            results.append("\n\t").append(normalisedCards)
                    .append("[").append(normalisedCards.size()).append("]");
            //chen
            results.append("\n\t").append(chenValues)
                    .append("[").append(chenValues.size()).append("]");
            //avg chen
            results.append("\n\t").append("Average: ")
                    .append(Math.round(chenValues.stream().mapToInt(number-> number).average().orElseGet(() -> 0D)));
            //raw cards
            results.append("\n\t").append(rawCards)
                    .append("[").append(rawCards.size()).append("]");
        } else { //NO HANDS
            results.append("\n\t").append("[0]");
        }
        return results.toString();
    }
}
