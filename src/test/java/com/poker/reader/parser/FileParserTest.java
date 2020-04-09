package com.poker.reader.parser;

import com.poker.reader.entity.*;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static com.poker.reader.parser.util.FileParserUtil.DATE_TIME_FORMAT;
import static org.junit.Assert.assertEquals;

public class FileParserTest {

    FileParser parser = new FileParser();

    @Test
    public void extractTournamentTest() {
        String line = "PokerStars Hand #210434850106: Tournament #2834364251, $0.23+$0.02 USD Hold'em No Limit - " +
                "Level I (10/20) - 2020/03/21 16:33:37 EET [2020/03/21 10:33:37 ET]\n";
        Tournament expectedTournament = Tournament.builder().id(2834364251L).buyIn(BigDecimal.valueOf(0.25)).build();
        Tournament actualTournament = parser.extractTournament(line);
        assertEquals(expectedTournament, actualTournament);
    }

    @Test
    public void extractHandTest() {
        String line = "PokerStars Hand #210434850106: Tournament #2834364251, $0.23+$0.02 USD Hold'em No Limit - " +
                "Level I (10/20) - 2020/03/21 16:33:37 EET [2020/03/21 10:33:37 ET]\n";
        LocalDateTime dateTime = LocalDateTime.parse("2020/03/21 10:33:37", DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
        Hand expectedHand =
                Hand.builder().id(210434850106L).level("I").smallBlind(10).bigBlind(20).dateTime(dateTime).build();
        Hand actualHand = parser.extractHand(line);
        assertEquals(expectedHand, actualHand);
    }

    @Test
    public void extractTableAndButtonTest() {
        String line = "Table '2779056951 40' 9-max Seat #5 is the button";
        Hand expectedHand = Hand.builder().tableId("2779056951 40").button(5).build();
        Hand actualHand = parser.extractTableAndButton(line, Hand.builder().build());
        assertEquals(expectedHand, actualHand);
    }

    @Test
    public void extractSeatTest() {
        String line = "Seat 7: Oliver N76 (13836 in chips)";
        Seat expected = Seat.builder().absolutePosition(7).player(Player.builder().nickname("Oliver N76").build()).stack(13836L)
                .build();
        Seat actual = parser.extractSeat(line);
        assertEquals(expected, actual);
    }

    @Test
    public void extractTypeActionTest() {
        String line = "H3ll5cream: raises 2597 to 2697 and is all-in";
        TypeAction actual = FileParser.selectTypeAction(line);
        TypeAction expected = TypeAction.ALL_IN;
        assertEquals(expected, actual);

        line = "GunDolfAA: raises 360 to 480";
        actual = FileParser.selectTypeAction(line);
        expected = TypeAction.RAISE;
        assertEquals(expected, actual);

        line = "W SERENA: calls 480";
        actual = FileParser.selectTypeAction(line);
        expected = TypeAction.CALL;
        assertEquals(expected, actual);

        line = "H3ll5cream: folds ";
        actual = FileParser.selectTypeAction(line);
        expected = TypeAction.FOLD;
        assertEquals(expected, actual);

        line = "mjmj1971: checks ";
        actual = FileParser.selectTypeAction(line);
        expected = TypeAction.CHECK;
        assertEquals(expected, actual);

        line = "GunDolfAA: bets 120";
        actual = FileParser.selectTypeAction(line);
        expected = TypeAction.BETS;
        assertEquals(expected, actual);
    }

    @Test
    public void extractActionTest() {
        String line = "H3ll5cream: raises 2597 to 2697 and is all-in";
        Action actual = parser.extractAction(line);
        Action expected =
                Action.builder().player(Player.builder().nickname("H3ll5cream").build()).typeAction(TypeAction.ALL_IN).value(2697L).build();
        assertEquals(expected, actual);

        line = "GunDolfAA: raises 360 to 480";
        actual = parser.extractAction(line);
        expected = Action.builder().player(Player.builder().nickname("GunDolfAA").build()).typeAction(TypeAction.RAISE)
                .value(480L).build();
        assertEquals(expected, actual);

        line = "W SERENA: calls 480";
        actual = parser.extractAction(line);
        expected = Action.builder().player(Player.builder().nickname("W SERENA").build()).typeAction(TypeAction.CALL)
                .value(480L).build();
        assertEquals(expected, actual);


        line = "H3ll5cream: folds ";
        actual = parser.extractAction(line);
        expected = Action.builder().player(Player.builder().nickname("H3ll5cream").build()).typeAction(TypeAction.FOLD)
                .value(0L).build();
        assertEquals(expected, actual);

        line = "mjmj1971: checks ";
        actual = parser.extractAction(line);
        expected = Action.builder().player(Player.builder().nickname("mjmj1971").build()).typeAction(TypeAction.CHECK)
                .value(0L).build();
        assertEquals(expected, actual);

        line = "GunDolfAA: bets 120";
        actual = parser.extractAction(line);
        expected = Action.builder().player(Player.builder().nickname("GunDolfAA").build()).typeAction(TypeAction.BETS)
                .value(120L).build();
        assertEquals(expected, actual);

        line = "VitalikX77: calls 4604 and is all-in";
        actual = parser.extractAction(line);
        expected = Action.builder().player(Player.builder().nickname("VitalikX77").build())
                .typeAction(TypeAction.CALL_ALL_IN).value(4604L).build();
        assertEquals(expected, actual);

        line = "mjmj1971: doesn't show hand";
        actual = parser.extractAction(line);
        expected = Action.builder().player(Player.builder().nickname("mjmj1971").build())
                .typeAction(TypeAction.NO_SHOW_HAND).value(0L).build();
        assertEquals(expected, actual);

    }

    @Test
    public void extractHoldCards() {
        String line = "Dealt to jcarlos.vale [3d Tc]";
        HoldCards actual = parser.extractHoldCardsFromAction(line);
        HoldCards expected =
                HoldCards.builder().card1("3d").card2("Tc").player(Player.builder().nickname("jcarlos.vale").build()).build();
        assertEquals(expected, actual);

        line = "Oliver N76: folds [8c 8s]";
        actual = parser.extractHoldCardsFromAction(line);
        expected = HoldCards.builder().card1("8c").card2("8s").player(Player.builder().nickname("Oliver N76").build())
                .build();
        assertEquals(expected, actual);

        line = "GunDolfAA: folds";
        actual = parser.extractHoldCardsFromAction(line);
        expected = HoldCards.builder().card1(null).card2(null).player(Player.builder().nickname("GunDolfAA").build())
                .build();
        assertEquals(expected, actual);
    }

    @Test
    public void extractFlop() {
        String line = "*** FLOP *** [9d 2h 9h]";
        Flop actual = parser.extractFlop(line);
        Flop expected = Flop.builder().card1("9d").card2("2h").card3("9h").build();
        assertEquals(expected, actual);
    }

    @Test
    public void extractTurn() {
        String line = "*** TURN *** [9d 2h 9h] [Jh]";
        Turn actual = parser.extractTurn(line);
        Turn expected = Turn.builder().card("Jh").build();
        assertEquals(expected, actual);
    }

    @Test
    public void extractRiver() {
        String line = "*** RIVER *** [9d 2h 9h Jh] [6s]";
        River actual = parser.extractRiver(line);
        River expected = River.builder().card("6s").build();
        assertEquals(expected, actual);
    }

    @Test
    public void extractUncalledBetTest() {
        String line = "Uncalled bet (300) returned to mjmj1971";
        AdditionalInfoPlayer actual = parser.extractAdditionalInfoPlayerUncalledBet(line);
        AdditionalInfoPlayer expected = AdditionalInfoPlayer.builder().info(TypeInfo.UNCALLED_BET).value(300L)
                .player(Player.builder().nickname("mjmj1971").build()).build();
        assertEquals(expected, actual);
    }

    @Test
    public void extractAdditionalInfoPlayerCollectedFromPotTest() {
        String line = "mjmj1971 collected 440 from pot";
        AdditionalInfoPlayer actual = parser.extractAdditionalInfoPlayerCollectedFromPot(line);
        AdditionalInfoPlayer expected = AdditionalInfoPlayer.builder().info(TypeInfo.COLLECTED).value(440L)
                .player(Player.builder().nickname("mjmj1971").build()).build();
        assertEquals(expected, actual);
    }

    @Test
    public void extractTotalPotTest() {
        String line = "Total pot 440 | Rake 0";
        Long actual = parser.extractTotalPot(line);
        Long expected = 440L;
        assertEquals(expected, actual);
    }

    @Test
    public void extractBoardTest() {
        String line = "Board [7s 5h Jc Qd 8d]";
        List<String> actual = parser.extractBoard(line);
        List<String> expected = new ArrayList<>();
        expected.add("7s");
        expected.add("5h");
        expected.add("Jc");
        expected.add("Qd");
        expected.add("8d");
        assertEquals(expected, actual);
    }

    @Test
    public void extractSummaryTest() {
        String line = "Seat 1: W SERENA folded on the River";
        Summary actual = parser.extractSummary(line);
        Summary expected =
                Summary.builder().seatId(1).value(null).additionalInfoPlayerList(new HashSet<>(Arrays.asList(TypeInfo.FOLDED_ON_THE_RIVER))).build();
        assertEquals(expected, actual);

        line = "Seat 2: matalaha folded before Flop (didn't bet)";
        actual = parser.extractSummary(line);
        expected =
                Summary.builder().seatId(2).value(null).additionalInfoPlayerList(new HashSet<>(Arrays.asList(TypeInfo.FOLDED_BEFORE_FLOP, TypeInfo.DID_NOT_BET))).build();
        assertEquals(expected, actual);

        line = "Seat 5: mjmj1971 (button) collected (440)";
        actual = parser.extractSummary(line);
        expected =
                Summary.builder().seatId(5).value(440L).additionalInfoPlayerList(new HashSet<>(Arrays.asList(TypeInfo.BUTTON,
                        TypeInfo.COLLECTED))).build();
        assertEquals(expected, actual);

        line = "Seat 8: H3ll5cream (big blind) collected (590)";
        actual = parser.extractSummary(line);
        expected =
                Summary.builder().seatId(8).value(590L).additionalInfoPlayerList(new HashSet<>(Arrays.asList(TypeInfo.BIG_BLIND,
                        TypeInfo.COLLECTED))).build();
        assertEquals(expected, actual);
    }
}
