package com.poker.reader.domain.util;

import static com.google.common.base.Preconditions.checkState;

import com.poker.reader.domain.model.Cards;
import com.poker.reader.domain.model.Player;
import java.util.List;
import lombok.NonNull;

public class Validations {

    private Validations() {}

    public static void validateCardsFromPlayer(@NonNull Player player, @NonNull List<Cards> cardsFromPlayer) {
        String nickname = player.getNickname();
        int totalCounter = 0;
        long totalChen = 0;
        for(Cards card : cardsFromPlayer) {
            checkState(card.getPlayer().equals(nickname),
                    "Inconsistent data retrieved nickname %s, %s",nickname , card.getPlayer());
            checkState(card.getCounter() == CardUtil.convertStringToList(card.getRawCards()).size(),
                    "Inconsistent data retrieved counter %s ==> %d", card.getRawCards(), card.getCounter());
            totalCounter += card.getCounter();
            totalChen += card.getChenValue();
        }
        checkState(totalCounter == player.getShowdowns(),
                "Inconsistent data showdowns counter %d, %d", totalCounter, player.getShowdowns());
        checkState(totalChen == player.getSumChen(),
                "Inconsistent data Chen values %d, %d", totalChen, player.getSumChen());
    }
}
