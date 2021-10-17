package com.poker.reader.domain.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.poker.reader.domain.util.Chen.calculateChenFormulaFrom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static Map<Integer, String> getMapOfPosition(int numberOfPlayers, Integer buttonPosition, Integer smallBlindPosition,
            Integer bigBlindPosition) {
        Map<Integer, String> mapOfPosition = new HashMap<>();
        mapOfPosition.put(bigBlindPosition, "BB");
        if (numberOfPlayers == 2) {
            mapOfPosition.put(buttonPosition, "SB, BTN");
        } else {
            mapOfPosition.put(buttonPosition, "BTN");
            mapOfPosition.put(smallBlindPosition, "SB");
            if (numberOfPlayers == 4) {
                mapOfPosition.put(bigBlindPosition + 1 <= numberOfPlayers ?  bigBlindPosition + 1 : (bigBlindPosition + 1) % numberOfPlayers, "CO");
            }
            if (numberOfPlayers == 5) {
                mapOfPosition.put(bigBlindPosition + 1 <= numberOfPlayers ?  bigBlindPosition + 1 : (bigBlindPosition + 1) % numberOfPlayers, "HJ");
                mapOfPosition.put(bigBlindPosition + 2 <= numberOfPlayers ?  bigBlindPosition + 2 : (bigBlindPosition + 2) % numberOfPlayers, "CO");
            }
            if (numberOfPlayers == 6) {
                mapOfPosition.put(bigBlindPosition + 1 <= numberOfPlayers ?  bigBlindPosition + 1 : (bigBlindPosition + 1) % numberOfPlayers, "UTG");
                mapOfPosition.put(bigBlindPosition + 2 <= numberOfPlayers ?  bigBlindPosition + 2 : (bigBlindPosition + 2) % numberOfPlayers, "HJ");
                mapOfPosition.put(bigBlindPosition + 3 <= numberOfPlayers ?  bigBlindPosition + 3 : (bigBlindPosition + 3) % numberOfPlayers, "CO");
            }
            if (numberOfPlayers == 7) {
                mapOfPosition.put(bigBlindPosition + 1 <= numberOfPlayers ?  bigBlindPosition + 1 : (bigBlindPosition + 1) % numberOfPlayers, "UTG");
                mapOfPosition.put(bigBlindPosition + 2 <= numberOfPlayers ?  bigBlindPosition + 2 : (bigBlindPosition + 2) % numberOfPlayers, "MP");
                mapOfPosition.put(bigBlindPosition + 3 <= numberOfPlayers ?  bigBlindPosition + 3 : (bigBlindPosition + 3) % numberOfPlayers, "HJ");
                mapOfPosition.put(bigBlindPosition + 4 <= numberOfPlayers ?  bigBlindPosition + 4 : (bigBlindPosition + 4) % numberOfPlayers, "CO");
            }
            if (numberOfPlayers == 8) {
                mapOfPosition.put(bigBlindPosition + 1 <= numberOfPlayers ?  bigBlindPosition + 1 : (bigBlindPosition + 1) % numberOfPlayers, "UTG");
                mapOfPosition.put(bigBlindPosition + 2 <= numberOfPlayers ?  bigBlindPosition + 2 : (bigBlindPosition + 2) % numberOfPlayers, "UTG");
                mapOfPosition.put(bigBlindPosition + 3 <= numberOfPlayers ?  bigBlindPosition + 3 : (bigBlindPosition + 3) % numberOfPlayers, "MP");
                mapOfPosition.put(bigBlindPosition + 4 <= numberOfPlayers ?  bigBlindPosition + 4 : (bigBlindPosition + 4) % numberOfPlayers, "HJ");
                mapOfPosition.put(bigBlindPosition + 5 <= numberOfPlayers ?  bigBlindPosition + 5 : (bigBlindPosition + 5) % numberOfPlayers, "CO");
            }
            if (numberOfPlayers == 9) {
                mapOfPosition.put(bigBlindPosition + 1 <= numberOfPlayers ?  bigBlindPosition + 1 : (bigBlindPosition + 1) % numberOfPlayers , "UTG");
                mapOfPosition.put(bigBlindPosition + 2 <= numberOfPlayers ?  bigBlindPosition + 2 : (bigBlindPosition + 2) % numberOfPlayers, "UTG");
                mapOfPosition.put(bigBlindPosition + 3 <= numberOfPlayers ?  bigBlindPosition + 3 : (bigBlindPosition + 3) % numberOfPlayers, "MP");
                mapOfPosition.put(bigBlindPosition + 4 <= numberOfPlayers ?  bigBlindPosition + 4 : (bigBlindPosition + 4) % numberOfPlayers, "MP");
                mapOfPosition.put(bigBlindPosition + 5 <= numberOfPlayers ?  bigBlindPosition + 5 : (bigBlindPosition + 5) % numberOfPlayers, "HJ");
                mapOfPosition.put(bigBlindPosition + 6 <= numberOfPlayers ?  bigBlindPosition + 6 : (bigBlindPosition + 6) % numberOfPlayers, "CO");
            }
            if (numberOfPlayers == 10) {
                mapOfPosition.put(bigBlindPosition + 1 <= numberOfPlayers ?  bigBlindPosition + 1 : (bigBlindPosition + 1) % numberOfPlayers , "UTG");
                mapOfPosition.put(bigBlindPosition + 2 <= numberOfPlayers ?  bigBlindPosition + 2 : (bigBlindPosition + 2) % numberOfPlayers, "UTG");
                mapOfPosition.put(bigBlindPosition + 3 <= numberOfPlayers ?  bigBlindPosition + 3 : (bigBlindPosition + 3) % numberOfPlayers, "UTG");
                mapOfPosition.put(bigBlindPosition + 4 <= numberOfPlayers ?  bigBlindPosition + 4 : (bigBlindPosition + 4) % numberOfPlayers, "MP");
                mapOfPosition.put(bigBlindPosition + 5 <= numberOfPlayers ?  bigBlindPosition + 5 : (bigBlindPosition + 5) % numberOfPlayers, "MP");
                mapOfPosition.put(bigBlindPosition + 6 <= numberOfPlayers ?  bigBlindPosition + 6 : (bigBlindPosition + 6) % numberOfPlayers, "HJ");
                mapOfPosition.put(bigBlindPosition + 7 <= numberOfPlayers ?  bigBlindPosition + 7 : (bigBlindPosition + 7) % numberOfPlayers, "CO");
            }
        }

        return mapOfPosition;
    }
}
