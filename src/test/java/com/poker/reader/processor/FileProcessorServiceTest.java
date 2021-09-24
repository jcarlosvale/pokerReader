package com.poker.reader.processor;

import static com.poker.reader.parser.util.DtoOperationsUtil.getCountShowdownCards;

import com.poker.reader.domain.service.FileProcessorService;
import com.poker.reader.dto.AnalysedPlayer;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FileProcessorServiceTest {

    @InjectMocks
    private FileProcessorService fileProcessorService;

    @Test
    void extractUsers() {
        //given
        List<String> lines = List.of(
                "PokerStars Hand #221329367981: Tournament #3067368972, $0.25+$0.24+$0.06 USD Hold'em No Limit - Level IV (30/60) - 2020/12/11 19:52:21 EET [2020/12/11 12:52:21 ET]",
                "Table '3067368972 53' 9-max Seat #1 is the button",
                "Seat 1: Reinerownz (9988 in chips, $0.36 bounty)",
                "Seat 2: the joker726 (12201 in chips, $0.48 bounty)",
                "Seat 3: DiegoGutibr (7176 in chips, $0.24 bounty)",
                "Seat 4: Serega777690 (10147 in chips, $0.36 bounty)",
                "Seat 5: Aleks-080881 (4875 in chips, $0.24 bounty)",
                "Seat 6: 19902504 (5294 in chips, $0.24 bounty)",
                "Seat 7: JaneB551 (4754 in chips, $0.24 bounty)",
                "Seat 8: jcarlos.vale (5000 in chips, $0.24 bounty)",
                "Seat 9: 18ronny18 (4715 in chips, $0.24 bounty) is sitting out",
                "Reinerownz: posts the ante 9",
                "the joker726: posts the ante 9",
                "DiegoGutibr: posts the ante 9",
                "Serega777690: posts the ante 9",
                "Aleks-080881: posts the ante 9",
                "19902504: posts the ante 9",
                "JaneB551: posts the ante 9",
                "jcarlos.vale: posts the ante 9",
                "18ronny18: posts the ante 9",
                "the joker726: posts small blind 30",
                "DiegoGutibr: posts big blind 60");

        Set<String> expectedPlayers =
                Set.of("Reinerownz",
                        "the joker726",
                        "DiegoGutibr",
                        "Serega777690",
                        "Aleks-080881",
                        "19902504",
                        "JaneB551",
                        "jcarlos.vale",
                        "18ronny18");

        //when
        Set<String> players = fileProcessorService.extractPlayers(lines);

        //then
        Assertions.assertThat(players).containsExactlyInAnyOrderElementsOf(expectedPlayers);

    }
/*
        @BeforeEach
        void setup() {
            fileProcessorService = new FileProcessorService();
        }

        @Test
        void processOneHandNoshowdown() throws IOException {
            //GIVEN
            Resource resource = new ClassPathResource("one-hand.txt", getClass().getClassLoader());
            List<String> lines = FileUtils.readLines(resource.getFile(), "utf-8");
            Set<String> expectedPlayers =
                    Set.of("W SERENA",
                            "matalaha",
                            "xTheWindelPilot",
                            "schlier4",
                            "mjmj1971",
                            "GunDolfAA",
                            "Oliver N76",
                            "H3ll5cream",
                            "jcarlos.vale");

            //WHEN
            FileProcessedDto fileProcessed = fileProcessorService.importFile(fileName, lines);

            //THEN
            assertThat(fileProcessed.getTournament()).isEqualTo("2779056951");
            assertThat(fileProcessed.getPlayers()).containsExactlyInAnyOrderElementsOf(expectedPlayers);
            assertThat(fileProcessed.getTotalHands()).isEqualTo(1);
        }

        @Test
        void processTwoHandsNoshowdown() throws IOException {
            //GIVEN
            Resource resource = new ClassPathResource("two-hand.txt", getClass().getClassLoader());
            List<String> lines = FileUtils.readLines(resource.getFile(), "utf-8");
            Set<String> expectedPlayers =
                    Set.of("W SERENA",
                            "matalaha",
                            "xTheWindelPilot",
                            "schlier4",
                            "mjmj1971",
                            "GunDolfAA",
                            "Oliver N76",
                            "H3ll5cream",
                            "jcarlos.vale",
                            "LauriOy",
                            "LEE MARVIN17",
                            "Gerason93",
                            "Gj,tlf777",
                            "SoChute",
                            "Phisie",
                            "Elena19years",
                            "evstraliss");

            //WHEN
            FileProcessedDto fileProcessed = fileProcessorService.importFile(fileName, lines);

            //THEN
            assertThat(fileProcessed.getTournament()).isEqualTo("2779056951");
            assertThat(fileProcessed.getPlayers())
                    .containsExactlyInAnyOrderElementsOf(expectedPlayers);
            assertThat(fileProcessed.getTotalHands()).isEqualTo(2);
        }

        @Test
        void processOneHandShowdown() throws IOException {
            //GIVEN
            Resource resource = new ClassPathResource("one-hand-showdown.txt", getClass().getClassLoader());
            List<String> lines = FileUtils.readLines(resource.getFile(), "utf-8");
            Set<String> expectedPlayers =
                    Set.of("FlyingButche",
                            "tEddy-KBG 77",
                            "dimYLiaK",
                            "andrey pyatkin",
                            "ErickSayajin",
                            "clonharek704",
                            "AyrtonAA95",
                            "(ANEKDOT)777",
                            "jcarlos.vale");

            //WHEN
            FileProcessedDto fileProcessed = fileProcessorService.importFile(fileName, lines);

            //THEN
            assertThat(fileProcessed.getTournament()).isEqualTo("3060068759");
            assertThat(fileProcessed.getPlayers())
                    .containsExactlyInAnyOrderElementsOf(expectedPlayers);
            assertThat(fileProcessed.getAnalysedPlayers().contains(mockAnalysedPlayer("FlyingButche", "Ad Qh")))
                    .isTrue();
            assertThat(fileProcessed.getTotalHands()).isEqualTo(1);
        }



        @Test
        void processHugeFileExample() throws IOException {
            //GIVEN
            Resource resource = new ClassPathResource("huge-file.txt", getClass().getClassLoader());
            List<String> lines = FileUtils.readLines(resource.getFile(), "utf-8");

            //WHEN
            FileProcessedDto fileProcessed = fileProcessorService.importFile(fileName, lines);

            //THEN
            assertThat(fileProcessed.getTournament()).isEqualTo("3082657132");
            assertThat(fileProcessed.getPlayers().size()).isEqualTo(67);
            assertThat(fileProcessed.getAnalysedPlayers()).hasSize(62);
            assertThat(fileProcessed.getTotalHands()).isEqualTo(396);
            assertThat(countHands(fileProcessed.getAnalysedPlayers())).isEqualTo(239);
        }

        @Test
        void evaluateFileException() throws IOException {
            //GIVEN
            Resource resource = new ClassPathResource("exception-case.txt", getClass().getClassLoader());
            List<String> lines = FileUtils.readLines(resource.getFile(), "utf-8");

            //WHEN
            FileProcessedDto fileProcessed = fileProcessorService.importFile(fileName, lines);

            //THEN
            assertThat(fileProcessed.getPlayers().size()).isEqualTo(4);
            assertThat(fileProcessed.getAnalysedPlayers()).hasSize(49);
            assertThat(fileProcessed.getTotalHands()).isEqualTo(156);
            assertThat(countHands(fileProcessed.getAnalysedPlayers())).isEqualTo(129);
        }
    */
    private int countHands(Collection<AnalysedPlayer> analysedPlayerCollection) {
        int count = 0;
        for(AnalysedPlayer analysedPlayer:analysedPlayerCollection) {
            count += getCountShowdownCards(analysedPlayer.getNormalisedCardsMap());
        }
        return count;
    }

}