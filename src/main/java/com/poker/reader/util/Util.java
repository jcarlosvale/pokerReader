package com.poker.reader.util;

import com.poker.reader.entity.PairOfCards;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public class Util {
    private final static Map<Character, Integer> faceValuesMap = new HashMap<>();
    private final static Map<PairOfCards, PairOfCards> cardsNormalized = new HashMap<>();

    static {
        faceValuesMap.put('T', 10);
        faceValuesMap.put('J', 11);
        faceValuesMap.put('K', 12);
        faceValuesMap.put('Q', 13);
        faceValuesMap.put('A', 14);

        for (char c = '2'; c <= '9'; c++) {
            faceValuesMap.put(c, c - '0');
        }
    }

    static {
        String faceCards = "AKQJT98765432";
        long id = 1L;
        //pairs
        for (int i = 0; i < faceCards.length(); i++) {
            PairOfCards pairOfCards1 =
                    PairOfCards
                            .builder()
                            .card1(faceCards.charAt(i))
                            .card2(faceCards.charAt(i))
                            .isSuited(false)
                            .id(id)
                            .build();
            cardsNormalized.put(pairOfCards1, pairOfCards1);
            id++;
        }

        //null cases
        for (int i = 0; i < faceCards.length(); i++) {
            PairOfCards pairOfCards1 =
                    PairOfCards
                            .builder()
                            .card1(faceCards.charAt(i))
                            .card2(null)
                            .isSuited(false)
                            .id(id)
                            .build();

            PairOfCards pairOfCards2 = PairOfCards
                    .builder()
                    .card1(null)
                    .card2(faceCards.charAt(i))
                    .isSuited(false)
                    .id(id)
                    .build();

            cardsNormalized.put(pairOfCards1, pairOfCards1);
            cardsNormalized.put(pairOfCards2, pairOfCards1);

            id++;
        }

        //complete cases suited and not suited
        for (int i = 0; i < faceCards.length(); i++) {
            for (int j = i + 1; j < faceCards.length(); j++) {
                PairOfCards pairOfCards1 =
                        PairOfCards
                                .builder()
                                .card1(faceCards.charAt(i))
                                .card2(faceCards.charAt(j))
                                .isSuited(false)
                                .id(id)
                                .build();

                PairOfCards pairOfCards2 = PairOfCards
                        .builder()
                        .card1(faceCards.charAt(j))
                        .card2(faceCards.charAt(i))
                        .isSuited(false)
                        .id(id)
                        .build();

                cardsNormalized.put(pairOfCards1, pairOfCards1);
                cardsNormalized.put(pairOfCards2, pairOfCards1);
                id++;

                pairOfCards1 =
                        PairOfCards
                                .builder()
                                .card1(faceCards.charAt(i))
                                .card2(faceCards.charAt(j))
                                .isSuited(true)
                                .id(id)
                                .build();

                pairOfCards2 = PairOfCards
                        .builder()
                        .card1(faceCards.charAt(j))
                        .card2(faceCards.charAt(i))
                        .isSuited(true)
                        .id(id)
                        .build();

                cardsNormalized.put(pairOfCards1, pairOfCards1);
                cardsNormalized.put(pairOfCards2, pairOfCards1);
                id++;
            }
        }
        log.debug("LOADED NORMALIZED CARDS");
    }

    public static int faceValue(Character card) {
        if (card == null) {
            return Integer.MIN_VALUE;
        } else {
            return faceValuesMap.get(card);
        }
    }

    public static PairOfCards normalizedCard(PairOfCards pairOfCards) {
        return cardsNormalized.get(pairOfCards);
    }
}
