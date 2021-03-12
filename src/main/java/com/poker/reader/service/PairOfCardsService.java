package com.poker.reader.service;

import com.poker.reader.dto.HoldCards;
import com.poker.reader.entity.PairOfCards;
import com.poker.reader.repository.PairOfCardsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class PairOfCardsService {
    private final static Map<String, PairOfCards> cardsNormalized = new HashMap<>();
    private final PairOfCardsRepository pairOfCardsRepository;

    public void load() {
        if (cardsNormalized.isEmpty()) {
            generateCombinations();
            if (pairOfCardsRepository.count() == 0) {
                pairOfCardsRepository.saveAll(cardsNormalized.values());
                pairOfCardsRepository.flush();
            }
        }
    }

    //Expected 2652 combinations + 52 (card1 + null) = 2704
    private Map<String, PairOfCards> generateCombinations() {
        String [] faceCards = new String[]{"A","K","Q","J","T","9","8","7","6","5","4","3","2"}; //13
        String [] suits = new String[]{"d","c","h","s"}; //4
        String [] cards = new String[suits.length * faceCards.length];

        //building cards = 52
        int count = 0;
        for (String faceCard: faceCards) {
            for (String suit : suits) {
                cards[count++] = faceCard+suit;
            }
        }

        //combinations
        for (int  i= 0;  i < cards.length ; i++) {
            for (int j = 0; j < cards.length; j++) {
                if (i!=j) {
                    PairOfCards pairOfCards = PairOfCards.builder().build();
                    String pairOfCardId;
                    if(faceValue(cards[i]) > faceValue(cards[j])) {
                        pairOfCards.setCard1(cards[i].charAt(0));
                        pairOfCards.setCard2(cards[j].charAt(0));
                        pairOfCardId = cards[i].charAt(0) + "" + cards[j].charAt(0) + "" + getSuited(cards[i], cards[j]);
                    } else {
                        pairOfCards.setCard1(cards[j].charAt(0));
                        pairOfCards.setCard2(cards[i].charAt(0));
                        pairOfCardId = cards[j].charAt(0) + "" + cards[i].charAt(0) + "" + getSuited(cards[i], cards[j]);
                    }
                    String id = cards[i] + cards[j];
                    pairOfCards.setId(pairOfCardId);
                    pairOfCards.setIsSuited(cards[i].charAt(1) == cards[j].charAt(1));
                    cardsNormalized.put(id, pairOfCards);
                }
            }
        }
        //null values
        for (String s: cards) {
            cardsNormalized.put(s, PairOfCards.builder().id(Character.toString(s.charAt(0))).card1(s.charAt(0)).isSuited(false).build());
        }

        log.debug("LOADED NORMALIZED CARDS");
        return cardsNormalized;
    }

    private String getSuited(String card1, String card2) {
        if(card1.charAt(1) == card2.charAt(1)) {
            return "s";
        } else {
            if(card1.charAt(0) != card2.charAt(0)) { //is not pair
                return "o";
            }
        }
        return "";
    }

    public PairOfCards findOrPersist(HoldCards holdCards) {
        if (Objects.isNull(holdCards)) return null;
        log.debug("Pair Of Cards {}", holdCards);
        load();
        return fromHoldCards(holdCards.getCard1(), holdCards.getCard2());
    }

    private PairOfCards fromHoldCards(String card1, String card2) {
        if (Objects.isNull(card1) && Objects.isNull(card2)) {
            return null;
        }
        String id;
        if (Objects.isNull(card1)) {
            id = card2;
        } else {
            if (Objects.isNull(card2)) {
                id = card1;
            } else {
                id = card1 + card2;
            }
        }
        return cardsNormalized.get(id);
    }

    private int faceValue(String card) {
        if (card == null) {
            return Integer.MIN_VALUE;
        } else {
            char c = card.charAt(0);
            switch (c) {
                case 'T': return 10;
                case 'J': return 11;
                case 'K': return 12;
                case 'Q': return 13;
                case 'A': return 14;
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    return c - '0';
                default:
                    return Integer.MIN_VALUE;
            }
        }
    }
}
