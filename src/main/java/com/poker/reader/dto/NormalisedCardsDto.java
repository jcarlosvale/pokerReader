package com.poker.reader.dto;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.poker.reader.parser.util.CardUtil.valueOf;

import com.google.common.base.Objects;
import com.poker.reader.parser.util.Chen;
import lombok.Data;

@Data
public class NormalisedCardsDto implements Comparable<NormalisedCardsDto> {
    private final String rawData;
    private final char card1;
    private final char card2;
    private final boolean suited;
    private final boolean pair;
    private final int chen;


    public NormalisedCardsDto(String rawData) {
        checkNotNull(rawData, "cards must be not null");
        checkArgument(rawData.length() > 4, "invalid format of rawdata " + rawData);
        this.rawData = rawData;
        if(valueOf(rawData.charAt(0)) >= valueOf(rawData.charAt(3))) {
            this.card1 = rawData.charAt(0);
            this.card2 = rawData.charAt(3);
        } else {
            this.card1 = rawData.charAt(3);
            this.card2 = rawData.charAt(0);
        }
        this.pair = card1 == card2;
        this.suited = rawData.charAt(1) == rawData.charAt(4);
        this.chen = Chen.calculateChenFormulaFrom(card1, card2, pair, suited);
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
        return card1 == that.card1 && card2 == that.card2 && suited == that.suited && pair == that.pair
                && chen == that.chen;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(rawData, card1, card2, suited, pair, chen);
    }
}
