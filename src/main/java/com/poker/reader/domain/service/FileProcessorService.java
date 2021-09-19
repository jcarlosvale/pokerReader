package com.poker.reader.domain.service;

import com.poker.reader.domain.dto.FileSection;
import com.poker.reader.domain.dto.LinesOfHand;
import com.poker.reader.domain.model.Player;
import com.poker.reader.domain.model.Tournament;
import com.poker.reader.domain.repository.PlayerRepository;
import com.poker.reader.domain.repository.TournamentRepository;
import com.poker.reader.domain.util.Util;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.function.Predicate.not;

@Service
@Log4j2
@RequiredArgsConstructor
public class FileProcessorService {

    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;

    public boolean process(String fileName, final List<String> lines) {
        if (!isProcessedFile(fileName)){
            List<LinesOfHand> handsFromFile = extractHands(lines);
            if (!handsFromFile.isEmpty()) {
                Optional<Tournament> tournamentOptional = saveTournament(handsFromFile.get(0), fileName);
                if (tournamentOptional.isPresent()) {
                    return processHands(handsFromFile, tournamentOptional.get());
                }
            }
        }
        return false;
    }

    private Optional<Tournament> saveTournament(LinesOfHand linesOfHand, String fileName) {

        String tournamentId = linesOfHand.getTournamentId();
        LocalDate playedAt = linesOfHand.getPlayedAt();

        if (!tournamentRepository.existsById(tournamentId)) {
            return Optional.of(tournamentRepository.save(new Tournament(tournamentId, fileName,
                    playedAt, LocalDateTime.now())));
        }
        return Optional.empty();
    }

    private boolean isProcessedFile(String fileName) {
        return tournamentRepository.existsTournamentByFileNameEquals(fileName);
    }

    private boolean processHands(List<LinesOfHand> handsFromFile, Tournament tournament) {
        handsFromFile.forEach(hand -> {
            Set<String> players = extractPlayers(hand.getLinesFromSection(FileSection.HEADER));
            savePlayers(players, tournament);
            extractCardsFromPlayers(hand.getLinesFromSection(FileSection.SHOWDOWN));
        });
        return true;
    }

    private void savePlayers(Set<String> players, Tournament tournament) {
        players
                .stream()
                .filter(not(playerRepository::existsById))
                .map(nickname -> new Player(nickname, tournament.getPlayedAt(), LocalDateTime.now()))
                .forEach(playerRepository::save);
    }

    /**
     * Extract cards
     * @param lines
     */
    private void extractCardsFromPlayers(List<String> lines) {
        /*
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

         */
    }

    /**
     * Extract the hands from lines of file.
     * @param lines
     * @return
     */
    private List<LinesOfHand> extractHands(@NonNull List<String> lines) {
        List<LinesOfHand> hands = new ArrayList<>();
        LinesOfHand linesOfHand = null;
        FileSection currentSection = null;
        String tournament = null;
        for(String line: lines) {
            if (line.trim().isEmpty()) continue;
            if (line.contains("PokerStars Hand #") && line.contains(": Tournament #")) {
                currentSection = FileSection.HEADER;
                if (linesOfHand != null) {
                    hands.add(linesOfHand);
                }

                String handId = StringUtils.substringBetween(line, "PokerStars Hand #", ": Tournament ");
                String tournamentId = StringUtils.substringBetween(line, ": Tournament #", ", ");
                String strDateTime = StringUtils.substringBetween(line, "[", "]");
                strDateTime = strDateTime.substring(0, Util.DATE_TIME_FORMAT.length()).trim();
                LocalDate playedAt = Util.toLocalDate(strDateTime);


                if(tournament == null) {
                    tournament = tournamentId;
                } else {
                    checkArgument(tournamentId.equals(tournament), "invalid file tournament!");
                }
                linesOfHand = new LinesOfHand(handId, tournamentId, playedAt);
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
        return hands;
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