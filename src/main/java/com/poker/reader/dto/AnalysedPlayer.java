package com.poker.reader.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnalysedPlayer {
    private String player;
    private List<RawCardsDto> rawCards;
    private List<HandOfPlayerDto> hands;
    private long chenAverage;

    @Override
    public String toString() {
        return  "\n"
                + player + ":"
                //normalised cards
                + "\n\t" + hands.stream().map(handOfPlayerDto -> "(" + handOfPlayerDto.getCount() + ")" + handOfPlayerDto.getCards()).collect(Collectors.joining(", "))
                //avg chen
                + "\n\t" + "Chen Average: "
                + chenAverage
                //raw cards
                + "\n\t" + rawCards
                + "[" + rawCards.size() + "]";
    }
}
