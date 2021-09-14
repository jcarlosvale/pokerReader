package com.poker.reader.backup.parser.util;

import com.poker.reader.dto.NormalisedCardsDto;

import java.util.List;
import java.util.Map;

public class FileProcessorUtil {
    private FileProcessorUtil() {

    }

    public static int countHands(final Map<String, List<NormalisedCardsDto>> handsOfPlayers) {
        int count = 0;
        for (List<NormalisedCardsDto> hands: handsOfPlayers.values()) {
            count += hands.size();
        }
        return count;
    }
}
