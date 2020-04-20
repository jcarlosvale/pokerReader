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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        Hand expectedHand = Hand
                .builder()
                .id(208296842229L)
                .tournament(expectedTournament)
                .level("VI")
                .smallBlind(50)
                .bigBlind(100)
                .dateTime(LocalDateTime.parse("2020/01/18 15:02:11", DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)))
                .tableId("2779056951 40")
                .button(5)
                .build();

        Set<TypeInfo> additionalInfoPlayerSet = new HashSet<>();
        additionalInfoPlayerSet.add(TypeInfo.FOLDED_ON_THE_RIVER);
        expectedHand.getSeats().put(Player.builder().nickname("W SERENA").build(),
                Seat
                .builder()
                .seatId(1)
                .player(Player.builder().nickname("W SERENA").build())
                .stack(5000L)
                .summary(Summary
                        .builder()
                        .seatId(1)
                        .additionalInfoPlayerSet(additionalInfoPlayerSet)
                        .value(null)
                        .build())
                .build());

        additionalInfoPlayerSet = new HashSet<>();
        additionalInfoPlayerSet.add(TypeInfo.FOLDED_BEFORE_FLOP);
        additionalInfoPlayerSet.add(TypeInfo.DID_NOT_BET);
        expectedHand.getSeats().put(Player.builder().nickname("matalaha").build(),
                Seat
                .builder()
                .seatId(2)
                .player(Player.builder().nickname("matalaha").build())
                .stack(6917L)
                .summary(Summary
                        .builder()
                        .seatId(2)
                        .additionalInfoPlayerSet(additionalInfoPlayerSet)
                        .value(null)
                        .build())
                .build());

        additionalInfoPlayerSet = new HashSet<>();
        additionalInfoPlayerSet.add(TypeInfo.FOLDED_BEFORE_FLOP);
        additionalInfoPlayerSet.add(TypeInfo.DID_NOT_BET);
        expectedHand.getSeats().put(Player.builder().nickname("xTheWindelPilot").build(),
                Seat
                .builder()
                .seatId(3)
                .player(Player.builder().nickname("xTheWindelPilot").build())
                .stack(1268L)
                .summary(Summary
                        .builder()
                        .seatId(3)
                        .additionalInfoPlayerSet(additionalInfoPlayerSet)
                        .value(null)
                        .build())
                .build());

        additionalInfoPlayerSet = new HashSet<>();
        additionalInfoPlayerSet.add(TypeInfo.FOLDED_BEFORE_FLOP);
        additionalInfoPlayerSet.add(TypeInfo.DID_NOT_BET);
        expectedHand.getSeats().put(Player.builder().nickname("schlier4").build(),
                Seat
                .builder()
                .seatId(4)
                .summary(Summary
                        .builder()
                        .seatId(4)
                        .additionalInfoPlayerSet(additionalInfoPlayerSet)
                        .value(null)
                        .build())
                .player(Player.builder().nickname("schlier4").build())
                .stack(8186L)
                .build());

        additionalInfoPlayerSet = new HashSet<>();
        additionalInfoPlayerSet.add(TypeInfo.BUTTON);
        additionalInfoPlayerSet.add(TypeInfo.COLLECTED);
        expectedHand.getSeats().put(Player.builder().nickname("mjmj1971").build(),
                Seat
                .builder()
                .seatId(5)
                .player(Player.builder().nickname("mjmj1971").build())
                .stack(10998L)
                .summary(Summary
                        .builder()
                        .seatId(5)
                        .additionalInfoPlayerSet(additionalInfoPlayerSet)
                        .value(440L)
                        .build())
                .build());

        additionalInfoPlayerSet = new HashSet<>();
        additionalInfoPlayerSet.add(TypeInfo.SMALL_BLIND);
        additionalInfoPlayerSet.add(TypeInfo.FOLDED_BEFORE_FLOP);
        expectedHand.getSeats().put(Player.builder().nickname("GunDolfAA").build(),
                Seat
                .builder()
                .seatId(6)
                .player(Player.builder().nickname("GunDolfAA").build())
                .stack(4523L)
                .summary(Summary
                        .builder()
                        .seatId(6)
                        .additionalInfoPlayerSet(additionalInfoPlayerSet)
                        .value(null)
                        .build())
                .build());

        additionalInfoPlayerSet = new HashSet<>();
        additionalInfoPlayerSet.add(TypeInfo.BIG_BLIND);
        additionalInfoPlayerSet.add(TypeInfo.FOLDED_ON_THE_RIVER);
        expectedHand.getSeats().put(Player.builder().nickname("Oliver N76").build(),
                Seat
                .builder()
                .seatId(7)
                .player(Player.builder().nickname("Oliver N76").build())
                .stack(13836L)
                .summary(Summary
                        .builder()
                        .seatId(7)
                        .additionalInfoPlayerSet(additionalInfoPlayerSet)
                        .value(null)
                        .build())
                .build());

        additionalInfoPlayerSet = new HashSet<>();
        additionalInfoPlayerSet.add(TypeInfo.FOLDED_BEFORE_FLOP);
        additionalInfoPlayerSet.add(TypeInfo.DID_NOT_BET);
        expectedHand.getSeats().put(Player.builder().nickname("H3ll5cream").build(),
                Seat
                .builder()
                .seatId(8)
                .player(Player.builder().nickname("H3ll5cream").build())
                .stack(2717L)
                .summary(Summary
                        .builder()
                        .seatId(8)
                        .additionalInfoPlayerSet(additionalInfoPlayerSet)
                        .value(null)
                        .build())
                .build());

        additionalInfoPlayerSet = new HashSet<>();
        additionalInfoPlayerSet.add(TypeInfo.FOLDED_BEFORE_FLOP);
        additionalInfoPlayerSet.add(TypeInfo.DID_NOT_BET);
        expectedHand.getSeats().put(Player.builder().nickname("jcarlos.vale").build(),
                Seat
                .builder()
                .seatId(9)
                .player(Player.builder().nickname("jcarlos.vale").build())
                .stack(5000L)
                .summary(Summary
                        .builder()
                        .seatId(9)
                        .additionalInfoPlayerSet(additionalInfoPlayerSet)
                        .value(null)
                        .build())
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

        //PRE FLOP
        expectedHand.getSeats().get(Player.builder().nickname("jcarlos.vale").build())
                .setHoldCards(
                        HoldCards
                                .builder()
                                .player(Player.builder().nickname("jcarlos.vale").build())
                                .card1("Kc")
                                .card2("7d")
                                .build());
        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("H3ll5cream").build())
                .typeStreet(TypeStreet.PREFLOP)
                .typeAction(TypeAction.FOLD)
                .value(0L)
                .build());
        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("jcarlos.vale").build())
                .typeStreet(TypeStreet.PREFLOP)
                .typeAction(TypeAction.FOLD)
                .value(0L)
                .build());
        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("W SERENA").build())
                .typeStreet(TypeStreet.PREFLOP)
                .typeAction(TypeAction.CALL)
                .value(100L)
                .build());
        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("matalaha").build())
                .typeStreet(TypeStreet.PREFLOP)
                .typeAction(TypeAction.FOLD)
                .value(0L)
                .build());
        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("xTheWindelPilot").build())
                .typeStreet(TypeStreet.PREFLOP)
                .typeAction(TypeAction.FOLD)
                .value(0L)
                .build());
        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("schlier4").build())
                .typeStreet(TypeStreet.PREFLOP)
                .typeAction(TypeAction.FOLD)
                .value(0L)
                .build());
        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("mjmj1971").build())
                .typeStreet(TypeStreet.PREFLOP)
                .typeAction(TypeAction.CALL)
                .value(100L)
                .build());
        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("GunDolfAA").build())
                .typeStreet(TypeStreet.PREFLOP)
                .typeAction(TypeAction.FOLD)
                .value(0L)
                .build());
        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("Oliver N76").build())
                .typeStreet(TypeStreet.PREFLOP)
                .typeAction(TypeAction.CHECK)
                .value(0L)
                .build());

        //FLOP
        expectedHand.setFlop(Flop.builder().card1("7s").card2("5h").card3("Jc").build());
        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("Oliver N76").build())
                .typeStreet(TypeStreet.FLOP)
                .typeAction(TypeAction.CHECK)
                .value(0L)
                .build());
        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("W SERENA").build())
                .typeStreet(TypeStreet.FLOP)
                .typeAction(TypeAction.CHECK)
                .value(0L)
                .build());
        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("mjmj1971").build())
                .typeStreet(TypeStreet.FLOP)
                .typeAction(TypeAction.CHECK)
                .value(0L)
                .build());

        //TURN
        expectedHand.setTurn(Turn.builder().card("Qd").build());
        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("Oliver N76").build())
                .typeStreet(TypeStreet.TURN)
                .typeAction(TypeAction.CHECK)
                .value(0L)
                .build());
        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("W SERENA").build())
                .typeStreet(TypeStreet.TURN)
                .typeAction(TypeAction.CHECK)
                .value(0L)
                .build());
        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("mjmj1971").build())
                .typeStreet(TypeStreet.TURN)
                .typeAction(TypeAction.CHECK)
                .value(0L)
                .build());

        //RIVER
        expectedHand.setRiver(River.builder().card("8d").build());

        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("Oliver N76").build())
                .typeStreet(TypeStreet.RIVER)
                .typeAction(TypeAction.CHECK)
                .value(0L)
                .build());
        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("W SERENA").build())
                .typeStreet(TypeStreet.RIVER)
                .typeAction(TypeAction.CHECK)
                .value(0L)
                .build());
        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("mjmj1971").build())
                .typeStreet(TypeStreet.RIVER)
                .typeAction(TypeAction.BETS)
                .value(300L)
                .build());
        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("Oliver N76").build())
                .typeStreet(TypeStreet.RIVER)
                .typeAction(TypeAction.FOLD)
                .value(0L)
                .build());
        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("W SERENA").build())
                .typeStreet(TypeStreet.RIVER)
                .typeAction(TypeAction.FOLD)
                .value(0L)
                .build());
        expectedHand.getActions().add(Action
                .builder()
                .player(Player.builder().nickname("mjmj1971").build())
                .typeStreet(TypeStreet.RIVER)
                .typeAction(TypeAction.NO_SHOW_HAND)
                .value(0L)
                .build());

        expectedHand.getAdditionalInfoPlayerList()
                .add(AdditionalInfoPlayer.builder()
                        .info(TypeInfo.UNCALLED_BET)
                        .value(300L)
                        .player(Player.builder().nickname("mjmj1971").build())
                        .build());

        expectedHand.getAdditionalInfoPlayerList()
                .add(AdditionalInfoPlayer.builder()
                        .info(TypeInfo.COLLECTED)
                        .value(440L)
                        .player(Player.builder().nickname("mjmj1971").build())
                        .build());

        //SUMMARY
        expectedHand.setTotalPot(440L);
        expectedHand.setBoard(Board.builder()
                .card1("7s")
                .card2("5h")
                .card3("Jc")
                .card4("Qd")
                .card5("8d")
                .build());

        List<Hand> expectedHandList = new ArrayList<>();
        expectedHandList.add(expectedHand);

        System.out.println(expectedHandList);

        System.out.println(fileReaderProcessor.getHandList());

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
