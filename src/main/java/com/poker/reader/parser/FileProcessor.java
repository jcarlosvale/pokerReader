package com.poker.reader.parser;

import com.google.common.base.Preconditions;
import com.poker.reader.analyser.Analyse;
import com.poker.reader.dto.*;
import lombok.Getter;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Log
@Getter
public class FileProcessor {

    private String tournament;
    private final List<HandDto> hands;
    private final Set<String> players;
    private final Map<String, List<RawCardsDto>> handsOfPlayers;

    public FileProcessor() {
        tournament = null;
        hands = new ArrayList<>();
        players = new TreeSet<>();
        handsOfPlayers = new HashMap<>();
    }

    public Optional<FileProcessedDto> process(final List<String> lines) {
        clearData();
        extractHands(lines);
        processHands();
        return Optional.of(
                FileProcessedDto.builder()
                .analysedPlayers(Analyse.handsOfPlayers(players, handsOfPlayers))
                .totalHands(hands.size())
                .totalPlayers(players.size())
                .build());
    }

    private void clearData() {
        hands.clear();
        players.clear();
        handsOfPlayers.clear();
    }

    public StringBuilder getAnalysis() {
        StringBuilder result = new StringBuilder();
        result
                .append("Total hands: ").append(hands.size())
                .append("\n")
                .append("Total players: ").append(players.size())
                .append("\n")
                .append(Analyse.handsOfPlayers(players, handsOfPlayers));
        return result;
    }

    public List<AnalysedPlayer> getAnalysedPlayers() {
        return Analyse.handsOfPlayers(players, handsOfPlayers);
    }

    private void processHands() {
        hands.forEach(hand -> {
            players.addAll(loadPlayers(hand.getLinesFromSection(FileSection.HEADER)));
            extractHandsOfPlayers(hand.getLinesFromSection(FileSection.SHOWDOWN));
        });
    }

    private void extractHandsOfPlayers(List<String> lines) {
        if (!CollectionUtils.isEmpty(lines)) {
            lines
                .stream()
                .filter(line -> line.contains(": shows ["))
                .forEach(line -> {
/*
                    if(line.equals("[ro]hoi: shows [Jh Qs] (two pair, Queens and Fives)")) {
                        log.info(line);
                    }
*/
                    String player = StringUtils.substringBefore(line,": ");
                    RawCardsDto rawCardsDto = new RawCardsDto(line.substring(line.lastIndexOf("[") + 1, line.lastIndexOf("]")));
                    handsOfPlayers.computeIfAbsent(player, s -> new ArrayList<>()).add(rawCardsDto);
                });
        }
    }

    private void extractHands(List<String> lines) {
        if (!CollectionUtils.isEmpty(lines)) {
            HandDto handDto = null;
            FileSection currentSection = null;
            for(String line: lines) {
                if (line.contains("PokerStars Hand #") && line.contains(": Tournament #")) {

                    currentSection = FileSection.HEADER;
                    if (handDto != null) {
                        hands.add(handDto);
                    }

                    String handId = StringUtils.substringBetween(line, "PokerStars Hand #", ": Tournament ");
                    String tournamentId = StringUtils.substringBetween(line, ": Tournament #", ", ");

                    if(tournament == null) {
                        tournament = tournamentId;
                    } else {
                        Preconditions.checkArgument(tournamentId.equals(tournament), "invalid file tournament!");
                    }

                    handDto = new HandDto(handId, tournamentId);
                }

                FileSection fileSection = extractSection(line);
                if (Objects.nonNull(fileSection)) {
                    currentSection = fileSection;
                }

                if (handDto != null) {
                    handDto.addLine(currentSection, line);
                }
            }

            if (handDto != null) {
                hands.add(handDto);
            }
        }
    }

    private FileSection extractSection(final String line) {
        if (line.contains("*** HOLE CARDS ***"))  return FileSection.PRE_FLOP;
        if (line.contains("*** FLOP ***"))        return FileSection.FLOP;
        if (line.contains("*** TURN ***"))        return FileSection.TURN;
        if (line.contains("*** RIVER ***"))       return FileSection.RIVER;
        if (line.contains("*** SHOW DOWN ***"))   return FileSection.SHOWDOWN;
        if (line.contains("*** SUMMARY ***"))     return FileSection.SUMMARY;
        return null;
    }

    private Set<String> loadPlayers(final List<String> lines) {
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