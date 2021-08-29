package processor;

import dto.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static dto.TypeInfo.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static parser.FileParserUtil.DATE_TIME_FORMAT;

class FileReaderProcessorTest {

    FileReaderProcessor fileReaderProcessor = new FileReaderProcessor();

    @BeforeAll
    public static void changeLogLevel() {
        final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.setLevel(Level.INFO);
    }

    @Test
    void processOneHandTest() throws IOException {
        Resource resource = new ClassPathResource("one-hand.txt", getClass().getClassLoader());
        fileReaderProcessor.processFile(resource.getFile().getAbsolutePath());

        TournamentDTO expectedTournamentDTO = TournamentDTO.builder().id(2779056951L).buyIn(BigDecimal.valueOf(0.49)).build();
        HandDTO expectedHandDTO = HandDTO
                .builder()
                .id(208296842229L)
                .tournamentDTO(expectedTournamentDTO)
                .level("VI")
                .smallBlind(50)
                .bigBlind(100)
                .date(LocalDate.parse("2020/01/18", DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)))
                .tableId("2779056951 40")
                .button(5)
                .build();

        SeatDTO seatDTO =
                SeatDTO
                .builder()
                .seatId(1)
                .playerDTO(PlayerDTO.builder().nickname("W SERENA").build())
                .stack(5000L)
                .build();
        seatDTO.getInfoPlayerAtHandList().add(
                InfoPlayerAtHand.builder()
                .info(FOLDED_ON_THE_RIVER)
                .build());
        expectedHandDTO.getSeats().put(PlayerDTO.builder().nickname("W SERENA").build(), seatDTO);


        seatDTO =
                SeatDTO
                .builder()
                .seatId(2)
                .playerDTO(PlayerDTO.builder().nickname("matalaha").build())
                .stack(6917L)
                .build();
        seatDTO.getInfoPlayerAtHandList().add(
                InfoPlayerAtHand.builder()
                        .info(FOLDED_BEFORE_FLOP)
                        .build());
        expectedHandDTO.getSeats().put(PlayerDTO.builder().nickname("matalaha").build(), seatDTO);



        seatDTO =
                SeatDTO
                .builder()
                .seatId(3)
                .playerDTO(PlayerDTO.builder().nickname("xTheWindelPilot").build())
                .stack(1268L)
                .build();
        seatDTO.getInfoPlayerAtHandList().add(
                InfoPlayerAtHand.builder()
                        .info(FOLDED_BEFORE_FLOP)
                        .build());
        expectedHandDTO.getSeats().put(PlayerDTO.builder().nickname("xTheWindelPilot").build(), seatDTO);


        seatDTO =
                SeatDTO
                .builder()
                .seatId(4)
                .playerDTO(PlayerDTO.builder().nickname("schlier4").build())
                .stack(8186L)
                .build();
        seatDTO.getInfoPlayerAtHandList().add(
                InfoPlayerAtHand.builder()
                .info(FOLDED_BEFORE_FLOP)
                .build());
        expectedHandDTO.getSeats().put(PlayerDTO.builder().nickname("schlier4").build(), seatDTO);

        seatDTO =
                SeatDTO
                .builder()
                .seatId(5)
                .playerDTO(PlayerDTO.builder().nickname("mjmj1971").build())
                .stack(10998L)
                .build();
        seatDTO.getInfoPlayerAtHandList().add(
                InfoPlayerAtHand.builder()
                .info(BUTTON)
                .build());
        seatDTO.getInfoPlayerAtHandList().add(
                InfoPlayerAtHand.builder()
                .info(COLLECTED)
                .value(440L)
                .build());
        expectedHandDTO.getSeats().put(PlayerDTO.builder().nickname("mjmj1971").build(), seatDTO);

