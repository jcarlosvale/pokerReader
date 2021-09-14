package com.poker.reader.parser;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poker.reader.dto.AnalysedPlayer;
import com.poker.reader.dto.FileProcessedDto;
import com.poker.reader.dto.NormalisedCardsDto;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

class FileProcessorTest {

    private FileProcessor fileProcessor;

    @BeforeEach
    void setup() {
        fileProcessor = new FileProcessor();
    }
/*
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
        fileProcessor.process(lines);

        //THEN
        assertThat(fileProcessor.getPlayers())
                .containsExactlyInAnyOrderElementsOf(expectedPlayers);
        assertThat(fileProcessor.getAnalysedPlayerMap())
                .isEmpty();
        assertThat(fileProcessor.getHands())
                .hasSize(1);
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
        fileProcessor.process(lines);

        //THEN
        assertThat(fileProcessor.getPlayers())
                .containsExactlyInAnyOrderElementsOf(expectedPlayers);
        assertThat(fileProcessor.getAnalysedPlayerMap())
                .isEmpty();
        assertThat(fileProcessor.getHands())
                .hasSize(2);
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
        Map<String, AnalysedPlayer> expectedHandsOfPlayers = new HashMap<>();
        expectedHandsOfPlayers.put("FlyingButche", mockAnalysedPlayer("FlyingButche", "Ad Qh"));
        expectedHandsOfPlayers.put("ErickSayajin", mockAnalysedPlayer("ErickSayajin", "3h Ah"));
        expectedHandsOfPlayers.put("andrey pyatkin", mockAnalysedPlayer("andrey pyatkin", "8s Kd"));

        //WHEN
        FileProcessedDto fileProcessed = fileProcessor.process(lines).get();

        //THEN
        assertThat(fileProcessed.getAnalysedPlayers())
                .containsExactlyInAnyOrderElementsOf(expectedPlayers);
        assertThat(fileProcessed.getAnalysedPlayerMap())
                .containsExactlyEntriesOf(expectedHandsOfPlayers);
        assertThat(fileProcessed.getHands())
                .hasSize(1);
    }

 */

    @Test
    void processHugeFileExample() throws IOException {
        //GIVEN
        Resource resource = new ClassPathResource("huge-file.txt", getClass().getClassLoader());
        List<String> lines = FileUtils.readLines(resource.getFile(), "utf-8");

        //WHEN
        FileProcessedDto fileProcessed = fileProcessor.process(lines).get();

        //THEN
        assertThat(fileProcessed.getTotalPlayers()).isEqualTo(67);
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
        FileProcessedDto fileProcessed = fileProcessor.process(lines).get();

        //THEN
        assertThat(fileProcessed.getTotalPlayers()).isEqualTo(4);
        assertThat(fileProcessed.getAnalysedPlayers()).hasSize(49);
        assertThat(fileProcessed.getTotalHands()).isEqualTo(156);
        assertThat(countHands(fileProcessed.getAnalysedPlayers())).isEqualTo(129);
    }

    @Test
    void analysedPlayers() throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();

        //GIVEN
        Resource resource = new ClassPathResource("exception-case.txt", getClass().getClassLoader());
        List<String> lines = FileUtils.readLines(resource.getFile(), "utf-8");

        //WHEN
        fileProcessor.process(lines);

        //THEN
    }

    private AnalysedPlayer mockAnalysedPlayer(String player, String cards) {
        TreeMap<NormalisedCardsDto, Integer> treeMap = new TreeMap<>();
        treeMap.put(new NormalisedCardsDto(cards), 1);
        return AnalysedPlayer.builder().player(player).normalisedCardsMap(treeMap).build();
    }

    private int countHands(Collection<AnalysedPlayer> analysedPlayerCollection) {
        int count = 0;
        for(AnalysedPlayer analysedPlayer:analysedPlayerCollection) {
            count += analysedPlayer.getCountShowdownCards();
        }
        return count;
    }

}