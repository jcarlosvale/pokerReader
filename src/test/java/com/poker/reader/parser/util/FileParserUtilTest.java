package com.poker.reader.parser.util;

import com.poker.reader.entity.TypeAction;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.poker.reader.parser.util.FileParserUtil.DATE_TIME_FORMAT;
import static com.poker.reader.parser.util.Tokens.END_CARD;
import static com.poker.reader.parser.util.Tokens.START_CARD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FileParserUtilTest {

    @Test
    public void extractEmptyLongTest() {
        assertNull(FileParserUtil.extractLong(null, "some start", "some end"));
    }

    @Test
    public void extractLongTest() {
        Long expectedId = 2834364251L;
        String line = "PokerStars Hand #210434850106: Tournament #2834364251, $0.23+$0.02 USD Hold'em No Limit - " +
                "Level I (10/20) - 2020/03/21 16:33:37 EET [2020/03/21 10:33:37 ET]\n";
        Long actualId = FileParserUtil.extractLong(line, Tokens.START_TOURNAMENT, Tokens.END_TOURNAMENT);
        assertEquals(expectedId, actualId);
    }

    @Test
    public void extractBigDecimalTest() {
        BigDecimal expected = new BigDecimal("0.23");
        String line = "PokerStars Hand #210434850106: Tournament #2834364251, $0.23+$0.02 USD Hold'em No Limit - " +
                "Level I (10/20) - 2020/03/21 16:33:37 EET [2020/03/21 10:33:37 ET]\n";
        BigDecimal actual = FileParserUtil.extractBigDecimal(line, Tokens.START_BUY_IN_PRIZE, Tokens.END_BUY_IN_PRIZE);
        assertEquals(expected, actual);
    }

    @Test
    public void extractEmptyBigDecimalTest() {
        BigDecimal expected = BigDecimal.ZERO;
        String line = "some text";
        BigDecimal actual = FileParserUtil.extractBigDecimal(line, "some token", "some token");
        assertEquals(expected, actual);
    }

    @Test
    public void extractIntegerTest() {
        Integer expected = 20;
        String line = "PokerStars Hand #210434850106: Tournament #2834364251, $0.23+$0.02 USD Hold'em No Limit - " +
                "Level I (10/20) - 2020/03/21 16:33:37 EET [2020/03/21 10:33:37 ET]\n";
        Integer actual = FileParserUtil.extractInteger(line, Tokens.START_BIG_BLIND, Tokens.END_BIG_BLIND);
        assertEquals(expected, actual);
    }

    @Test
    public void extractEmptyIntegerTest() {
        String line = "some text";
        Integer actual = FileParserUtil.extractInteger(line, "some token", "some token");
        assertNull(actual);
    }

    @Test
    public void extractDateTimeTest() {
        LocalDateTime expected = LocalDateTime.parse("2020/03/21 10:33:37", DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
        String line = "PokerStars Hand #210434850106: Tournament #2834364251, $0.23+$0.02 USD Hold'em No Limit - " +
                "Level I (10/20) - 2020/03/21 16:33:37 EET [2020/03/21 10:33:37 ET]\n";
        LocalDateTime actual = FileParserUtil.extractLocalDateTime(line, Tokens.START_DATE, Tokens.END_DATE);
        assertEquals(expected, actual);
    }

    @Test
    public void extractEmptyDateTimeTest() {
        String line = "some text";
        LocalDateTime actual = FileParserUtil.extractLocalDateTime(line, "some token", "some token");
        assertNull(actual);
    }

    @Test
    public void extractTypeActionTest() {
        String line = "H3ll5cream: raises 2597 to 2697 and is all-in";
        TypeAction actual = FileParserUtil.selectTypeAction(line);
        TypeAction expected = TypeAction.ALL_IN;
        assertEquals(expected, actual);

        line = "GunDolfAA: raises 360 to 480";
        actual = FileParserUtil.selectTypeAction(line);
        expected = TypeAction.RAISE;
        assertEquals(expected, actual);

        line = "W SERENA: calls 480";
        actual = FileParserUtil.selectTypeAction(line);
        expected = TypeAction.CALL;
        assertEquals(expected, actual);

        line = "H3ll5cream: folds ";
        actual = FileParserUtil.selectTypeAction(line);
        expected = TypeAction.FOLD;
        assertEquals(expected, actual);

        line = "mjmj1971: checks ";
        actual = FileParserUtil.selectTypeAction(line);
        expected = TypeAction.CHECK;
        assertEquals(expected, actual);

        line = "GunDolfAA: bets 120";
        actual = FileParserUtil.selectTypeAction(line);
        expected = TypeAction.BETS;
        assertEquals(expected, actual);
    }

    @Test
    public void extractValueFromActionTest() {
        String line = "H3ll5cream: raises 2597 to 2697 and is all-in";
        Long actual = FileParserUtil.extractValueFromAction(line, TypeAction.ALL_IN);
        Long expected = 2697L;
        assertEquals(expected, actual);

        line = "GunDolfAA: raises 360 to 480";
        actual = FileParserUtil.extractValueFromAction(line, TypeAction.RAISE);
        expected = 480L;
        assertEquals(expected, actual);

        line = "W SERENA: calls 480";
        actual = FileParserUtil.extractValueFromAction(line, TypeAction.CALL);
        expected = 480L;
        assertEquals(expected, actual);


        line = "H3ll5cream: folds ";
        actual = FileParserUtil.extractValueFromAction(line, TypeAction.FOLD);
        expected = 0L;
        assertEquals(expected, actual);

        line = "mjmj1971: checks ";
        actual = FileParserUtil.extractValueFromAction(line, TypeAction.CHECK);
        expected = 0L;
        assertEquals(expected, actual);

        line = "GunDolfAA: bets 120";
        actual = FileParserUtil.extractValueFromAction(line, TypeAction.BETS);
        expected = 120L;
        assertEquals(expected, actual);
    }

    @Test
    public void extractCardTest() {
        String line = "Dealt to jcarlos.vale [3d Tc]";

        String actual = FileParserUtil.extractCard(line, START_CARD, END_CARD, 1);
        String expected = "3d";
        assertEquals(actual, expected);

        actual = FileParserUtil.extractCard(line, START_CARD, END_CARD, 2);
        expected = "Tc";
        assertEquals(actual, expected);

        line = "Oliver N76: folds [8c 8s]";
        actual = FileParserUtil.extractCard(line, START_CARD, END_CARD, 1);
        expected = "8c";
        assertEquals(actual, expected);

        actual = FileParserUtil.extractCard(line, START_CARD, END_CARD, 2);
        expected = "8s";
        assertEquals(actual, expected);

        line = "mjmj1971: folds";
        actual = FileParserUtil.extractCard(line, START_CARD, END_CARD, 1);
        assertNull(actual);

        actual = FileParserUtil.extractCard(line, START_CARD, END_CARD, 2);
        assertNull(actual);
    }
}