        seatDTO =
                SeatDTO
                .builder()
                .seatId(6)
                .playerDTO(PlayerDTO.builder().nickname("GunDolfAA").build())
                .stack(4523L)
                .build();
        seatDTO.getInfoPlayerAtHandList().add(
                InfoPlayerAtHand.builder()
                .info(SMALL_BLIND)
                .build());
        seatDTO.getInfoPlayerAtHandList().add(
                InfoPlayerAtHand.builder()
                .info(FOLDED_BEFORE_FLOP)
                .build());
        expectedHandDTO.getSeats().put(PlayerDTO.builder().nickname("GunDolfAA").build(), seatDTO);

        seatDTO =
                SeatDTO
                .builder()
                .seatId(7)
                .playerDTO(PlayerDTO.builder().nickname("Oliver N76").build())
                .stack(13836L)
                .build();
        seatDTO.getInfoPlayerAtHandList().add(
                InfoPlayerAtHand.builder()
                .info(BIG_BLIND)
                .build());
        seatDTO.getInfoPlayerAtHandList().add(
                InfoPlayerAtHand.builder()
                .info(FOLDED_ON_THE_RIVER)
                .build());
        expectedHandDTO.getSeats().put(PlayerDTO.builder().nickname("Oliver N76").build(), seatDTO);

        seatDTO =
                SeatDTO
                .builder()
                .seatId(8)
                .playerDTO(PlayerDTO.builder().nickname("H3ll5cream").build())
                .stack(2717L)
                .build();
        seatDTO.getInfoPlayerAtHandList().add(
                InfoPlayerAtHand.builder()
                .info(FOLDED_BEFORE_FLOP)
                .build());
        expectedHandDTO.getSeats().put(PlayerDTO.builder().nickname("H3ll5cream").build(), seatDTO);

