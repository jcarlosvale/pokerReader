package com.poker.reader.parser;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Set;
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
                .hasSize(expectedPlayers.size())
                .hasSameElementsAs(expectedPlayers);
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
                .hasSize(expectedPlayers.size())
                .hasSameElementsAs(expectedPlayers);
        assertThat(fileProcessor.getHandsOfPlayers())
                .isEmpty();
        assertThat(fileProcessor.getHands())
                .hasSize(2);
    }
}