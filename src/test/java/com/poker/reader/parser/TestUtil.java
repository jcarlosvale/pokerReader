package com.poker.reader.parser;

import com.poker.reader.dto.AnalysedPlayer;
import com.poker.reader.dto.NormalisedCardsDto;
import com.poker.reader.parser.util.DtoOperationsUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestUtil {

    public static AnalysedPlayer mockAnalysedPlayer(String player, String ... cards) {
        Map<NormalisedCardsDto, Integer> map = new HashMap<>();
        List<String> rawCardsList = new ArrayList<>();
        for (String card : cards) {
            map.put(DtoOperationsUtil.toNormalisedCardsDto(card), 1);
            rawCardsList.add(card);
        }
        return new AnalysedPlayer(player, map, rawCardsList);
    }

}
