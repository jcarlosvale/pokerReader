package com.poker.reader.processor;

import com.poker.reader.dto.AnalysedPlayer;
import com.poker.reader.dto.NormalisedCardsDto;
import com.poker.reader.parser.util.DtoOperationsUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;

@Log4j2
public class FileHtmlProcessor {

    public static void updatePlayersTableFile(List<AnalysedPlayer> playerList, String directoryPath) throws IOException {
        log.info("Generating file...");
        Resource resource = new ClassPathResource("/html/headerTable.txt", FileHtmlProcessor.class.getClassLoader());
        List<String> lines = FileUtils.readLines(resource.getFile(), "utf-8");
        lines.add(generatePlayersTable(playerList));
        resource = new ClassPathResource("/html/headerTable.txt", FileHtmlProcessor.class.getClassLoader());
        lines.addAll(FileUtils.readLines(resource.getFile(), "utf-8"));

        File fileToGenerate = new File(directoryPath + File.separator + "players.html");
        FileUtils.write(fileToGenerate, String.join("\n", lines), "UTF-8");
        log.info("file generated: " + fileToGenerate.getAbsolutePath());
    }

    public static String generatePlayersTable(List<AnalysedPlayer> playerList) {
        return generateTableHead() + "\n" + generatePlayersTableRows(playerList);
    }

    private static String generateTableHead() {
        String dateTimeString = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss").format(LocalDateTime.now());
        return thead(tr(
                th("player"),
                th("chen avg"),
                th("# hands"),
                th("cards" + " " + dateTimeString))).render();
    }

    private static String generatePlayersTableRows(List<AnalysedPlayer> playerList) {
        //alphabetically
        playerList.sort(Comparator.comparing(AnalysedPlayer::getPlayer));
        return tbody(
                each(playerList, (integer, analysedPlayer) -> {
                    long avgChenValue = DtoOperationsUtil.getAverageChen(analysedPlayer.getNormalisedCardsMap());
                    String className = classNameFromChenValue(avgChenValue);
                    return tr(attrs(className),
                        td(analysedPlayer.getPlayer()),
                        td(String.valueOf(avgChenValue)),
                        td(String.valueOf(DtoOperationsUtil.getCountShowdownCards(analysedPlayer.getNormalisedCardsMap()))),
                        td(formatCards(analysedPlayer.getNormalisedCardsMap())));
                }
                )).render();
    }

    private static String classNameFromChenValue(long avgChenValue) {
        if (avgChenValue >= 10) return ".bg-primary";
        if (avgChenValue >= 8) return ".bg-success";
        if (avgChenValue >= 5) return ".table-warning";
        return ".bg-danger";
    }

    private static String formatCards(Map<NormalisedCardsDto, Integer> normalisedCardsMap) {
        TreeMap<NormalisedCardsDto, Integer> treeMap = new TreeMap<>(normalisedCardsMap);

        return treeMap.entrySet().stream()
                .map(FileHtmlProcessor::toString)
                .collect(Collectors.joining(", "));
    }

    private static String toString(Entry<NormalisedCardsDto, Integer> entry) {
        return entry.getValue() + "x" + toString(entry.getKey());
    }

    private static String toString(NormalisedCardsDto normalisedCardsDto) {
        String suited = "";
        if (!normalisedCardsDto.isPair()) {
            if (normalisedCardsDto.isSuited()) suited = "s";
            else suited = "o";
        }
        return
                String.valueOf(normalisedCardsDto.getCard1()) + normalisedCardsDto.getCard2() + suited;
    }

    public static void updatePlayersTableFile(List<AnalysedPlayer> playerList, String tournament, String directoryPath)
            throws IOException {
        log.info("Generating file...");
        Resource resource = new ClassPathResource("/html/headerTable.txt", FileHtmlProcessor.class.getClassLoader());
        List<String> lines = FileUtils.readLines(resource.getFile(), "utf-8");
        lines.add(generatePlayersTable(playerList));
        resource = new ClassPathResource("/html/headerTable.txt", FileHtmlProcessor.class.getClassLoader());
        lines.addAll(FileUtils.readLines(resource.getFile(), "utf-8"));

        File fileToGenerate = new File(directoryPath + File.separator + "players-" + tournament + ".html");
        FileUtils.write(fileToGenerate, String.join("\n", lines), "UTF-8");
        log.info("file generated: " + fileToGenerate.getAbsolutePath());
    }
}
