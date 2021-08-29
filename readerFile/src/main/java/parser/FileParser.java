package parser;

import dto.*;
import exception.InvalidInfoPlayerAtHand;
import exception.InvalidTypeActionException;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static dto.TypeInfo.COLLECTED;
import static dto.TypeInfo.WON;

public class FileParser {

    //reviewed
    public static TournamentDTO extractTournament(String line) {
        return TournamentDTO.builder()
                .id(FileParserUtil.extractLong(line, Tokens.START_TOURNAMENT, Tokens.END_TOURNAMENT))
                .buyIn(FileParserUtil.extractBigDecimal(line, Tokens.START_BUY_IN_PRIZE, Tokens.END_BUY_IN_PRIZE))
                .build();
    }

    //reviewed
    public static HandDTO extractHand(String line) {
        return HandDTO.builder()
                .id(FileParserUtil.extractLong(line, Tokens.START_HAND, Tokens.END_HAND))
                .level(FileParserUtil.extract(line, Tokens.START_LEVEL, Tokens.END_LEVEL))
                .smallBlind(FileParserUtil.extractInteger(line, Tokens.START_SMALL_BLIND, Tokens.END_SMALL_BLIND))
                .bigBlind(FileParserUtil.extractInteger(line, Tokens.START_BIG_BLIND, Tokens.END_BIG_BLIND))
                .date(FileParserUtil.extractLocalDate(line, Tokens.START_DATE, Tokens.END_DATE)).build();
    }

    //reviewed
    public static String extractTable(String line) {
        return FileParserUtil.extract(line, Tokens.START_TABLE, Tokens.END_TABLE);
    }

    //reviewed
    public static Integer extractButton(String line) {
        return FileParserUtil.extractInteger(line, Tokens.START_BUTTON, Tokens.END_BUTTON);
    }

    //reviewed
    public static SeatDTO extractSeat(String line) {
        String nickname = extractNickname(line);
        return SeatDTO.builder()
                .seatId(FileParserUtil.extractInteger(line, Tokens.START_SEAT_POSITION, Tokens.END_SEAT_POSITION))
                .playerDTO(PlayerDTO.builder().nickname(nickname).build())
                .stack(FileParserUtil.extractLong(line, nickname + Tokens.START_STACK, Tokens.END_STACK)).build();
    }

    //reviewed
    public static String extractNickname(String line) {
        int startPos = line.indexOf(Tokens.START_PLAYER) + 1;
        int lastPos = -1;
        for (var i = 0; i < line.length(); i++) {
            if (line.charAt(i) == Tokens.END_PLAYER.charAt(1)) {
                lastPos = i;
            }
        }
        return line.substring(startPos, lastPos - 1).trim();
    }

    public static Integer extractSeatId(String line) {
        return FileParserUtil.extractInteger(line, Tokens.START_SEAT_POSITION, Tokens.END_SEAT_POSITION);
    }

    //reviewed
    public static Action extractAction(String line) {
        var typeAction = selectTypeAction(line);
        var action =
                Action.builder()
                        .playerDTO(PlayerDTO.builder().nickname(StringUtils.substringBefore(line, ":").trim()).build())
                        .typeAction(typeAction)
                        .value(FileParserUtil.extractValueFromAction(line, typeAction))
                        .build();
        if (typeAction.equals(TypeAction.SHOW_HAND)) {
            action.setHoldCards(extractHoldCards(line));
            action.setScoring(FileParserUtil.extract(line, Tokens.START_SCORING, Tokens.END_SCORING));
        }
        return action;
    }

    public static HoldCards extractHoldCards(String line) {
        if (line.contains(Tokens.DEALT_TO)) {
            return HoldCards.builder()
                    .playerDTO(PlayerDTO.builder()
                            .nickname(FileParserUtil.extract(line, Tokens.DEALT_TO, Tokens.START_CARD)).build())
                    .card1(FileParserUtil.extractCard(line, Tokens.START_CARD, Tokens.END_CARD, 1))
                    .card2(FileParserUtil.extractCard(line, Tokens.START_CARD, Tokens.END_CARD, 2))
                    .build();
        } else {
            String card1 = FileParserUtil.extractCard(line, Tokens.START_CARD, Tokens.END_CARD, 1);
            String card2 = FileParserUtil.extractCard(line, Tokens.START_CARD, Tokens.END_CARD, 2);
            return HoldCards.builder()
                    .playerDTO(PlayerDTO.builder().nickname(StringUtils.substringBefore(line, ":").trim()).build())
                    .card1(card1)
                    .card2(card2)
                    .build();
        }
    }

    public static Flop extractFlop(String line) {
        return Flop.builder().card1(FileParserUtil.extractCard(line, Tokens.START_CARD, Tokens.END_CARD, 1))
                .card2(FileParserUtil.extractCard(line, Tokens.START_CARD, Tokens.END_CARD, 2))
                .card3(FileParserUtil.extractCard(line, Tokens.START_CARD, Tokens.END_CARD, 3))
                .build();
    }

    public static Turn extractTurn(String line) {
        return Turn.builder().card(FileParserUtil.extractCard(line, Tokens.START_TURN, Tokens.END_TURN, 1)).build();
    }

    public static River extractRiver(String line) {
        return River.builder().card(FileParserUtil.extractCard(line, Tokens.START_RIVER, Tokens.END_RIVER, 1)).build();
    }

    public static Set<InfoPlayerAtHand> extractInfoPlayerAtHand(String line) {
        Set<InfoPlayerAtHand> infoPlayerAtHandSet = new HashSet<>();
        List<TypeInfo> typeInfoList = selectTypeInfo(line);
        for (TypeInfo infoPlayerAtHand : typeInfoList) {
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
                .value(FileParserUtil.extractLong(line, Tokens.START_WON_POT, Tokens.END_WON_POT))
                .build();
    }

    private static InfoPlayerAtHand extractCollectedInfo(String line) {
        return InfoPlayerAtHand.builder()
                .info(COLLECTED)
                .value(FileParserUtil.extractLong(line, Tokens.START_COLLECTED_FROM_POT, Tokens.END_COLLECTED_FROM_POT))
                .build();
    }

    public static Long extractTotalPot(String line) {
        return FileParserUtil.extractLong(line, Tokens.START_TOTAL_POT, Tokens.END_TOTAL_POT);
    }

    public static Long extractSidePot(String line) {
        if (line.contains(Tokens.START_SIDE_POT)) {
            String sidePotLine = line.substring(line.indexOf(Tokens.START_SIDE_POT));
            return FileParserUtil.extractLong(sidePotLine, Tokens.START_SIDE_POT, Tokens.END_SIDE_POT);
        }
        return null;
    }

    public static Board extractBoard(String line) {
        List<String> listCards = FileParserUtil.extractList(line, Tokens.START_BOARD, Tokens.END_BOARD, " ");
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
        if (line.contains(Tokens.SHOW_HAND_ACTION)) return TypeAction.SHOW_HAND;
        if (line.contains(Tokens.MUCKS_HAND_ACTION)) return TypeAction.MUCKS_HAND;
        throw new InvalidTypeActionException("TYPE ACTION NOT FOUND AT LINE: " + line);
    }

    public static List<TypeInfo> selectTypeInfo(String line) {
        return Arrays
                .stream(TypeInfo.values())
                .filter(info -> line.contains(info.getToken()))
                .collect(Collectors.toList());
    }

}
