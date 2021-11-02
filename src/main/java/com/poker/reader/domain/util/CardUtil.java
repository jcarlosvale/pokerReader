package com.poker.reader.domain.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.poker.reader.domain.util.Chen.calculateChenFormulaFrom;

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

    public static List<String> convertStringToList(String cards) {
        checkNotNull(cards, "cards must not be null");
        return Arrays.stream(cards.split(", "))
                .collect(Collectors.toList());
    }

    public static String convertListToString(List<String> cards) {
        checkNotNull(cards, "cards must not be null");
        return String.join(", ", cards);
    }

    public static String sort(String stringListNormalisedCards) {
        if (stringListNormalisedCards == null) return null;
        List<String> listOfNormalisedCards = convertStringToList(stringListNormalisedCards);
        listOfNormalisedCards.sort((o1, o2) -> calculateChenFormulaFrom(o2) - calculateChenFormulaFrom(o1));
        return convertListToString(listOfNormalisedCards);
    }

    public static String extractBoardShowdownFrom(String board) {
        int countCards = board.split(" ").length;
        if (countCards == 5) return "RIVER";
        if (countCards == 4) return "TURN";
        if (countCards == 3) return "FLOP";
        return "";
    }
}
