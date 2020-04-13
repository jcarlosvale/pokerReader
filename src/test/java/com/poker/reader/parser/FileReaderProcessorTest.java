package com.poker.reader.parser;

import com.poker.reader.entity.*;
import com.poker.reader.parser.util.TypeFileSection;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.poker.reader.parser.util.FileParserUtil.DATE_TIME_FORMAT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FileReaderProcessorTest {

    FileReaderProcessor fileReaderProcessor = new FileReaderProcessor();

    @Test
    public void processOneHandTest() throws IOException {
        Resource resource = new ClassPathResource("one-hand.txt", getClass().getClassLoader());
        fileReaderProcessor.readFile(resource.getFile().getAbsolutePath());

        Tournament expectedTournament = Tournament.builder().id(2779056951L).buyIn(BigDecimal.valueOf(0.55)).build();
        assertEquals(expectedTournament, fileReaderProcessor.getTournament());

        Hand expectedHand = Hand
                .builder()
                .id(208296842229L)
                .level("VI")
                .smallBlind(50)
                .bigBlind(100)
                .dateTime(LocalDateTime.parse("2020/01/18 15:02:11", DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)))
                .tableId("2779056951 40")
                .button(5)
                .build();
        expectedHand.getSeats().add(Seat
                .builder()
                .absolutePosition(1)
                .player(Player.builder().nickname("W SERENA").build())
                .stack(5000L)
                .build());
        expectedHand.getSeats().add(Seat
                .builder()
                .absolutePosition(2)
                .player(Player.builder().nickname("matalaha").build())
                .stack(6917L)
                .build());
        expectedHand.getSeats().add(Seat
                .builder()
                .absolutePosition(3)
                .player(Player.builder().nickname("xTheWindelPilot").build())
                .stack(1268L)
                .build());
        expectedHand.getSeats().add(Seat
                .builder()
                .absolutePosition(4)
                .player(Player.builder().nickname("schlier4").build())
                .stack(8186L)
                .build());
        expectedHand.getSeats().add(Seat
                .builder()
                .absolutePosition(5)
                .player(Player.builder().nickname("mjmj1971").build())
                .stack(10998L)
                .build());
        expectedHand.getSeats().add(Seat
                .builder()
                .absolutePosition(6)
                .player(Player.builder().nickname("GunDolfAA").build())
                .stack(4523L)
                .build());
        expectedHand.getSeats().add(Seat
                .builder()
                .absolutePosition(7)
                .player(Player.builder().nickname("Oliver N76").build())
                .stack(13836L)
                .build());
        expectedHand.getSeats().add(Seat
                .builder()
                .absolutePosition(8)
                .player(Player.builder().nickname("H3ll5cream").build())
                .stack(2717L)
                .build());
        expectedHand.getSeats().add(Seat
                .builder()
                .absolutePosition(9)
                .player(Player.builder().nickname("jcarlos.vale").build())
                .stack(5000L)
                .build());

        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("W SERENA").build())
                .value(10L)
                .typeStreet(TypeStreet.ANTE)
                .typeAction(TypeAction.ANTE)
                .build());

        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("matalaha").build())
                .value(10L)
                .typeStreet(TypeStreet.ANTE)
                .typeAction(TypeAction.ANTE)
                .build());

        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("xTheWindelPilot").build())
                .value(10L)
                .typeStreet(TypeStreet.ANTE)
                .typeAction(TypeAction.ANTE)
                .build());

        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("schlier4").build())
                .value(10L)
                .typeStreet(TypeStreet.ANTE)
                .typeAction(TypeAction.ANTE)
                .build());

        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("mjmj1971").build())
                .value(10L)
                .typeStreet(TypeStreet.ANTE)
                .typeAction(TypeAction.ANTE)
                .build());

        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("GunDolfAA").build())
                .value(10L)
                .typeStreet(TypeStreet.ANTE)
                .typeAction(TypeAction.ANTE)
                .build());

        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("Oliver N76").build())
                .value(10L)
                .typeStreet(TypeStreet.ANTE)
                .typeAction(TypeAction.ANTE)
                .build());

        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("H3ll5cream").build())
                .value(10L)
                .typeStreet(TypeStreet.ANTE)
                .typeAction(TypeAction.ANTE)
                .build());

        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("jcarlos.vale").build())
                .value(10L)
                .typeStreet(TypeStreet.ANTE)
                .typeAction(TypeAction.ANTE)
                .build());

        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("GunDolfAA").build())
                .value(50L)
                .typeStreet(TypeStreet.ANTE)
                .typeAction(TypeAction.SMALL_BLIND)
                .build());

        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("Oliver N76").build())
                .value(100L)
                .typeStreet(TypeStreet.ANTE)
                .typeAction(TypeAction.BIG_BLIND)
                .build());

        List<Hand> expectedHandList = new ArrayList<>();
        expectedHandList.add(expectedHand);

        assertEquals(expectedHandList, fileReaderProcessor.getHandList());
    }

    @Test
    public void verifySectionTest() {
        String line = "PokerStars Hand #208296842229: Tournament #2779056951, $0.49+$0.06 USD Hold'em No Limit - " +
                "Level VI (50/100) - 2020/01/18 22:02:11 EET [2020/01/18 15:02:11 ET]";
        TypeFileSection actual = fileReaderProcessor.verifySection(line);
        TypeFileSection expected = TypeFileSection.HEADER;
        assertEquals(expected, actual);

        line = "*** HOLE CARDS ***";
        actual = fileReaderProcessor.verifySection(line);
        expected = TypeFileSection.PRE_FLOP;
        assertEquals(expected, actual);

        line = "*** FLOP *** [7s 5h Jc]";
        actual = fileReaderProcessor.verifySection(line);
        expected = TypeFileSection.FLOP;
        assertEquals(expected, actual);

        line = "*** TURN *** [7s 5h Jc] [Qd]";
        actual = fileReaderProcessor.verifySection(line);
        expected = TypeFileSection.TURN;
        assertEquals(expected, actual);

        line = "*** RIVER *** [7s 5h Jc Qd] [8d]";
        actual = fileReaderProcessor.verifySection(line);
        expected = TypeFileSection.RIVER;
        assertEquals(expected, actual);

        line = "*** SUMMARY ***";
        actual = fileReaderProcessor.verifySection(line);
        expected = TypeFileSection.SUMMARY;
        assertEquals(expected, actual);

        line = "Seat 7: Oliver N76 (big blind) folded on the River\n";
        actual = fileReaderProcessor.verifySection(line);
        assertNull(actual);
    }
}
