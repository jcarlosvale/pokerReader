package com.poker.reader.parser.util;

import com.poker.reader.analyser.Analyse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HandGenerator {

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
            int chen = Analyse.calculateChenFormulaFrom(hand);
            mapOfChen.computeIfAbsent(chen, s -> new ArrayList<>()).add(hand);
        }
        mapOfChen.forEach((integer, cards) -> System.out.println(integer + " --> " + cards));
    }

    public static void main(String[] args) {
        List<String> cards  = generateNormalisedCards();
        System.out.println(cards.size());
        System.out.println(cards);
        int maxChen = Integer.MIN_VALUE;
        int minChen = Integer.MAX_VALUE;

        for(String card: cards) {
            int chen = Analyse.calculateChenFormulaFrom(card);
            System.out.println(card + ":" + chen);
            maxChen = Math.max(maxChen, chen);
            minChen = Math.min(minChen, chen);
        }
        System.out.println("MIN CHEN: " + minChen);
        System.out.println("MAX CHEN: " + maxChen);
        printChenTable();
    }

}
