package com.poker.reader.parser;

import com.poker.reader.entity.*;
import com.poker.reader.parser.util.TypeFileSection;
import lombok.Data;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static com.poker.reader.entity.TypeStreet.ANTE;
import static com.poker.reader.entity.TypeStreet.PREFLOP;
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
            switch (section) {
                case HEADER:
                    processHeader(line);
                    break;
                case PRE_FLOP:
                    processPreFlop(line);
                    break;
                default:
            }
        }
    }

    private void processPreFlop(String line) {
        Hand hand = handList.getLast();
        if (line.contains(SECTION_PRE_FLOP)) return;
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
        if (line.contains(SECTION_HEADER)) return HEADER;
        if (line.contains(SECTION_PRE_FLOP)) return PRE_FLOP;
        if (line.contains(SECTION_FLOP)) return FLOP;
        if (line.contains(SECTION_TURN)) return TURN;
        if (line.contains(SECTION_RIVER)) return RIVER;
        if (line.contains(SECTION_SUMMARY)) return SUMMARY;
        return null;
    }
}
