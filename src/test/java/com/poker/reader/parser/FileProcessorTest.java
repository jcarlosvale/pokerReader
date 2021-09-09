package com.poker.reader.parser;

import com.poker.reader.dto.RawCardsDto;
import com.poker.reader.parser.util.FileProcessorUtil;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class FileProcessorTest {

    private FileProcessor fileProcessor;

    @BeforeEach
    void setup() {
        fileProcessor = new FileProcessor();
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
        fileProcessor.process(lines);

        //THEN
        assertThat(fileProcessor.getPlayers())
                .containsExactlyInAnyOrderElementsOf(expectedPlayers);
        assertThat(fileProcessor.getHandsOfPlayers())
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
        assertThat(fileProcessor.getHandsOfPlayers())
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
        Map<String, List<RawCardsDto>> expectedHandsOfPlayers = new HashMap<>();
        expectedHandsOfPlayers.put("FlyingButche", List.of(new RawCardsDto("Ad Qh")));
        expectedHandsOfPlayers.put("ErickSayajin", List.of(new RawCardsDto("3h Ah")));
        expectedHandsOfPlayers.put("andrey pyatkin", List.of(new RawCardsDto("8s Kd")));

        //WHEN
        fileProcessor.process(lines);


        //THEN
        assertThat(fileProcessor.getPlayers())
                .containsExactlyInAnyOrderElementsOf(expectedPlayers);
        assertThat(fileProcessor.getHandsOfPlayers())
                .containsExactlyEntriesOf(expectedHandsOfPlayers);
        assertThat(fileProcessor.getHands())
                .hasSize(1);
    }

    @Test
    void processHugeFileExample() throws IOException {
        //GIVEN
        Resource resource = new ClassPathResource("huge-file.txt", getClass().getClassLoader());
        List<String> lines = FileUtils.readLines(resource.getFile(), "utf-8");

        //WHEN
        fileProcessor.process(lines);

        //THEN
        assertThat(fileProcessor.getPlayers()).hasSize(67);
        assertThat(fileProcessor.getHandsOfPlayers()).hasSize(62);
        assertThat(fileProcessor.getHands()).hasSize(396);
        assertThat(FileProcessorUtil.countHands(fileProcessor.getHandsOfPlayers())).isEqualTo(239);
        System.out.println(fileProcessor.getAnalysis());
    }

    @Test
    void evaluateFileException() throws IOException {
        //GIVEN
        Resource resource = new ClassPathResource("exception-case.txt", getClass().getClassLoader());
        List<String> lines = FileUtils.readLines(resource.getFile(), "utf-8");

        //WHEN
        fileProcessor.process(lines);

        //THEN
        assertThat(fileProcessor.getPlayers()).hasSize(71);
        assertThat(fileProcessor.getHandsOfPlayers()).hasSize(57);
        assertThat(fileProcessor.getHands()).hasSize(203);
        assertThat(FileProcessorUtil.countHands(fileProcessor.getHandsOfPlayers())).isEqualTo(145);
        System.out.println(fileProcessor.getAnalysis());
    }


}