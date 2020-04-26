package com.poker.reader.parser;

import com.poker.reader.entity.*;
import com.poker.reader.exception.InvalidSectionFileException;
import com.poker.reader.parser.util.TypeFileSection;
import com.poker.reader.validator.HandValidator;
import lombok.Data;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.poker.reader.entity.TypeStreet.FLOP;
import static com.poker.reader.entity.TypeStreet.RIVER;
import static com.poker.reader.entity.TypeStreet.SHOWDOWN;
import static com.poker.reader.entity.TypeStreet.TURN;
import static com.poker.reader.entity.TypeStreet.*;
import static com.poker.reader.parser.util.FileParser.*;
import static com.poker.reader.parser.util.Tokens.*;
import static com.poker.reader.parser.util.TypeFileSection.*;

@Data
public class FileReaderProcessor {

    private final LinkedList<Hand> handList = new LinkedList<>();
    private final Set<Player> players = new HashSet<>();

    public void readFile(String filePath) throws IOException {
        List<String> lines = FileUtils.readLines(new File(filePath), "utf-8");
        TypeFileSection section = null;
        for (String line : lines) {
            TypeFileSection tempSection = verifySection(line);
            if ((tempSection != null) && (tempSection != section)) {
                section = tempSection;
            }
            assert section != null;
            switch (section) {
                case HEADER:
                    processHeader(line);
                    processActions(line, ANTE);
                    break;
                case PRE_FLOP:
                    processPreFlop(line);
                    processActions(line, PREFLOP);
                    break;
                case FLOP:
                    processFlop(line);
                    processActions(line, FLOP);
                    break;
                case TURN:
                    processTurn(line);
                    processActions(line, TURN);
                    break;
                case RIVER:
                    processRiver(line);
                    processActions(line, RIVER);
                    break;
                case SHOWDOWN:
                    processActions(line, SHOWDOWN);
                    break;
                case SUMMARY:
                    processSummary(line);
                    break;
                case END_OF_HAND:
                    validate();
                    break;
                default:
                    throw new InvalidSectionFileException(section.toString());
            }
        }
    }

    private boolean validate() {
        Hand hand = handList.getLast();
        return HandValidator.validate(hand);
    }

    private void processRiver(String line) {
        Hand hand = handList.getLast();
        if (line.contains(SECTION_RIVER)) {
            River river = extractRiver(line);
            hand.setRiver(river);
        }
    }

    private void processTurn(String line) {
        Hand hand = handList.getLast();
        if (line.contains(SECTION_TURN)) {
            Turn turn = extractTurn(line);
            hand.setTurn(turn);
        }
    }

    private void processFlop(String line) {
        Hand hand = handList.getLast();
        if (line.contains(SECTION_FLOP)) {
            Flop flop = extractFlop(line);
            hand.setFlop(flop);
        }
    }

    private void processPreFlop(String line) {
        Hand hand = handList.getLast();
        if (line.contains(SECTION_PRE_FLOP)) { return;}
        if (line.contains(DEALT_TO)) {
            HoldCards holdCards = extractHoldCards(line);
            hand.getSeats().get(holdCards.getPlayer()).setHoldCards(holdCards);
        }
    }

    protected void processHeader(String line) {
        if (line.contains(START_HAND)) {
            Hand hand = extractHand(line);
            hand.setTournament(extractTournament(line));
            handList.add(hand);
        } else {
            Hand hand = handList.getLast();
            if (line.contains(START_TABLE)) {
                String tableId = extractTable(line);
                Integer button = extractButton(line);
                hand.setTableId(tableId);
                hand.setButton(button);
            } else
            if (line.contains(START_SEAT_POSITION)) {
                Seat seat = extractSeat(line);
                hand.getSeats().put(seat.getPlayer(),seat);
                players.add(seat.getPlayer());
            }
        }
    }

    private void processActions(String line, TypeStreet typeStreet) {
        Hand hand = handList.getLast();
        if (isAction(line)) {
            Action action = extractAction(line);
            action.setTypeStreet(typeStreet);
            hand.getActions().add(action);
            HoldCards holdCards = action.getHoldCards();
            if (holdCards != null) {
                hand.getSeats().get(holdCards.getPlayer()).setHoldCards(holdCards);
            }
        }
    }

    private boolean isAction(String line) {
        for (Player player : players) {
            if (line.contains(player.getNickname()+":")) return true;
        }
        return false;
    }

    private void processSummary(String line) {
        Hand hand = handList.getLast();
        if (line.contains(SECTION_SUMMARY)) return;
        if (line.contains(START_TOTAL_POT)) {
            hand.setTotalPot(extractTotalPot(line));
        } else
        if (line.contains(START_BOARD)) {
            Board board = extractBoard(line);
            hand.setBoard(board);
        } else{
            Integer seatId = extractSeatId(line);
            Seat seat = hand.getSeatBySeatId(seatId);
            hand.getSeats().get(seat.getPlayer()).getInfoPlayerAtHandList().addAll(extractInfoPlayerAtHand(line));
        }
    }

    protected TypeFileSection verifySection(String line) {
        if (line.contains(SECTION_HEADER)) {return HEADER;}
        if (line.contains(SECTION_PRE_FLOP)) {return PRE_FLOP;}
        if (line.contains(SECTION_FLOP)) {return TypeFileSection.FLOP;}
        if (line.contains(SECTION_TURN)) {return TypeFileSection.TURN;}
        if (line.contains(SECTION_RIVER)) {return TypeFileSection.RIVER;}
        if (line.contains(SECTION_SHOWDOWN)) {return TypeFileSection.SHOWDOWN;}
        if (line.contains(SECTION_SUMMARY)) {return SUMMARY;}
        if (line.equals(SECTION_END_OF_HAND)) {return END_OF_HAND;}
        if(line.contains(SECTION_TOKEN)) {throw new InvalidSectionFileException("FOUND A NOT EVALUATED SECTION: " + line);}
        return null;
    }
}
