package com.poker.reader.domain.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.poker.reader.domain.util.CardUtil.valueOf;

import com.poker.reader.domain.model.Cards;
import com.poker.reader.domain.model.FileSection;
import com.poker.reader.domain.model.Player;
import com.poker.reader.domain.model.Tournament;
import com.poker.reader.domain.repository.CardsRepository;
import com.poker.reader.domain.repository.PlayerRepository;
import com.poker.reader.domain.repository.TournamentRepository;
import com.poker.reader.domain.util.CardUtil;
import com.poker.reader.domain.util.Chen;
import com.poker.reader.domain.util.Util;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@Log4j2
@RequiredArgsConstructor
public class FileProcessorService {

    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;
    private final CardsRepository cardsRepository;

    @Transactional
    public boolean process(String fileName, final List<String> lines) {
        if (!isProcessedFile(fileName)){
            List<LinesOfHand> handsFromFile = extractHands(lines);
            if (!handsFromFile.isEmpty()) {
                Optional<Tournament> tournamentOptional = saveTournament(handsFromFile.get(0), fileName);
                if (tournamentOptional.isPresent()) {
                    return processHands(handsFromFile, tournamentOptional.get());
                }
            }
        } else {
            log.info("Already processed {}", fileName);
        }
        return false;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<Tournament> saveTournament(LinesOfHand linesOfHand, String fileName) {

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
            updatePlayers(players, tournament);
            extractCardsFromPlayers(hand.getLinesFromSection(FileSection.SHOWDOWN));
        });
        return true;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void updatePlayers(Set<String> players, Tournament tournament) {
        for(String player: players) {
            if (playerRepository.existsById(player)) { //exists
                var playerFromDatabase = playerRepository.getById(player);
                playerFromDatabase.setTotalHands(playerFromDatabase.getTotalHands()+1);
                playerRepository.save(playerFromDatabase);
            } else { //not exists
                playerRepository.save(
                        Player
                                .builder()
                                .nickname(player)
                                .showdowns(0)
                                .totalHands(1)
                                .avgChen(-100L)
                                .sumChen(0L)
                                .playedAt(tournament.getPlayedAt())
                                .createdAt(LocalDateTime.now())
                                .build());
            }
        }
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
                saveCardsOfPlayer(player, rawData);
                });
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveCardsOfPlayer(@NonNull String player, @NonNull String rawCard) {
        checkArgument(playerRepository.existsById(player), "Inconsistent reading from file: missing player " + player);
        checkArgument(rawCard.length() > 4, "invalid format of rawData " + rawCard);

        var cards = updateCardsFromPlayer(player, rawCard);
        updatePlayerCardsStats(player, cards.getChenValue());
    }

    private void updatePlayerCardsStats(String player, long chenValue) {
        var playerFromDb = playerRepository.getById(player);
        playerFromDb.setShowdowns(playerFromDb.getShowdowns()+1);
        playerFromDb.setSumChen(playerFromDb.getSumChen()+chenValue);
        playerFromDb.setAvgChen(Math.round( ((double) playerFromDb.getSumChen()) / playerFromDb.getShowdowns()));
        playerRepository.save(playerFromDb);
    }

    private Cards updateCardsFromPlayer(String player, String rawCard) {
        String card1;
        String card2;
        if(valueOf(rawCard.charAt(0)) >= valueOf(rawCard.charAt(3))) {
            card1 = String.valueOf(rawCard.charAt(0));
            card2 = String.valueOf(rawCard.charAt(3));
        } else {
            card1 = String.valueOf(rawCard.charAt(3));
            card2 = String.valueOf(rawCard.charAt(0));
        }
        boolean isPair = card1.equals(card2);
        boolean isSuited = rawCard.charAt(1) == rawCard.charAt(4);
        String suited = "";

        if (!isPair) {
            if (isSuited) suited = "s";
            else suited = "o";
        }

        String description = card1 + card2 + suited;

        Optional<Cards> cardsFromPlayerOptional = cardsRepository.findByPlayerAndDescription(player, description);

        if (cardsFromPlayerOptional.isPresent()) {
            Cards cardsFromPlayer = cardsFromPlayerOptional.get();
            List<String> listOfRawCards = CardUtil.convertStringToList(cardsFromPlayer.getRawCards());
            listOfRawCards.add(rawCard);
            cardsFromPlayer.setRawCards(CardUtil.convertListToString(listOfRawCards));
            cardsFromPlayer.setCounter(cardsFromPlayer.getCounter() + 1);
            return cardsRepository.save(cardsFromPlayer);
        } else {
            int chenValue = Chen.calculateChenFormulaFrom(description);
            Cards cards = new Cards(player, description, rawCard, card1, card2, isSuited, isPair, 1L, chenValue, LocalDateTime.now());
            return cardsRepository.save(cards);
        }
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
    public Set<String> extractPlayers(final List<String> lines) {
        if (CollectionUtils.isEmpty(lines)) {
            return Set.of();
        } else {
            return lines
                    .stream()
                    .filter(line -> line.startsWith("Seat ") && line.contains(" in chips"))
                    .map(seatLine -> StringUtils.substringBefore(seatLine, "in chips"))
                    .map(seatLine -> seatLine.substring(seatLine.indexOf(":")+1, seatLine.lastIndexOf("(")).trim()
                    )
                    .collect(Collectors.toSet());
        }
    }
}