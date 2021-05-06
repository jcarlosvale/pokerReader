package processor;

import dto.*;
import exception.InvalidSectionFileException;
import lombok.Data;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import validator.HandValidator;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static dto.TypeStreet.ANTE;
import static dto.TypeStreet.PREFLOP;
import static parser.FileParser.*;
import static parser.Tokens.*;

@Data
@Log4j2
@Service
public class FileReaderProcessor {

    @Getter
    private final LinkedList<HandDTO> handDTOList = new LinkedList<>();
    @Getter
    private final Set<PlayerDTO> playerDTOS = new HashSet<>();
    private File file;

    public List<File> readDirectory(String directoryPath) {
        List<File> files = (List<File>) FileUtils.listFiles(new File(directoryPath), new String[]{"txt"}, false);
        return files;
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
            if (line.contains("alexandru2111 said, \"putaaaaaa  raise for nothing? **** you and your mother!!!")) {
                System.out.println(" debug ");
            }
            TypeFileSection tempSection = verifySection(line);
            if (tempSection == TypeFileSection.CHAT_MESSAGE) {
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
                    processActions(line, TypeStreet.FLOP);
                    break;
                case TURN:
                    processTurn(line);
                    processActions(line, TypeStreet.TURN);
                    break;
                case RIVER:
                    processRiver(line);
                    processActions(line, TypeStreet.RIVER);
                    break;
                case SHOWDOWN:
                    processActions(line, TypeStreet.SHOWDOWN);
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
        HandDTO handDTO = handDTOList.getLast();
        return HandValidator.validate(handDTO);
    }

    private void processRiver(String line) {
        HandDTO handDTO = handDTOList.getLast();
        if (line.contains(SECTION_RIVER)) {
            River river = extractRiver(line);
            handDTO.setRiver(river);
        }
    }

    private void processTurn(String line) {
        HandDTO handDTO = handDTOList.getLast();
        if (line.contains(SECTION_TURN)) {
            Turn turn = extractTurn(line);
            handDTO.setTurn(turn);
        }
    }

    private void processFlop(String line) {
        HandDTO handDTO = handDTOList.getLast();
        if (line.contains(SECTION_FLOP)) {
            Flop flop = extractFlop(line);
            handDTO.setFlop(flop);
        }
    }

    private void processPreFlop(String line) {
        HandDTO handDTO = handDTOList.getLast();
        if (line.contains(SECTION_PRE_FLOP)) { return;}
        if (line.contains(DEALT_TO)) {
            HoldCards holdCards = extractHoldCards(line);
            handDTO.getSeats().get(holdCards.getPlayerDTO()).setHoldCards(holdCards);
        }
    }

    protected void processHeader(String line) {
        if (line.contains(START_HAND)) {
            HandDTO handDTO = extractHand(line);
            handDTO.setTournamentDTO(extractTournament(line));
            handDTOList.add(handDTO);
        } else {
            HandDTO handDTO = handDTOList.getLast();
            if (line.contains(START_TABLE)) {
                String tableId = extractTable(line);
                Integer button = extractButton(line);
                handDTO.setTableId(tableId);
                handDTO.setButton(button);
            } else
            if (line.contains(START_SEAT_POSITION)) {
                SeatDTO seatDTO = extractSeat(line);
                handDTO.getSeats().put(seatDTO.getPlayerDTO(), seatDTO);
                playerDTOS.add(seatDTO.getPlayerDTO());
            }
        }
    }

    private void processActions(String line, TypeStreet typeStreet) {
        HandDTO handDTO = handDTOList.getLast();
        if (isAction(line)) {
            Action action = extractAction(line);
            action.setTypeStreet(typeStreet);
            handDTO.getActions().add(action);
            HoldCards holdCards = action.getHoldCards();
            if (holdCards != null) {
                handDTO.getSeats().get(holdCards.getPlayerDTO()).setHoldCards(holdCards);
            }
        }
    }

    private boolean isAction(String line) {
        for (PlayerDTO playerDTO : playerDTOS) {
            if (line.contains(playerDTO.getNickname()+":")) return true;
        }
        return false;
    }

    private void processSummary(String line) {
        HandDTO handDTO = handDTOList.getLast();
        if (line.contains(SECTION_SUMMARY)) return;
        if (line.contains(START_TOTAL_POT)) {
            handDTO.setTotalPot(extractTotalPot(line));
            handDTO.setSidePot(extractSidePot(line));
        } else
        if (line.contains(START_BOARD)) {
            Board board = extractBoard(line);
            handDTO.setBoard(board);
        } else{
            Integer seatId = extractSeatId(line);
            SeatDTO seatDTO = handDTO.getSeatBySeatId(seatId);
            handDTO.getSeats().get(seatDTO.getPlayerDTO()).getInfoPlayerAtHandList().addAll(extractInfoPlayerAtHand(line));
        }
    }

    protected TypeFileSection verifySection(String line) {
        if (line.contains(SECTION_CHAT_MESSAGE)) return TypeFileSection.CHAT_MESSAGE;
        if (line.contains(SECTION_HEADER)) {return TypeFileSection.HEADER;}
        if (line.contains(SECTION_PRE_FLOP)) {return TypeFileSection.PRE_FLOP;}
        if (line.contains(SECTION_FLOP)) {return TypeFileSection.FLOP;}
        if (line.contains(SECTION_TURN)) {return TypeFileSection.TURN;}
        if (line.contains(SECTION_RIVER)) {return TypeFileSection.RIVER;}
        if (line.contains(SECTION_SHOWDOWN)) {return TypeFileSection.SHOWDOWN;}
        if (line.contains(SECTION_SUMMARY)) {return TypeFileSection.SUMMARY;}
        if (line.equals(SECTION_END_OF_HAND)) {return TypeFileSection.END_OF_HAND;}
        if(line.contains(SECTION_TOKEN)) {throw new InvalidSectionFileException("FOUND A NOT EVALUATED SECTION: " + line);}
        return null;
    }
}
