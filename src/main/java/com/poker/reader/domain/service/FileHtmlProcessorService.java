package com.poker.reader.domain.service;

import static j2html.TagCreator.attrs;
import static j2html.TagCreator.each;
import static j2html.TagCreator.tbody;
import static j2html.TagCreator.td;
import static j2html.TagCreator.th;
import static j2html.TagCreator.thead;
import static j2html.TagCreator.tr;

import com.poker.reader.domain.model.Player;
import com.poker.reader.domain.model.Seat;
import com.poker.reader.domain.repository.PlayerRepository;
import com.poker.reader.domain.repository.SeatRepository;
import com.poker.reader.domain.util.Converter;
import com.poker.reader.dto.AnalysedPlayer;
import com.poker.reader.dto.NormalisedCardsDto;
import com.poker.reader.parser.util.DtoOperationsUtil;
import com.poker.reader.view.rs.dto.PlayerDto;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Component
public class FileHtmlProcessorService {

    private final PlayerRepository playerRepository;
    private final SeatRepository seatRepository;

    public Page<PlayerDto> findPaginated(Pageable pageable) {

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();

        Page<Player> pagePlayers = playerRepository.findAll(pageable);
        List<PlayerDto> playerDtoList = new ArrayList<>();

        for(Player player: pagePlayers.getContent()) {
            List<Seat> seatsFromPlayer = seatRepository.findByPlayer(player);
            playerDtoList.add(Converter.toPlayerDto(player, seatsFromPlayer));
        }

        return new PageImpl<>(playerDtoList, PageRequest.of(currentPage, pageSize), playerRepository.count());
    }

    public static void updatePlayersTableFile(List<AnalysedPlayer> playerList, String directoryPath) throws IOException {
        log.info("Generating file...");
        Resource resource = new ClassPathResource("/html/headerTable.txt", FileHtmlProcessorService.class.getClassLoader());
        List<String> lines = FileUtils.readLines(resource.getFile(), "utf-8");
        lines.add(generatePlayersTable(playerList));
        resource = new ClassPathResource("/html/headerTable.txt", FileHtmlProcessorService.class.getClassLoader());
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
                th("# hand"),
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
                .map(FileHtmlProcessorService::toString)
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
        Resource resource = new ClassPathResource("/html/headerTable.txt", FileHtmlProcessorService.class.getClassLoader());
        List<String> lines = FileUtils.readLines(resource.getFile(), "utf-8");
        lines.add(generatePlayersTable(playerList));
        resource = new ClassPathResource("/html/headerTable.txt", FileHtmlProcessorService.class.getClassLoader());
        lines.addAll(FileUtils.readLines(resource.getFile(), "utf-8"));

        File fileToGenerate = new File(directoryPath + File.separator + "players-" + tournament + ".html");
        FileUtils.write(fileToGenerate, String.join("\n", lines), "UTF-8");
        log.info("file generated: " + fileToGenerate.getAbsolutePath());
    }
}
