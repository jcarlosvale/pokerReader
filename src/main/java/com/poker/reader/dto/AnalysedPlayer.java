package com.poker.reader.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnalysedPlayer {
    private final String player;

    @JsonDeserialize(keyUsing = NormalisedCardsDtoDeserializer.class)
    private final Map<NormalisedCardsDto, Integer> normalisedCardsMap;

    private final List<String> rawCards;

    @JsonCreator
    public AnalysedPlayer(
            @JsonProperty("player") String player,
            @JsonProperty("normalisedCardsMap") Map<NormalisedCardsDto, Integer> normalisedCardsMap,
            @JsonProperty("rawCards") List<String> rawCards) {
        this.player = player;
        this.normalisedCardsMap = normalisedCardsMap;
        this.rawCards = rawCards;
    }
}
