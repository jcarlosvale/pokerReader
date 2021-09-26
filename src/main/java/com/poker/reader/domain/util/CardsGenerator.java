package com.poker.reader.domain.util;

import com.poker.reader.domain.model.Cards;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.poker.reader.domain.util.CardUtil.valueOf;
import static com.poker.reader.domain.util.Chen.calculateChenFormulaFrom;

public class CardsGenerator {

    public static Set<Cards> generateCards() {
        return
                generateAllHands().stream().map(CardsGenerator::generateCard).collect(Collectors.toSet());
    }

    private static Cards generateCard(String rawCard) {
        String card1;
        String card2;
        if(valueOf(rawCard.charAt(0)) >= valueOf(rawCard.charAt(3))) {
            card1 = String.valueOf(rawCard.charAt(0));
            card2 = String.valueOf(rawCard.charAt(3));
        } else {
            card1 = String.valueOf(rawCard.charAt(3));
            card2 = String.valueOf(rawCard.charAt(0));
        }
        boolean isPair = card1.equals(card2);
        boolean isSuited = rawCard.charAt(1) == rawCard.charAt(4);
        String suited = "";

        if (!isPair) {
            if (isSuited) suited = "s";
            else suited = "o";
        }

        String normalised = card1 + card2 + suited;

        return Cards
                .builder()
                .description(rawCard)
                .normalised(normalised)
                .card1(card1)
                .card2(card2)
                .suited(isSuited)
                .pair(isPair)
                .chen(Chen.calculateChenFormulaFrom(normalised))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private static List<String> generateAllHands() {
        List<String> naipes = List.of("c", "d", "h", "s");
        List<String> cards = List.of("2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A");
        List<String> generatedHand = new ArrayList<>();
        for(String card1 : cards) {
            for(String naipe1 : naipes) {
                String card_1 = card1 + naipe1;
                for(String card2 : cards) {
                    for(String naipe2 : naipes) {
                        String card_2 = card2 + naipe2;
                        if(card_1.equals(card_2)) continue;
                        generatedHand.add(card_1 + " " + card_2);
                    }
                }
            }
        }
        checkArgument(generatedHand.size() == 2652, "error generating cards");
        return generatedHand;
    }

    public static List<String> generateNormalisedCards() {
        List<String> cards = List.of("2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A");
        List<String> generateCards = new ArrayList<>();
        //PAIR
        for(String card : cards) {
            generateCards.add(card+card);
        }
        //combinations
        for (int i = cards.size()-1; i >= 0; i--) {
            for (int j = 0; j < i; j++) {
                generateCards.add(cards.get(i)+cards.get(j)+"o");
                generateCards.add(cards.get(i)+cards.get(j)+"s");
            }
        }
        return generateCards;
    }

    public static void printChenTable() {
        Map<Integer, List<String>> mapOfChen = new TreeMap<>();
        List<String> hands  = generateNormalisedCards();
        for(String hand: hands) {
            int chen = calculateChenFormulaFrom(hand);
            mapOfChen.computeIfAbsent(chen, s -> new ArrayList<>()).add(hand);
        }
        mapOfChen.forEach((integer, cards) -> System.out.println(integer + " --> " + cards));
    }

    public static Cards from(String description) {
        Cards cards = new Cards();
        cards.setDescription(description);
        cards.setCard1(String.valueOf(description.charAt(0)));
        cards.setCard2(String.valueOf(description.charAt(1)));
        cards.setSuited(description.length() > 2 && description.charAt(2) == 's');
        cards.setPair(cards.getCard1().equals(cards.getCard2()));
        cards.setChen(calculateChenFormulaFrom(description));
        cards.setCreatedAt(LocalDateTime.now());
        return cards;
    }

    public static void main(String[] args) {
        System.out.println(generateAllHands().size()); //2652
    }

}
