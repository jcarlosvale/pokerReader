package com.poker.reader.backup.parser;

import com.poker.reader.backup.entity.*;
import com.poker.reader.backup.exception.InvalidSectionFileException;
import com.poker.reader.backup.parser.util.TypeFileSection;
import com.poker.reader.backup.validator.HandValidator;
import com.poker.reader.domain.service.FileProcessorService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.poker.reader.backup.entity.TypeStreet.FLOP;
import static com.poker.reader.backup.entity.TypeStreet.RIVER;
import static com.poker.reader.backup.entity.TypeStreet.SHOWDOWN;
import static com.poker.reader.backup.entity.TypeStreet.TURN;
import static com.poker.reader.backup.entity.TypeStreet.*;
import static com.poker.reader.backup.parser.util.FileParser.*;
import static com.poker.reader.backup.parser.util.Tokens.*;
import static com.poker.reader.backup.parser.util.TypeFileSection.*;

@Data
@Log4j2
public class FileReaderBkp {

    private final FileProcessorService fileProcessorService = new FileProcessorService(null, null);
    private final LinkedList<Hand> handList = new LinkedList<>();
    private final Set<Player> players = new HashSet<>();
    private File file;

    public static List<File> readDirectory(String directoryPath) {
        return (List<File>) FileUtils.listFiles(new File(directoryPath), new String[]{"txt"}, false);
    }

    public void readFile(String filePath) throws IOException {
        readFile(new File(filePath));
    }

    public void readFile(File file) throws IOException {
        log.debug(" FILE: " + file.getAbsolutePath());
        this.file = file;
        List<String> lines = FileUtils.readLines(file, "utf-8");
        readFile(lines);
    }

    private void readFile(List<String> lines) {
        TypeFileSection section = null;
        for (String line : lines) {
            log.debug(line);
            TypeFileSection tempSection = verifySection(line);
            if (tempSection == CHAT_MESSAGE) {
                continue;
            }
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
            hand.setSidePot(extractSidePot(line));
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
        if (line.contains(SECTION_CHAT_MESSAGE)) return CHAT_MESSAGE;
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
