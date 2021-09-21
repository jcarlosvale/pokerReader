package com.poker.reader.domain.util;

import static com.poker.reader.domain.util.CardUtil.valueOf;

import com.poker.reader.domain.model.Cards;
import com.poker.reader.domain.model.Player;
import com.poker.reader.domain.model.Seat;
import com.poker.reader.view.rs.dto.PlayerDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Converter {

    private Converter() {}

    public static PlayerDto toPlayerDto(Player player, List<Seat> seatsFromPlayer) {
        List<String> rawCardsList = new ArrayList<>();
        List<String> cardsList = new ArrayList<>();
        int showDowns = 0;
        int avgChen = 0;

        for(Seat seat : seatsFromPlayer) {
            if (seat.getRawCards() != null) {
                rawCardsList.addAll(CardUtil.convertStringToList(seat.getRawCards()));
                avgChen += Chen.calculateChenFormulaFrom(seat.getCards().getDescription());
                cardsList.add(seat.getCards().getDescription());
                showDowns++;
            }
        }

        if (showDowns > 0) avgChen = avgChen / showDowns;
        else avgChen = 0;
        int showDownsStat = (int)(100.0 * showDowns / seatsFromPlayer.size());

        String cards = CardUtil.convertListToString(cardsList);
        String rawCards = CardUtil.convertListToString(rawCardsList);

        return PlayerDto
                .builder()
                .nickname(player.getNickname())
                .totalHands(seatsFromPlayer.size())
                .showdowns(showDowns)
                .showdownStat(showDownsStat)
                .avgChen(avgChen + "%")
                .playedAt(player.getPlayedAt())
                .createdAt(player.getCreatedAt())
                .cards(cards)
                .rawCards(rawCards)
                .build();
    }

    public static Cards toCard(String rawCard) {
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

        String description = card1 + card2 + suited;

        return Cards
                .builder()
                .description(description)
                .card1(card1)
                .card2(card2)
                .suited(isSuited)
                .pair(isPair)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
