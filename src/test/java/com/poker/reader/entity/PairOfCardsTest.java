package com.poker.reader.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PairOfCardsTest {

    @Test
    public void testCreateCardsOk(){
        PairOfCards expectedPairOfCards = new PairOfCards();
        expectedPairOfCards.setCard1('A');
        expectedPairOfCards.setCard2('K');

        PairOfCards actualPairOfCards = new PairOfCards();
        actualPairOfCards.setCard1('K');
        actualPairOfCards.setCard2('A');

        System.out.println(expectedPairOfCards.hashCode() + "  =  " + actualPairOfCards.hashCode());
        assertEquals(expectedPairOfCards, actualPairOfCards);

        expectedPairOfCards.setCard1('2');
        expectedPairOfCards.setCard2('T');

        actualPairOfCards.setCard1('T');
        actualPairOfCards.setCard2('2');

        System.out.println(expectedPairOfCards.hashCode() + "  =  " + actualPairOfCards.hashCode());
        assertEquals(expectedPairOfCards, actualPairOfCards);

        expectedPairOfCards.setCard1('6');
        expectedPairOfCards.setCard2(null);

        actualPairOfCards.setCard1(null);
        actualPairOfCards.setCard2('6');

        System.out.println(expectedPairOfCards.hashCode() + "  =  " + actualPairOfCards.hashCode());
        assertEquals(expectedPairOfCards, actualPairOfCards);
    }

}