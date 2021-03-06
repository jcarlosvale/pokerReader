package com.poker.reader.parser.util;

import com.poker.reader.dto.*;
import com.poker.reader.exception.InvalidInfoPlayerAtHand;
import com.poker.reader.exception.InvalidTypeActionException;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.poker.reader.dto.TypeInfo.COLLECTED;
import static com.poker.reader.dto.TypeInfo.WON;
import static com.poker.reader.parser.util.FileParserUtil.*;
import static com.poker.reader.parser.util.Tokens.*;

public class FileParser {

    public static TournamentDTO extractTournament(String line) {
        return TournamentDTO.builder().id(extractLong(line, START_TOURNAMENT, END_TOURNAMENT))
                .buyIn(extractBigDecimal(line, START_BUY_IN_PRIZE, END_BUY_IN_PRIZE)).build();
    }

    public static HandDTO extractHand(String line) {
        return HandDTO.builder().id(extractLong(line, START_HAND, END_HAND)).level(extract(line, START_LEVEL, END_LEVEL))
                .smallBlind(extractInteger(line, START_SMALL_BLIND, END_SMALL_BLIND))
                .bigBlind(extractInteger(line, START_BIG_BLIND, END_BIG_BLIND))
                .date(extractLocalDate(line, START_DATE, END_DATE)).build();
    }

    public static String extractTable(String line) {
        return extract(line, START_TABLE, END_TABLE);
    }

    public static Integer extractButton(String line) {
        return extractInteger(line, START_BUTTON, END_BUTTON);
    }


    public static SeatDTO extractSeat(String line) {
        String nickname = extractNickname(line, START_PLAYER, END_PLAYER);
        return SeatDTO.builder().seatId(extractInteger(line, START_SEAT_POSITION, END_SEAT_POSITION))
                .playerDTO(PlayerDTO.builder().nickname(nickname).build())
                .stack(extractLong(line, nickname+START_STACK, END_STACK)).build();
    }

    private static String extractNickname(String line, String startPlayer, String endPlayer) {
        int startPos = line.indexOf(startPlayer) + 1;
        int lastPos = -1;
        for (int i = 0; i < line.length(); i++) {
            if(line.charAt(i) == endPlayer.charAt(1)) {
                lastPos = i;
            }
        }
        return line.substring(startPos, lastPos-1).trim();
    }

    public static Integer extractSeatId(String line) {
        return extractInteger(line, START_SEAT_POSITION, END_SEAT_POSITION);
    }

    public static Action extractAction(String line) {
        TypeAction typeAction = selectTypeAction(line);
        Action action =
                Action.builder()
                .playerDTO(PlayerDTO.builder().nickname(StringUtils.substringBefore(line, ":").trim()).build())
                .typeAction(typeAction)
                .value(extractValueFromAction(line, typeAction))
                .build();
        if (typeAction.equals(TypeAction.SHOW_HAND)) {
            action.setHoldCards(extractHoldCards(line));
            action.setScoring(extract(line, START_SCORING, END_SCORING));
        }
        return action;
    }

    public static HoldCards extractHoldCards(String line) {
        if (line.contains(DEALT_TO)) {
            return HoldCards.builder()
                    .playerDTO(PlayerDTO.builder().nickname(extract(line, DEALT_TO, START_CARD)).build())
                    .card1(extractCard(line, START_CARD, END_CARD, 1)).card2(extractCard(line, START_CARD, END_CARD, 2))
                    .build();
        } else {
            String card1 = extractCard(line, START_CARD, END_CARD, 1);
            String card2 = extractCard(line, START_CARD, END_CARD, 2);
            return HoldCards.builder()
                    .playerDTO(PlayerDTO.builder().nickname(StringUtils.substringBefore(line, ":").trim()).build())
                    .card1(card1)
                    .card2(card2)
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

    public static Set<InfoPlayerAtHand> extractInfoPlayerAtHand(String line) {
        Set<InfoPlayerAtHand> infoPlayerAtHandSet = new HashSet<>();
        List<TypeInfo> typeInfoList = selectTypeInfo(line);
        for(TypeInfo infoPlayerAtHand : typeInfoList) {
            switch (infoPlayerAtHand) {
                case COLLECTED:
                    infoPlayerAtHandSet.add(extractCollectedInfo(line));
                    break;
                case FOLDED_BEFORE_FLOP:
                case FOLDED_ON_THE_RIVER:
                case BUTTON:
                case BIG_BLIND:
                case SMALL_BLIND:
                case SHOWED_HAND:
                case LOST:
                    infoPlayerAtHandSet.add(
                        InfoPlayerAtHand.builder()
                                .info(infoPlayerAtHand)
                                .build());
                    break;
                case WON:
                    infoPlayerAtHandSet.add(extractWonInfo(line));
                    break;
                default:
                    throw new InvalidInfoPlayerAtHand("TYPE INFO PLAYER AT HAND NOT FOUND AT LINE: " + line);
            }
        }
        return infoPlayerAtHandSet;
    }

    private static InfoPlayerAtHand extractWonInfo(String line) {
        return InfoPlayerAtHand.builder()
                .info(WON)
                .value(extractLong(line, START_WON_POT, END_WON_POT))
                .build();
    }

    private static InfoPlayerAtHand extractCollectedInfo(String line) {
        return InfoPlayerAtHand.builder()
                .info(COLLECTED)
                .value(extractLong(line, START_COLLECTED_FROM_POT, END_COLLECTED_FROM_POT))
                .build();
    }

    public static Long extractTotalPot(String line) {
        return extractLong(line, START_TOTAL_POT, END_TOTAL_POT);
    }

    public static Long extractSidePot(String line) {
        if (line.contains(START_SIDE_POT)) {
            String sidePotLine = line.substring(line.indexOf(START_SIDE_POT));
            return extractLong(sidePotLine, START_SIDE_POT, END_SIDE_POT);
        }
        return null;
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

    public static TypeAction selectTypeAction(String line) {
        if (line.contains(Tokens.ALL_IN_ACTION)) return TypeAction.ALL_IN;
        if (line.contains(Tokens.ANTE_ACTION)) return TypeAction.ANTE;
        if (line.contains(Tokens.SMALL_BLIND_ACTION)) return TypeAction.SMALL_BLIND;
        if (line.contains(Tokens.BIG_BLIND_ACTION)) return TypeAction.BIG_BLIND;
        if (line.contains(Tokens.FOLD_ACTION)) return TypeAction.FOLD;
        if (line.contains(Tokens.CALL_ACTION)) return TypeAction.CALL;
        if (line.contains(Tokens.CHECK_ACTION)) return TypeAction.CHECK;
        if (line.contains(Tokens.BETS_ACTION)) return TypeAction.BETS;
        if (line.contains(Tokens.RAISE_ACTION)) return TypeAction.RAISE;
        if (line.contains(Tokens.NO_SHOW_HAND_ACTION)) return TypeAction.NO_SHOW_HAND;
        if (line.contains(SHOW_HAND_ACTION)) return TypeAction.SHOW_HAND;
        if (line.contains(MUCKS_HAND_ACTION)) return TypeAction.MUCKS_HAND;
        throw new InvalidTypeActionException("TYPE ACTION NOT FOUND AT LINE: " + line);
    }

    public static List<TypeInfo> selectTypeInfo(String line) {
        return Arrays
                .stream(TypeInfo.values())
                .filter(info -> line.contains(info.getToken()))
                .collect(Collectors.toList());
    }

}
