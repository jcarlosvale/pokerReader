package com.poker.reader.parser;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

class FileProcessorTest {

    private final FileProcessor fileProcessor = new FileProcessor();

    @Test
    void loadPlayersOneHand() throws IOException {
        //GIVEN
        Resource resource = new ClassPathResource("one-hand.txt", getClass().getClassLoader());
        List<String> lines = FileUtils.readLines(resource.getFile(), "utf-8");
        Set<String> expectedResult =
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
        Set<String> actualResult = fileProcessor.loadPlayers(lines);

        //THEN
        assertThat(actualResult)
                .hasSize(expectedResult.size())
                .hasSameElementsAs(expectedResult);
    }

    @Test
    void loadPlayersTwoHands() throws IOException {
        //GIVEN
        Resource resource = new ClassPathResource("two-hands.txt", getClass().getClassLoader());
        List<String> lines = FileUtils.readLines(resource.getFile(), "utf-8");
        Set<String> expectedResult =
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
        Set<String> actualResult = fileProcessor.loadPlayers(lines);

        //THEN
        assertThat(actualResult)
                .hasSize(expectedResult.size())
                .hasSameElementsAs(expectedResult);
    }

    @Test
    void loadPlayersEmptyLines() {
        //GIVEN

        //WHEN
        Set<String> actualResult = fileProcessor.loadPlayers(List.of());

        //THEN
        assertThat(actualResult)
                .isEmpty();
    }

    @Test
    void loadPlayersNullLines() {
        //GIVEN

        //WHEN
        Set<String> actualResult = fileProcessor.loadPlayers(null);

        //THEN
        assertThat(actualResult)
                .isEmpty();
    }

}