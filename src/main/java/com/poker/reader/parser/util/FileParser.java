package com.poker.reader.parser.util;

import com.poker.reader.entity.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.poker.reader.parser.util.FileParserUtil.*;
import static com.poker.reader.parser.util.Tokens.*;

public class FileParser {

    public static Tournament extractTournament(String line) {
        return Tournament.builder().id(extractLong(line, START_TOURNAMENT, END_TOURNAMENT))
                .buyIn(extractBigDecimal(line, START_BUY_IN_PRIZE, END_BUY_IN_PRIZE)
                        .add(extractBigDecimal(line, START_BUY_IN_RAKE, END_BUY_IN_RAKE))).build();
    }

    public static Hand extractHand(String line) {
        return Hand.builder().id(extractLong(line, START_HAND, END_HAND)).level(extract(line, START_LEVEL, END_LEVEL))
                .smallBlind(extractInteger(line, START_SMALL_BLIND, END_SMALL_BLIND))
                .bigBlind(extractInteger(line, START_BIG_BLIND, END_BIG_BLIND))
                .dateTime(extractLocalDateTime(line, START_DATE, END_DATE)).build();
    }

    public static String extractTable(String line) {
        return extract(line, START_TABLE, END_TABLE);
    }

    public static Integer extractButton(String line) {
        return extractInteger(line, START_BUTTON, END_BUTTON);
    }


    public static Seat extractSeat(String line) {
        return Seat.builder().seatId(extractInteger(line, START_SEAT_POSITION, END_SEAT_POSITION))
                .player(Player.builder().nickname(extract(line, START_PLAYER, END_PLAYER)).build())
                .stack(extractLong(line, START_STACK, END_STACK)).build();
    }

    public static Action extractAction(String line) {
        TypeAction typeAction = selectTypeAction(line);
        return Action.builder().player(Player.builder().nickname(StringUtils.substringBefore(line, ":").trim()).build())
                .typeAction(typeAction).value(FileParserUtil.extractValueFromAction(line, typeAction)).build();
    }

    public static HoldCards extractHoldCardsFromAction(String line) {
        if (line.contains(DEALT_TO)) {
            return HoldCards.builder()
                    .player(Player.builder().nickname(FileParserUtil.extract(line, DEALT_TO, START_CARD)).build())
                    .card1(extractCard(line, START_CARD, END_CARD, 1)).card2(extractCard(line, START_CARD, END_CARD, 2))
                    .build();
        } else {
            return HoldCards.builder()
                    .player(Player.builder().nickname(StringUtils.substringBefore(line, ":").trim()).build())
                    .card1(extractCard(line, START_CARD, END_CARD, 1)).card2(extractCard(line, START_CARD, END_CARD, 2))
                    .build();
        }
    }

    public static Flop extractFlop(String line) {
        return Flop.builder().card1(extractCard(line, START_CARD, END_CARD, 1))
                .card2(extractCard(line, START_CARD, END_CARD, 2)).card3(extractCard(line, START_CARD, END_CARD, 3))
                .build();
    }

    public static Turn extractTurn(String line) {
        return Turn.builder().card(extractCard(line, START_TURN, END_TURN, 1)).build();
    }

    public static River extractRiver(String line) {
        return River.builder().card(extractCard(line, START_RIVER, END_RIVER, 1)).build();
    }

    public static AdditionalInfoPlayer extractAdditionalInfoPlayerUncalledBet(String line) {
        return AdditionalInfoPlayer.builder()
                .info(TypeInfo.UNCALLED_BET)
                .value(extractLong(line, START_UNCALLED_BET, END_UNCALLED_BET))
                .player(Player.builder().nickname(StringUtils.substringAfter(line, RETURNED_TO).trim()).build())
                .build();
    }

    public static AdditionalInfoPlayer extractAdditionalInfoPlayerCollectedFromPot(String line) {
        return AdditionalInfoPlayer.builder()
                .info(TypeInfo.COLLECTED)
                .value(extractLong(line, START_COLLECTED_FROM_POT, END_COLLECTED_FROM_POT))
                .player(Player.builder().nickname(StringUtils.substringBefore(line, START_COLLECTED_FROM_POT).trim()).build())
                .build();
    }

    public static Long extractTotalPot(String line) {
        return extractLong(line, START_TOTAL_POT, END_TOTAL_POT);
    }

    public static Board extractBoard(String line) {
        List<String> listCards = extractList(line, START_BOARD, END_BOARD, " ");
        if (listCards.size() > 0) {
            Board board = Board
                    .builder()
                    .card1(listCards.get(0))
                    .card2(listCards.get(1))
                    .card3(listCards.get(2))
                    .build();
            if (listCards.size() > 3) board.setCard4(listCards.get(3));
            if (listCards.size() > 4) board.setCard5(listCards.get(4));
            return board;
        }
        return null;
    }

    public static Summary extractSummary(String line) {
        Set<TypeInfo> typeInfoList = extractTypeInfoListFrom(line);
        return Summary
                .builder()
                .seatId(extractInteger(line, START_SEAT_POSITION, END_SEAT_POSITION))
                .value(extractLong(line, START_COLLECTED_SUMMARY, END_COLLECTED_SUMMARY))
                .additionalInfoPlayerSet(typeInfoList)
                .build();
    }

    public static Set<TypeInfo> extractTypeInfoListFrom(String line) {
        return Arrays
                .stream(TypeInfo.values())
                .filter(typeInfo -> line.contains(typeInfo.getToken()))
                .collect(Collectors.toSet());
    }

    public static TypeAction selectTypeAction(String line) {
        if (line.contains(Tokens.ANTE_ACTION))         return TypeAction.ANTE;
        if (line.contains(Tokens.SMALL_BLIND_ACTION))  return TypeAction.SMALL_BLIND;
        if (line.contains(Tokens.BIG_BLIND_ACTION))    return TypeAction.BIG_BLIND;
        if (line.contains(Tokens.FOLD_ACTION))         return TypeAction.FOLD;
        if (line.contains(Tokens.CALL_ACTION)) {
            if (line.contains(Tokens.ALL_IN_ACTION))   return TypeAction.CALL_ALL_IN;
            else                                       return TypeAction.CALL;
        }
        if (line.contains(Tokens.CHECK_ACTION))        return TypeAction.CHECK;
        if (line.contains(Tokens.BETS_ACTION))         return TypeAction.BETS;

        if (line.contains(Tokens.RAISE_ACTION)) {
            if (line.contains(Tokens.ALL_IN_ACTION))   return TypeAction.ALL_IN;
            else                                       return TypeAction.RAISE;
        }
        if (line.contains(Tokens.NO_SHOW_HAND_ACTION)) return TypeAction.NO_SHOW_HAND;
        return null;
    }
}
