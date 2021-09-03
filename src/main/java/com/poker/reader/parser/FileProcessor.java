package com.poker.reader.parser;

import com.poker.reader.dto.FileSection;
import com.poker.reader.dto.HandDto;
import com.poker.reader.dto.RawCardsDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

@Getter
public class FileProcessor {

    private final List<HandDto> hands;
    private final Set<String> players;
    private final Map<String, List<RawCardsDto>> handsOfPlayers;

    public FileProcessor() {
        hands = new ArrayList<>();
        players = new HashSet<>();
        handsOfPlayers = new HashMap<>();
    }

    public void process(final List<String> lines) {
        extractHands(lines);
        processHands();
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
                    String player = StringUtils.substringBefore(line,": ");
                    RawCardsDto rawCardsDto = new RawCardsDto(StringUtils.substringBetween(line, "[", "]"));
                    handsOfPlayers.computeIfAbsent(player, s -> new ArrayList<>()).add(rawCardsDto);
                });
        }
    }

    private void extractHands(List<String> lines) {
        if (!CollectionUtils.isEmpty(lines)) {
            HandDto handDto = null;
            FileSection currentSection = null;
            for(String line: lines) {
                if (line.startsWith("PokerStars Hand #")) {

                    currentSection = FileSection.HEADER;
                    if (handDto != null) {
                        hands.add(handDto);
                    }

                    String handId = StringUtils.substringBetween(line, "PokerStars Hand #", ": Tournament ");
                    String tournamentId = StringUtils.substringBetween(line, ": Tournament #", ", ");
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
                    .map(seatLine -> seatLine.substring(seatLine.indexOf(":")+1, seatLine.lastIndexOf("(")).trim())
                    .collect(Collectors.toSet());
        }
    }
}