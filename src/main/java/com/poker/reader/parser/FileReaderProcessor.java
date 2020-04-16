package com.poker.reader.parser;

import com.poker.reader.entity.*;
import com.poker.reader.exception.InvalidSectionFileException;
import com.poker.reader.parser.util.TypeFileSection;
import lombok.Data;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static com.poker.reader.entity.TypeStreet.FLOP;
import static com.poker.reader.entity.TypeStreet.RIVER;
import static com.poker.reader.entity.TypeStreet.TURN;
import static com.poker.reader.entity.TypeStreet.*;
import static com.poker.reader.parser.util.FileParser.*;
import static com.poker.reader.parser.util.Tokens.*;
import static com.poker.reader.parser.util.TypeFileSection.*;

@Data
public class FileReaderProcessor {

    private final LinkedList<Hand> handList;
    private Tournament tournament;

    public FileReaderProcessor() {
        handList = new LinkedList<>();
    }

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
                    break;
                case PRE_FLOP:
                    processPreFlop(line);
                    break;
                case FLOP:
                    processFlop(line);
                    break;
                case TURN:
                    processTurn(line);
                    break;
                case RIVER:
                    processRiver(line);
                    break;
                case SUMMARY:
                    processSummary(line);
                    break;
                case END_OF_HAND:
                    break;
                default:
                    throw new InvalidSectionFileException(section.toString());
            }
        }
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
            Summary summary = extractSummary(line);
            hand.getSeatBySeatId(summary.getSeatId()).setSummary(summary);
        }
    }

    private void processRiver(String line) {
        Hand hand = handList.getLast();
        if (line.contains(SECTION_RIVER)) {
            River river = extractRiver(line);
            hand.setRiver(river);
        } else {
            if (line.contains(START_UNCALLED_BET)) {
                AdditionalInfoPlayer additionalInfo = extractAdditionalInfoPlayerUncalledBet(line);
                hand.getAdditionalInfoPlayerList().add(additionalInfo);
            } else
            if (line.contains(START_COLLECTED_FROM_POT)){
                AdditionalInfoPlayer additionalInfo = extractAdditionalInfoPlayerCollectedFromPot(line);
                hand.getAdditionalInfoPlayerList().add(additionalInfo);
            }
            else {
                Action action = extractAction(line);
                action.setTypeStreet(RIVER);
                hand.getActions().add(action);
            }
        }
    }

    private void processTurn(String line) {
        Hand hand = handList.getLast();
        if (line.contains(SECTION_TURN)) {
            Turn turn = extractTurn(line);
            hand.setTurn(turn);
        } else {
            Action action = extractAction(line);
            action.setTypeStreet(TURN);
            hand.getActions().add(action);
        }
    }

    private void processFlop(String line) {
        Hand hand = handList.getLast();
        if (line.contains(SECTION_FLOP)) {
            Flop flop = extractFlop(line);
            hand.setFlop(flop);
        } else {
            Action action = extractAction(line);
            action.setTypeStreet(FLOP);
            hand.getActions().add(action);
        }
    }

    private void processPreFlop(String line) {
        Hand hand = handList.getLast();
        if (line.contains(SECTION_PRE_FLOP)) { return;}
        if (line.contains(DEALT_TO)) {
            HoldCards holdCards = extractHoldCardsFromAction(line);
            hand.getSeats().get(holdCards.getPlayer()).setHoldCards(holdCards);
        } else {
            Action action = extractAction(line);
            action.setTypeStreet(PREFLOP);
            hand.getActions().add(action);
        }
    }

    protected void processHeader(String line) {
        if (line.contains(START_HAND)) {
            handList.add(extractHand(line));
            tournament = extractTournament(line);
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
            } else {
                Action action = extractAction(line);
                action.setTypeStreet(ANTE);
                hand.getActions().add(action);
            }
        }
    }

    protected TypeFileSection verifySection(String line) {
        if (line.contains(SECTION_HEADER)) {return HEADER;}
        if (line.contains(SECTION_PRE_FLOP)) {return PRE_FLOP;}
        if (line.contains(SECTION_FLOP)) {return TypeFileSection.FLOP;}
        if (line.contains(SECTION_TURN)) {return TypeFileSection.TURN;}
        if (line.contains(SECTION_RIVER)) {return TypeFileSection.RIVER;}
        if (line.contains(SECTION_SUMMARY)) {return SUMMARY;}
        if (line.equals(SECTION_END_OF_HAND)) {return END_OF_HAND;}
        return null;
    }
}
