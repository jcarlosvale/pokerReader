package com.poker.reader.parser;

import com.poker.reader.entity.*;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        Hand expectedHand = Hand
                .builder()
                .id(210434850106L)
                .level("I")
                .smallBlind(10)
                .bigBlind(20)
                .dateTime(dateTime)
                .build();
        Hand actualHand = parser.extractHand(line);
        assertEquals(expectedHand, actualHand);
    }

    @Test
    public void extractTableAndButtonTest() {
        String line = "Table '2779056951 40' 9-max Seat #5 is the button";
        Hand expectedHand = Hand
                .builder()
                .tableId("2779056951 40")
                .button(5)
                .build();
        Hand actualHand = parser.extractTableAndButton(line, Hand.builder().build());
        assertEquals(expectedHand, actualHand);
    }

    @Test
    public void extractSeatTest() {
        String line = "Seat 7: Oliver N76 (13836 in chips)";
        Seat expected = Seat
                .builder()
                .absolutePosition(7)
                .player(Player.builder().nickname("Oliver N76").build())
                .stack(13836L)
                .build();
        Seat actual = parser.extractSeat(line);
        assertEquals(expected,actual);
    }

    @Test
    public void extractActionTest() {
        String line = "H3ll5cream: raises 2597 to 2697 and is all-in";
        Action actual = parser.extractAction(line);
        Action expected = Action
                .builder()
                .player(Player.builder().nickname("H3ll5cream").build())
                .typeAction(TypeAction.ALL_IN)
                .value(2697L)
                .build();
        assertEquals(expected, actual);

        line = "GunDolfAA: raises 360 to 480";
        actual = parser.extractAction(line);
        expected = Action
                .builder()
                .player(Player.builder().nickname("GunDolfAA").build())
                .typeAction(TypeAction.RAISE)
                .value(480L)
                .build();
        assertEquals(expected, actual);

        line = "W SERENA: calls 480";
        actual = parser.extractAction(line);
        expected = Action
                .builder()
                .player(Player.builder().nickname("W SERENA").build())
                .typeAction(TypeAction.CALL)
                .value(480L)
                .build();
        assertEquals(expected, actual);


        line = "H3ll5cream: folds ";
        actual = parser.extractAction(line);
        expected = Action
                .builder()
                .player(Player.builder().nickname("H3ll5cream").build())
                .typeAction(TypeAction.FOLD)
                .value(0L)
                .build();
        assertEquals(expected, actual);

        line = "mjmj1971: checks ";
        actual = parser.extractAction(line);
        expected = Action
                .builder()
                .player(Player.builder().nickname("mjmj1971").build())
                .typeAction(TypeAction.CHECK)
                .value(0L)
                .build();
        assertEquals(expected, actual);

        line = "GunDolfAA: bets 120";
        actual = parser.extractAction(line);
        expected = Action
                .builder()
                .player(Player.builder().nickname("GunDolfAA").build())
                .typeAction(TypeAction.BETS)
                .value(120L)
                .build();
        assertEquals(expected, actual);
    }
}