        seatDTO =
                SeatDTO
                .builder()
                .seatId(9)
                .playerDTO(PlayerDTO.builder().nickname("jcarlos.vale").build())
                .stack(5000L)
                .build();
        seatDTO.getInfoPlayerAtHandList().add(
                InfoPlayerAtHand.builder()
                .info(FOLDED_BEFORE_FLOP)
                .build());
        expectedHandDTO.getSeats().put(PlayerDTO.builder().nickname("jcarlos.vale").build(), seatDTO);

        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("W SERENA").build())
                .value(10L)
                .typeStreet(TypeStreet.ANTE)
                .typeAction(TypeAction.ANTE)
                .build());

        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("matalaha").build())
                .value(10L)
                .typeStreet(TypeStreet.ANTE)
                .typeAction(TypeAction.ANTE)
                .build());

        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("xTheWindelPilot").build())
                .value(10L)
                .typeStreet(TypeStreet.ANTE)
                .typeAction(TypeAction.ANTE)
                .build());

        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("schlier4").build())
                .value(10L)
                .typeStreet(TypeStreet.ANTE)
                .typeAction(TypeAction.ANTE)
                .build());

        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("mjmj1971").build())
                .value(10L)
                .typeStreet(TypeStreet.ANTE)
                .typeAction(TypeAction.ANTE)
                .build());

        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("GunDolfAA").build())
                .value(10L)
                .typeStreet(TypeStreet.ANTE)
                .typeAction(TypeAction.ANTE)
                .build());

        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("Oliver N76").build())
                .value(10L)
                .typeStreet(TypeStreet.ANTE)
                .typeAction(TypeAction.ANTE)
                .build());

        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("H3ll5cream").build())
                .value(10L)
                .typeStreet(TypeStreet.ANTE)
                .typeAction(TypeAction.ANTE)
                .build());

        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("jcarlos.vale").build())
                .value(10L)
                .typeStreet(TypeStreet.ANTE)
                .typeAction(TypeAction.ANTE)
                .build());

        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("GunDolfAA").build())
                .value(50L)
                .typeStreet(TypeStreet.ANTE)
                .typeAction(TypeAction.SMALL_BLIND)
                .build());

        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("Oliver N76").build())
                .value(100L)
                .typeStreet(TypeStreet.ANTE)
                .typeAction(TypeAction.BIG_BLIND)
                .build());

        //PRE FLOP
        expectedHandDTO.getSeats().get(PlayerDTO.builder().nickname("jcarlos.vale").build())
                .setHoldCards(
                        HoldCards
                                .builder()
                                .playerDTO(PlayerDTO.builder().nickname("jcarlos.vale").build())
                                .card1("Kc")
                                .card2("7d")
                                .build());
        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("H3ll5cream").build())
                .typeStreet(TypeStreet.PREFLOP)
                .typeAction(TypeAction.FOLD)
                .value(0L)
                .build());
        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("jcarlos.vale").build())
                .typeStreet(TypeStreet.PREFLOP)
                .typeAction(TypeAction.FOLD)
                .value(0L)
                .build());
        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("W SERENA").build())
                .typeStreet(TypeStreet.PREFLOP)
                .typeAction(TypeAction.CALL)
                .value(100L)
                .build());
        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("matalaha").build())
                .typeStreet(TypeStreet.PREFLOP)
                .typeAction(TypeAction.FOLD)
                .value(0L)
                .build());
        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("xTheWindelPilot").build())
                .typeStreet(TypeStreet.PREFLOP)
                .typeAction(TypeAction.FOLD)
                .value(0L)
                .build());
        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("schlier4").build())
                .typeStreet(TypeStreet.PREFLOP)
                .typeAction(TypeAction.FOLD)
                .value(0L)
                .build());
        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("mjmj1971").build())
                .typeStreet(TypeStreet.PREFLOP)
                .typeAction(TypeAction.CALL)
                .value(100L)
                .build());
        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("GunDolfAA").build())
                .typeStreet(TypeStreet.PREFLOP)
                .typeAction(TypeAction.FOLD)
                .value(0L)
                .build());
        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("Oliver N76").build())
                .typeStreet(TypeStreet.PREFLOP)
                .typeAction(TypeAction.CHECK)
                .value(0L)
                .build());

        //FLOP
        expectedHandDTO.setFlop(Flop.builder().card1("7s").card2("5h").card3("Jc").build());
        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("Oliver N76").build())
                .typeStreet(TypeStreet.FLOP)
                .typeAction(TypeAction.CHECK)
                .value(0L)
                .build());
        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("W SERENA").build())
                .typeStreet(TypeStreet.FLOP)
                .typeAction(TypeAction.CHECK)
                .value(0L)
                .build());
        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("mjmj1971").build())
                .typeStreet(TypeStreet.FLOP)
                .typeAction(TypeAction.CHECK)
                .value(0L)
                .build());

        //TURN
        expectedHandDTO.setTurn(Turn.builder().card("Qd").build());
        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("Oliver N76").build())
                .typeStreet(TypeStreet.TURN)
                .typeAction(TypeAction.CHECK)
                .value(0L)
                .build());
        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("W SERENA").build())
                .typeStreet(TypeStreet.TURN)
                .typeAction(TypeAction.CHECK)
                .value(0L)
                .build());
        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("mjmj1971").build())
                .typeStreet(TypeStreet.TURN)
                .typeAction(TypeAction.CHECK)
                .value(0L)
                .build());

        //RIVER
        expectedHandDTO.setRiver(River.builder().card("8d").build());

        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("Oliver N76").build())
                .typeStreet(TypeStreet.RIVER)
                .typeAction(TypeAction.CHECK)
                .value(0L)
                .build());
        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("W SERENA").build())
                .typeStreet(TypeStreet.RIVER)
                .typeAction(TypeAction.CHECK)
                .value(0L)
                .build());
        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("mjmj1971").build())
                .typeStreet(TypeStreet.RIVER)
                .typeAction(TypeAction.BETS)
                .value(300L)
                .build());
        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("Oliver N76").build())
                .typeStreet(TypeStreet.RIVER)
                .typeAction(TypeAction.FOLD)
                .value(0L)
                .build());
        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("W SERENA").build())
                .typeStreet(TypeStreet.RIVER)
                .typeAction(TypeAction.FOLD)
                .value(0L)
                .build());
        expectedHandDTO.getActions().add(Action
                .builder()
                .playerDTO(PlayerDTO.builder().nickname("mjmj1971").build())
                .typeStreet(TypeStreet.RIVER)
                .typeAction(TypeAction.NO_SHOW_HAND)
                .value(0L)
                .build());

        //SUMMARY
        expectedHandDTO.setTotalPot(440L);
        expectedHandDTO.setBoard(Board.builder()
                .card1("7s")
                .card2("5h")
                .card3("Jc")
                .card4("Qd")
                .card5("8d")
                .build());

        List<HandDTO> expectedHandDTOList = new ArrayList<>();
        expectedHandDTOList.add(expectedHandDTO);

        //System.out.println(expectedHandDTOList);

        //System.out.println(fileReaderProcessor.getHandDTOList());

        assertEquals(expectedHandDTOList, fileReaderProcessor.getHandDTOList());
    }

    @Test
    void processMultipleHandsTest() throws IOException {
        Resource resource = new ClassPathResource("multiple-hand.txt", getClass().getClassLoader());
        fileReaderProcessor.processFile(resource.getFile().getAbsolutePath());

        TournamentDTO expectedTournamentDTO = TournamentDTO.builder().id(2779056951L).buyIn(BigDecimal.valueOf(0.49)).build();

        for(HandDTO handDTO : fileReaderProcessor.getHandDTOList()) {
            assertEquals(expectedTournamentDTO, handDTO.getTournamentDTO());
        }
        assertEquals(15, fileReaderProcessor.getHandDTOList().size());
    }
    @Test
    void verifySectionTest() {
        String line = "PokerStars Hand #208296842229: Tournament #2779056951, $0.49+$0.06 USD Hold'em No Limit - " +
                "Level VI (50/100) - 2020/01/18 22:02:11 EET [2020/01/18 15:02:11 ET]";
        TypeFileSectionEnum actual = fileReaderProcessor.verifySection(line);
        TypeFileSectionEnum expected = TypeFileSectionEnum.HEADER;
        assertEquals(expected, actual);

        line = "*** HOLE CARDS ***";
        actual = fileReaderProcessor.verifySection(line);
        expected = TypeFileSectionEnum.PRE_FLOP;
        assertEquals(expected, actual);

        line = "*** FLOP *** [7s 5h Jc]";
        actual = fileReaderProcessor.verifySection(line);
        expected = TypeFileSectionEnum.FLOP;
        assertEquals(expected, actual);

        line = "*** TURN *** [7s 5h Jc] [Qd]";
        actual = fileReaderProcessor.verifySection(line);
        expected = TypeFileSectionEnum.TURN;
        assertEquals(expected, actual);

        line = "*** RIVER *** [7s 5h Jc Qd] [8d]";
        actual = fileReaderProcessor.verifySection(line);
        expected = TypeFileSectionEnum.RIVER;
        assertEquals(expected, actual);

        line = "*** SUMMARY ***";
        actual = fileReaderProcessor.verifySection(line);
        expected = TypeFileSectionEnum.SUMMARY;
        assertEquals(expected, actual);

        line = "Seat 7: Oliver N76 (big blind) folded on the River\n";
        actual = fileReaderProcessor.verifySection(line);
        assertNull(actual);
    }

    @Test
    void readDirectoryTest() throws URISyntaxException, IOException {
        URL url = FileReaderProcessorTest.class.getClassLoader().getResource("top");
        assert url != null;
        String directory = Paths.get(url.toURI()).toString();
        List<File> files = fileReaderProcessor.readDirectory(directory);
        assertEquals(10, files.size());
        for (File file : files) {
            fileReaderProcessor.processFile(file.getAbsolutePath());
        }
    }
}
