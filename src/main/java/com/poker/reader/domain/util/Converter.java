package com.poker.reader.domain.util;

import com.poker.reader.domain.model.Cards;
import com.poker.reader.domain.model.Player;
import com.poker.reader.view.rs.dto.PlayerDto;
import java.util.ArrayList;
import java.util.List;

public class Converter {

    private Converter() {}

    public static PlayerDto toPlayerDto(Player player, List<Cards> cardsFromPlayer) {
        List<String> rawCardsList = new ArrayList<>();
        List<String> cardsList = new ArrayList<>();

        cardsFromPlayer.forEach(cards -> {
            rawCardsList.addAll(CardUtil.convertStringToList(cards.getRawCards()));
            cardsList.add(cards.getDescription());
        });

        String cards = CardUtil.convertListToString(cardsList);
        String rawCards = CardUtil.convertListToString(rawCardsList);

        return PlayerDto
                .builder()
                .nickname(player.getNickname())
                .totalHands(player.getTotalHands())
                .showdowns(player.getShowdowns())
                .showdownStat(player.getShowdowns() / player.getTotalHands() * 100)
                .avgChen(player.getAvgChen())
                .sumChen(player.getSumChen())
                .playedAt(player.getPlayedAt())
                .createdAt(player.getCreatedAt())
                .cards(cards)
                .rawCards(rawCards)
                .build();
    }
}
