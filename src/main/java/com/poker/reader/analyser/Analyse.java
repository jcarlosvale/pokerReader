package com.poker.reader.analyser;

import com.poker.reader.dto.AnalysedPlayer;
import com.poker.reader.dto.HandOfPlayerDto;
import com.poker.reader.dto.RawCardsDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Analyse {

    private Analyse () {}

    public static List<AnalysedPlayer> handsOfPlayers(Set<String> players,
                                                      Map<String, List<RawCardsDto>> handsOfPlayers) {

        List<AnalysedPlayer> analysedPlayers = new ArrayList<>();

        for(String player: players) {
            AnalysedPlayer analysedPlayer;
            if (handsOfPlayers.containsKey(player)) {
                List<HandOfPlayerDto> normalisedCards = normaliseCards(player, handsOfPlayers.get(player));
                normalisedCards.sort(Analyse::handsOfPlayersComparator);
                Collections.reverse(normalisedCards);

                long chenAverage = calculateAverage(normalisedCards);

                analysedPlayer = AnalysedPlayer.builder()
                        .player(player)
                        .rawCards(handsOfPlayers.get(player))
                        .hands(normalisedCards)
                        .chenAverage(chenAverage)
                        .build();

            } else { //NO HANDS
                analysedPlayer = AnalysedPlayer.builder()
                        .player(player)
                        .rawCards(new ArrayList<>())
                        .hands(new ArrayList<>())
                        .chenAverage(-100)
                        .build();
            }
            analysedPlayers.add(analysedPlayer);
        }
        return analysedPlayers;
    }

    private static long calculateAverage(List<HandOfPlayerDto> normalisedCards) {

        int totalCount = normalisedCards.stream().mapToInt(HandOfPlayerDto::getCount).sum();
        double sum = normalisedCards.stream().mapToDouble(handOfPlayer -> handOfPlayer.getCount() * handOfPlayer.getChen()).sum();

        if(totalCount == 0) return -100;
        else return Math.round(sum / totalCount);

    }

    private static int handsOfPlayersComparator(HandOfPlayerDto handOfPlayer1, HandOfPlayerDto handOfPlayer2) {

        String normalisedCards1 = handOfPlayer1.getCards();
        String normalisedCards2 = handOfPlayer2.getCards();

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

    private static List<HandOfPlayerDto> normaliseCards(String player, List<RawCardsDto> rawCardsDtoList) {

        Map<String, Integer> mapNormalisedCards = new HashMap<>();

        for(RawCardsDto rawCard: rawCardsDtoList) {
            String normalisedCards = normaliseCards(rawCard);
            mapNormalisedCards.put(normalisedCards, mapNormalisedCards.getOrDefault(normalisedCards, 0)+1);
        }
        List<HandOfPlayerDto> handOfPlayerDtoList = new ArrayList<>();
        mapNormalisedCards.forEach((cards, counter) -> handOfPlayerDtoList.add(
                HandOfPlayerDto.builder()
                        .cards(cards)
                        .chen(calculateChenFormulaFrom(cards))
                        .count(counter)
                        .build()));
        return handOfPlayerDtoList;
    }

    private static String normaliseCards(RawCardsDto rawCard) {
        char faceCard1 = faceCard(rawCard.getCard1());
        char faceCard2 = faceCard(rawCard.getCard2());

        if(faceCard1 == faceCard2) { //PAIR
            return ""+faceCard1 + faceCard2;
        } else { //NOT PAIR
            char suited = isSuited(rawCard.getCard1(), rawCard.getCard2()) ? 's' : 'o';
            if (valueOf(faceCard2) > valueOf(faceCard1)) {
                return ""+faceCard2 + faceCard1 + suited;
            } else {
                return ""+faceCard1 + faceCard2 + suited;
            }
        }
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
