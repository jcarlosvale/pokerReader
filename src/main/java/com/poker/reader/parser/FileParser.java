package com.poker.reader.parser;

import com.poker.reader.entity.*;
import com.poker.reader.parser.util.FileParserUtil;
import org.apache.commons.lang3.StringUtils;

import static com.poker.reader.parser.util.FileParserUtil.*;
import static com.poker.reader.parser.util.Tokens.*;

public class FileParser {

    public Tournament extractTournament(String line){
        return Tournament
                        .builder()
                        .id(extractLong(line, START_TOURNAMENT, END_TOURNAMENT))
                        .buyIn(extractBigDecimal(line, START_BUY_IN_PRIZE, END_BUY_IN_PRIZE).add(extractBigDecimal(line, START_BUY_IN_RAKE, END_BUY_IN_RAKE)))
                        .build();
    }

    public Hand extractHand(String line) {
        return Hand
                .builder()
                .id(extractLong(line, START_HAND, END_HAND))
                .level(extract(line, START_LEVEL, END_LEVEL))
                .smallBlind(extractInteger(line, START_SMALL_BLIND, END_SMALL_BLIND))
                .bigBlind(extractInteger(line, START_BIG_BLIND, END_BIG_BLIND))
                .dateTime(extractLocalDateTime(line, START_DATE, END_DATE))
                .build();
    }

    public Hand extractTableAndButton(String line, Hand hand) {
        hand.setTableId(extract(line, START_TABLE, END_TABLE));
        hand.setButton(extractInteger(line, START_BUTTON, END_BUTTON));
        return hand;
    }

    public Seat extractSeat(String line) {
        return Seat
                .builder()
                .absolutePosition(extractInteger(line, START_SEAT_POSITION, END_SEAT_POSITION))
                .player(Player.builder().nickname(extract(line, START_PLAYER, END_PLAYER)).build())
                .stack(extractLong(line, START_STACK, END_STACK))
                .build();
    }

    public Action extractAction(String line) {
        TypeAction typeAction = FileParserUtil.selectTypeAction(line);
        return Action
                .builder()
                .player(Player.builder().nickname(StringUtils.substringBefore(line,":")).build())
                .typeAction(typeAction)
                .value(FileParserUtil.extractValueFromAction(line, typeAction))
                .build();
    }

    public HoldCards extractHoldCardsFromAction(String line) {
        if (line.contains(DEALT_TO)) {
            return HoldCards
                    .builder()
                    .player(Player.builder().nickname(FileParserUtil.extract(line, DEALT_TO, START_CARD)).build())
                    .card1(extractCard(line, START_CARD, END_CARD, 1))
                    .card2(extractCard(line, START_CARD, END_CARD, 2))
                    .build();
        } else {
            return HoldCards
                    .builder()
                    .player(Player.builder().nickname(StringUtils.substringBefore(line,":")).build())
                    .card1(extractCard(line, START_CARD, END_CARD, 1))
                    .card2(extractCard(line, START_CARD, END_CARD, 2))
                    .build();
        }
    }

    public Flop extractFlop(String line) {
        return Flop
                .builder()
                .card1(extractCard(line, START_CARD, END_CARD, 1))
                .card2(extractCard(line, START_CARD, END_CARD, 2))
                .card3(extractCard(line, START_CARD, END_CARD, 3))
                .build();
    }

    public Turn extractTurn(String line) {
        return Turn
                .builder()
                .card(extractCard(line, START_TURN, END_TURN, 1))
                .build();
    }

    public River extractRiver(String line) {
        return River
                .builder()
                .card(extractCard(line, START_RIVER, END_RIVER, 1))
                .build();
    }
}
