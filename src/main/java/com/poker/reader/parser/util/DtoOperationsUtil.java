package com.poker.reader.parser.util;

import com.poker.reader.domain.util.Chen;
import com.poker.reader.dto.AnalysedPlayer;
import com.poker.reader.dto.NormalisedCardsDto;
import java.util.HashMap;
import java.util.List;
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

    public static Map<String, AnalysedPlayer> convertToMapOfAnalysedPlayersByPlayer(List<AnalysedPlayer> analysedPlayers) {
        Map<String, AnalysedPlayer> mapOfAnalysedPlayersByPlayer = new HashMap<>();
        analysedPlayers
                .forEach(analysedPlayer -> mapOfAnalysedPlayersByPlayer.put(analysedPlayer.getPlayer(), analysedPlayer));
        return mapOfAnalysedPlayersByPlayer;
    }

}
