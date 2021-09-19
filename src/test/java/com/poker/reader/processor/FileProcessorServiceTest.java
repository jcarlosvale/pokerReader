package com.poker.reader.processor;

import com.poker.reader.dto.AnalysedPlayer;

import java.util.Collection;

import static com.poker.reader.parser.util.DtoOperationsUtil.getCountShowdownCards;

class FileProcessorServiceTest {
    /*
        private FileProcessorService fileProcessorService;

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
            FileProcessedDto fileProcessed = fileProcessorService.process(fileName, lines);

            //THEN
            assertThat(fileProcessed.getTournament()).isEqualTo("2779056951");
            assertThat(fileProcessed.getPlayers()).containsExactlyInAnyOrderElementsOf(expectedPlayers);
            assertThat(fileProcessed.getTotalHands()).isEqualTo(1);
        }

        @Test
        void processTwoHandsNoshowdown() throws IOException {
            //GIVEN
            Resource resource = new ClassPathResource("two-hands.txt", getClass().getClassLoader());
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
            FileProcessedDto fileProcessed = fileProcessorService.process(fileName, lines);

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
            FileProcessedDto fileProcessed = fileProcessorService.process(fileName, lines);

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
            FileProcessedDto fileProcessed = fileProcessorService.process(fileName, lines);

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
            FileProcessedDto fileProcessed = fileProcessorService.process(fileName, lines);

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