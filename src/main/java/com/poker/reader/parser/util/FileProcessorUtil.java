package com.poker.reader.parser.util;

import com.poker.reader.dto.RawCardsDto;

import java.util.List;
import java.util.Map;

public class FileProcessorUtil {
    private FileProcessorUtil() {

    }

    public static int countHands(final Map<String, List<RawCardsDto>> handsOfPlayers) {
        int count = 0;
        for (List<RawCardsDto> hands: handsOfPlayers.values()) {
            count += hands.size();
        }
        return count;
    }
}
