package com.poker.reader.parser.processor;

import static j2html.TagCreator.attrs;
import static j2html.TagCreator.each;
import static j2html.TagCreator.tbody;
import static j2html.TagCreator.td;
import static j2html.TagCreator.th;
import static j2html.TagCreator.thead;
import static j2html.TagCreator.tr;

import com.poker.reader.dto.AnalysedPlayer;
import com.poker.reader.dto.NormalisedCardsDto;
import com.poker.reader.parser.util.DtoOperationsUtil;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Log4j2
public class FileHtmlProcessor {

    public static void updatePlayersTableFile(List<AnalysedPlayer> playerList) throws IOException {
        log.info("Generating file...");
        Resource resource = new ClassPathResource("/html/headerTable.txt", FileHtmlProcessor.class.getClassLoader());
        List<String> lines = FileUtils.readLines(resource.getFile(), "utf-8");
        lines.add(generatePlayersTable(playerList));
        resource = new ClassPathResource("/html/headerTable.txt", FileHtmlProcessor.class.getClassLoader());
        lines.addAll(FileUtils.readLines(resource.getFile(), "utf-8"));

        resource = new ClassPathResource("/html/players.html", FileHtmlProcessor.class.getClassLoader());
        FileUtils.write(resource.getFile(), String.join("\n", lines), "UTF-8");
        log.info("file generated: " + resource.getFile().getAbsolutePath());
    }

    public static String generatePlayersTable(List<AnalysedPlayer> playerList) {
        return generateTableHead() + "\n" + generatePlayersTableRows(playerList);
    }

    private static String generateTableHead() {
        return thead(tr(
                th("player"),
                th("chen avg"),
                th("cards"))).render();
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
        return treeMap.keySet().stream()
                .map(FileHtmlProcessor::toString)
                .collect(Collectors.joining(", "));
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
}
