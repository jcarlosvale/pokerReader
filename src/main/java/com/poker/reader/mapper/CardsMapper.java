package com.poker.reader.mapper;

import com.poker.reader.dto.HoldCards;
import com.poker.reader.entity.PairOfCards;
import com.poker.reader.util.Util;

public class CardsMapper {
    public static PairOfCards fromHoldCards(HoldCards holdCards) {
        boolean isSuited = false;
        if ((holdCards.getCard1() != null) &&
            (holdCards.getCard2() != null) &&
            (holdCards.getCard1().length() > 1) &&
            (holdCards.getCard2().length() > 1)) {
            isSuited = holdCards.getCard1().charAt(1) == holdCards.getCard2().charAt(1);
        }
        Character card1 = (holdCards.getCard1() != null) ? holdCards.getCard1().charAt(0) : null;
        Character card2 = (holdCards.getCard2() != null) ? holdCards.getCard2().charAt(0) : null;

        PairOfCards temp = PairOfCards.builder().card1(card1).card2(card2).isSuited(isSuited).build();
        return Util.normalizedCard(temp);
    }
}
