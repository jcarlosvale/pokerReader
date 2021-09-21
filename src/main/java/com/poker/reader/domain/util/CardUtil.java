package com.poker.reader.domain.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class CardUtil {

    private CardUtil() {}

    public static int valueOf(char faceCard) {
        if (faceCard == 'T') return 10;
        if (faceCard == 'J') return 11;
        if (faceCard == 'Q') return 12;
        if (faceCard == 'K') return 13;
        if (faceCard == 'A') return 14;
        return faceCard - '0';
    }

    public static int valueOf(String faceCard) {
        if (faceCard.equals("T")) return 10;
        if (faceCard.equals("J")) return 11;
        if (faceCard.equals("Q")) return 12;
        if (faceCard.equals("K")) return 13;
        if (faceCard.equals("A")) return 14;
        return faceCard.charAt(0) - '0';
    }

    public static List<String> convertStringToList(String rawCards) {
        checkNotNull(rawCards, "rawCards must not be null");
        return Arrays.stream(rawCards.split(", "))
                .collect(Collectors.toList());
    }

    public static String convertListToString(List<String> rawCards) {
        checkNotNull(rawCards, "rawCards must not be null");
        return String.join(", ", rawCards);
    }
}
