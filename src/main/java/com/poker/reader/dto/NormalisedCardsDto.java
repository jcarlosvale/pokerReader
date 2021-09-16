package com.poker.reader.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import lombok.Data;

@Data
public class NormalisedCardsDto implements Comparable<NormalisedCardsDto> {
    private final char card1;
    private final char card2;
    private final boolean suited;
    private final boolean pair;

    @JsonCreator
    public NormalisedCardsDto(
            @JsonProperty("card1") char card1,
            @JsonProperty("card2") char card2,
            @JsonProperty("suited") boolean suited,
            @JsonProperty("pair") boolean pair)
    {
        this.card1 = card1;
        this.card2 = card2;
        this.suited = suited;
        this.pair = pair;
    }

    @Override
    public int compareTo(NormalisedCardsDto other) {
        if (this.equals(other)) return 0;
        if (this.pair) {
            if (other.pair) {
                return other.card1 - this.card1;
            } else {
                return -1;
            }
        } else {
            if (other.pair) {
                return 1;
            } else {
                if(this.card1 == other.card1 && this.card2 == other.card2) { //iguais
                    if (this.suited) return -1;
                    return 1;
                } else {
                    if (this.card1 == other.card1) {
                        return other.card2 - this.card2;
                    } else {
                        return other.card1 - this.card1;
                    }
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NormalisedCardsDto that = (NormalisedCardsDto) o;
        return card1 == that.card1 && card2 == that.card2 && suited == that.suited && pair == that.pair;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(card1, card2, suited, pair);
    }
}
