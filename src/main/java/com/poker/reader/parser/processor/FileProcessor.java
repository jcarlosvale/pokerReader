package com.poker.reader.parser.processor;

import static com.google.common.base.Preconditions.checkArgument;

import com.poker.reader.dto.AnalysedPlayer;
import com.poker.reader.dto.FileProcessedDto;
import com.poker.reader.dto.NormalisedCardsDto;
import com.poker.reader.parser.FileSection;
import com.poker.reader.parser.LinesOfHand;
import com.poker.reader.parser.util.DtoOperationsUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

@Log
public class FileProcessor {

    private String tournament;
    private final List<LinesOfHand> hands;
    private final Set<String> players;
    private final Map<String, AnalysedPlayer> analysedPlayerMap;

    public FileProcessor() {
        tournament = null;
        hands = new ArrayList<>();
        players = new TreeSet<>();
        analysedPlayerMap = new HashMap<>();
    }

    /**
     * Process the files:
     * - clear collection
     * - extract hands, the lines by section
     * - collect players and cards
     * - generate the File Processed DTO
     * @param lines
     * @return
     */
    public FileProcessedDto process(final List<String> lines) {
        clearData();
        extractHands(lines);
        processHands();
        return new FileProcessedDto
                (tournament, hands.size(), players.size(), players, new ArrayList<>(analysedPlayerMap.values()));
    }

    /**
     * Clear collections
     */
    private void clearData() {
        hands.clear();
        players.clear();
        analysedPlayerMap.clear();
        tournament = null;
    }

    /**
     * Extracts players and cards
     */
    private void processHands() {
        hands.forEach(hand -> {
            players.addAll(extractPlayers(hand.getLinesFromSection(FileSection.HEADER)));
            extractCardsFromPlayers(hand.getLinesFromSection(FileSection.SHOWDOWN));
        });
    }

    /**
     * Extract cards
     * @param lines
     */
    private void extractCardsFromPlayers(List<String> lines) {
        if(CollectionUtils.isEmpty(lines)) return;
        lines
            .stream()
            .filter(line -> line.contains(": shows ["))
            .forEach(line -> {
                String player = StringUtils.substringBefore(line,": shows [");
                String rawData = line.substring(line.lastIndexOf("[") + 1, line.lastIndexOf("]"));
                NormalisedCardsDto normalisedCardsDto = DtoOperationsUtil.toNormalisedCardsDto(rawData);
                if (analysedPlayerMap.containsKey(player)) {
                    AnalysedPlayer analysedPlayer = analysedPlayerMap.get(player);
                    analysedPlayer.getRawCards().add(rawData);
                    Map<NormalisedCardsDto, Integer> normalisedCardsMap = analysedPlayer.getNormalisedCardsMap();
                    normalisedCardsMap.put(normalisedCardsDto, normalisedCardsMap.getOrDefault(normalisedCardsDto, 0) + 1);
                } else {
                    Map<NormalisedCardsDto, Integer> normalisedCardsMap = new HashMap<>();
                    normalisedCardsMap.put(normalisedCardsDto, 1);

                    List<String> rawCardsList = new ArrayList<>();
                    rawCardsList.add(rawData);

                    AnalysedPlayer analysedPlayer =
                            new AnalysedPlayer(player, normalisedCardsMap, rawCardsList);

                    analysedPlayerMap.put(player, analysedPlayer);
                }
            });
    }

    /**
     * Extract the hands from lines of file.
     * @param lines
     */
    private void extractHands(@NonNull List<String> lines) {
        LinesOfHand linesOfHand = null;
        FileSection currentSection = null;
        for(String line: lines) {
            if (line.trim().isEmpty()) continue;
            if (line.contains("PokerStars Hand #") && line.contains(": Tournament #")) {
                currentSection = FileSection.HEADER;
                if (linesOfHand != null) {
                    hands.add(linesOfHand);
                }
                String handId = StringUtils.substringBetween(line, "PokerStars Hand #", ": Tournament ");
                String tournamentId = StringUtils.substringBetween(line, ": Tournament #", ", ");
                if(tournament == null) {
                    tournament = tournamentId;
                } else {
                    checkArgument(tournamentId.equals(tournament), "invalid file tournament!");
                }
                linesOfHand = new LinesOfHand(handId, tournamentId);
            }
            FileSection fileSection = extractSection(line);
            if (Objects.nonNull(fileSection)) {
                currentSection = fileSection;
            }
            if (linesOfHand != null) {
                linesOfHand.addLine(currentSection, line);
            }
        }
        if (linesOfHand != null) {
            hands.add(linesOfHand);
        }
    }

    /**
     * Extracts section if present any string
     * @param line
     * @return
     */
    private FileSection extractSection(final String line) {
        if (line.contains("*** HOLE CARDS ***"))  return FileSection.PRE_FLOP;
        if (line.contains("*** FLOP ***"))        return FileSection.FLOP;
        if (line.contains("*** TURN ***"))        return FileSection.TURN;
        if (line.contains("*** RIVER ***"))       return FileSection.RIVER;
        if (line.contains("*** SHOW DOWN ***"))   return FileSection.SHOWDOWN;
        if (line.contains("*** SUMMARY ***"))     return FileSection.SUMMARY;
        return null;
    }

    /**
     * Extract players from lines
     * @param lines
     * @return
     */
    private Set<String> extractPlayers(final List<String> lines) {
        if (CollectionUtils.isEmpty(lines)) {
            return Set.of();
        } else {
            return lines
                    .stream()
                    .filter(line -> line.startsWith("Seat ") && line.contains(" in chips)"))
                    .map(seatLine -> StringUtils.substringBefore(seatLine, "in chips)"))
                    .map(seatLine -> seatLine.substring(seatLine.indexOf(":")+1, seatLine.lastIndexOf("(")).trim()
                    )
                    .collect(Collectors.toSet());
        }
    }
}