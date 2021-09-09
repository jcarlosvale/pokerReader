package com.poker.reader.analyser;

import com.poker.reader.dto.AnalysedPlayer;
import com.poker.reader.dto.RawCardsDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Analyse {

    private Analyse () {}

    public static List<AnalysedPlayer> handsOfPlayers(Set<String> players,
                                                      Map<String, List<RawCardsDto>> handsOfPlayers) {

        List<AnalysedPlayer> analysedPlayers = new ArrayList<>();

        for(String player: players) {
            AnalysedPlayer analysedPlayer = null;
            if (handsOfPlayers.containsKey(player)) {
                List<String> normalisedCards = normaliseCards(handsOfPlayers.get(player));
                normalisedCards.sort(Analyse::handsOfPlayersComparator);
                Collections.reverse(normalisedCards);

                List<Integer> chenValues = calculateChenFormulaFrom(normalisedCards);

                analysedPlayer = AnalysedPlayer.builder()
                        .player(player)
                        .rawCards(handsOfPlayers.get(player))
                        .normalisedCards(normalisedCards)
                        .chenValues(chenValues)
                        .chenAverage(Math.round(chenValues.stream().mapToInt(number-> number).average().orElseGet(() -> 0D)))
                        .build();

            } else { //NO HANDS
                analysedPlayer = AnalysedPlayer.builder()
                        .player(player)
                        .build();
            }
            analysedPlayers.add(analysedPlayer);
        }
        return analysedPlayers;
    }

    private static int handsOfPlayersComparator(String normalisedCards1, String normalisedCards2) {
        int card1 = valueOf(normalisedCards1.charAt(0));
        int card2 = valueOf(normalisedCards1.charAt(1));

        int card3 = valueOf(normalisedCards2.charAt(0));
        int card4 = valueOf(normalisedCards2.charAt(1));

        boolean isSuited1 = (normalisedCards1.length() == 3 && normalisedCards1.charAt(2) == 's');

        boolean isPair1 = card1 == card2;
        boolean isPair2 = card3 == card4;

        if (isPair1) {
            if (isPair2) {
                return card1 - card3;
            } else {
                return 1;
            }
        } else {
            if (isPair2) {
                return -1;
            } else {
                if(card1 == card3 && card2 == card4) { //iguais
                    if (isSuited1) return 1;
                    return -1;
                } else {
                    if (card1 == card3) {
                        return card2 - card4;
                    } else {
                        return card1 - card3;
                    }
                }
            }
        }


    }

    public static List<Integer> calculateChenFormulaFrom(List<String> normalisedCards) {
        List<Integer> result = new ArrayList<>();
        for(String hand: normalisedCards) {
            result.add(calculateChenFormulaFrom(hand));
        }
        return result;
    }

    /**
     * Formula https://www.thepokerbank.com/strategy/basic/starting-hand-selection/chen-formula/
     * @param hand
     * @return
     */
    public static int calculateChenFormulaFrom(String hand) {
        char card1 = hand.charAt(0);
        char card2 = hand.charAt(1);
        double chenValue = chenValueOf(card1);
        int gap = 0;
        if (card1 == card2) {
            if (card1 == '2') {
                chenValue = 5;
            } else {
                chenValue *= 2;
            }
        } else {
            gap = valueOf(card1) - valueOf(card2) - 1;
            if (gap == 3) {
                gap = 4;
            } else {
                if (gap >= 4) {
                    gap = 5;
                }
            }
            chenValue -= gap;

            if ((gap <= 1) && (valueOf(card1) < valueOf('Q'))) {
                chenValue += 1;
            }
        }

        if (hand.length() == 3 &&  hand.charAt(2) == 's') {
            chenValue += 2;
        }


        return (int) Math.round(chenValue);
    }

    private static double chenValueOf(char card) {
        if (card == 'T') return 5;
        if (card == 'J') return 6;
        if (card == 'Q') return 7;
        if (card == 'K') return 8;
        if (card == 'A') return 10;
        return (card - '0')/2.0;
    }

    private static List<String> normaliseCards(List<RawCardsDto> rawCardsDtoList) {
        List<String> result = new ArrayList<>();
        for(RawCardsDto rawCard: rawCardsDtoList) {

            char faceCard1 = faceCard(rawCard.getCard1());
            char faceCard2 = faceCard(rawCard.getCard2());

            if(faceCard1 == faceCard2) { //PAIR
                result.add(""+faceCard1 + faceCard2);
            } else { //NOT PAIR
                char suited = isSuited(rawCard.getCard1(), rawCard.getCard2()) ? 's' : 'o';
                if (valueOf(faceCard2) > valueOf(faceCard1)) {
                    result.add(""+faceCard2 + faceCard1 + suited);
                } else {
                    result.add(""+faceCard1 + faceCard2 + suited);
                }
            }
        }
        return result;
    }

    private static int valueOf(char faceCard) {
        if (faceCard == 'T') return 10;
        if (faceCard == 'J') return 11;
        if (faceCard == 'Q') return 12;
        if (faceCard == 'K') return 13;
        if (faceCard == 'A') return 14;
        return faceCard - '0';
    }

    private static boolean isSuited(String card1, String card2) {
        return suitOf(card1) == suitOf(card2);
    }

    private static char suitOf(String card) {
        return card.charAt(1);
    }

    private static char faceCard(String card) {
        return card.charAt(0);
    }
}
