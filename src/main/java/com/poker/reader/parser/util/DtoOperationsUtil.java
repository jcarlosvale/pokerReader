package com.poker.reader.parser.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.poker.reader.parser.util.CardUtil.valueOf;

import com.poker.reader.dto.NormalisedCardsDto;
import java.util.Map;
import java.util.Map.Entry;
import lombok.NonNull;

public class DtoOperationsUtil {

    private DtoOperationsUtil() {}

    public static long getAverageChen(@NonNull Map<NormalisedCardsDto, Integer> normalisedCardsMap) {
        int count = 0;
        double sum = 0;
        for(Entry<NormalisedCardsDto, Integer> entry : normalisedCardsMap.entrySet()) {
            count += entry.getValue();
            sum += Chen.calculateChenFormulaFrom(entry.getKey()) * entry.getValue();
        }
        if(count == 0) return Chen.MIN;
        else return Math.round(sum / count);
    }

    public static int getCountShowdownCards(@NonNull Map<NormalisedCardsDto, Integer> normalisedCardsMap) {
        return normalisedCardsMap.values().stream().mapToInt(value -> value).sum();
    }

    public static NormalisedCardsDto toNormalisedCardsDto(@NonNull String rawData) {
        checkArgument(rawData.length() > 4, "invalid format of rawdata " + rawData);
        char card1;
        char card2;
        if(valueOf(rawData.charAt(0)) >= valueOf(rawData.charAt(3))) {
            card1 = rawData.charAt(0);
            card2 = rawData.charAt(3);
        } else {
            card1 = rawData.charAt(3);
            card2 = rawData.charAt(0);
        }
        boolean pair = card1 == card2;
        boolean suited = rawData.charAt(1) == rawData.charAt(4);

        return new NormalisedCardsDto(card1, card2, suited, pair);
    }

}
