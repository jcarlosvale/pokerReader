package com.poker.reader.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Objects;
import com.poker.reader.parser.util.Chen;
import java.util.Map.Entry;
import java.util.TreeMap;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnalysedPlayer {
    private final String player;
    private final TreeMap<NormalisedCardsDto, Integer> normalisedCardsMap;

    public long averageChen() {
        int count = 0;
        double sum = 0;
        for(Entry<NormalisedCardsDto, Integer> entry : normalisedCardsMap.entrySet()) {
            count += entry.getValue();
            sum += entry.getKey().getChen() * entry.getValue();
        }
        if(count == 0) return Chen.MIN;
        else return Math.round(sum / count);
    }

    public int getCountShowdownCards() {
        return this.normalisedCardsMap.values().stream().mapToInt(value -> value).sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AnalysedPlayer that = (AnalysedPlayer) o;
        return Objects.equal(player, that.player) && normalisedCardsMap.equals(that.normalisedCardsMap);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(player, normalisedCardsMap);
    }
}
