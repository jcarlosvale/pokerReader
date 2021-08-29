package processor;

import dto.*;
import exception.InvalidSectionFileException;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import validator.HandValidator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
public class FileReaderProcessor {

    private final LinkedList<HandDTO> handDTOList = new LinkedList<>();
    private final Set<PlayerDTO> playerDTOS = new HashSet<>();
    private File file;

    public List<File> readDirectory(String directoryPath) {
        log.debug("Reading directory [{}]", directoryPath);
        List<File> files = (List<File>) FileUtils.listFiles(new File(directoryPath), new String[]{"txt"}, false);
        log.debug("Total files [{}]", files.size());
        return files;
    }

    public void processFile(String filePath) throws IOException {
        processFile(new File(filePath));
    }

    public void processFile(File file) throws IOException {
        log.debug(" FILE: " + file.getAbsolutePath());
        this.file = file;
        List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
        processFile(lines);
    }

    private void processFile(List<String> lines) {
        TypeFileSectionEnum section = null;
        for (String line : lines) {
            log.debug(line);
            TypeFileSectionEnum tempSection = verifySection(line);
            if (tempSection == TypeFileSectionEnum.CHAT_MESSAGE) {
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
        var handDTO = handDTOList.getLast();
        return HandValidator.validate(handDTO);
    }

    private void processRiver(String line) {
        var handDTO = handDTOList.getLast();
        if (line.contains(SECTION_RIVER)) {
            var river = extractRiver(line);
            handDTO.setRiver(river);
        }
    }

    private void processTurn(String line) {
        var handDTO = handDTOList.getLast();
        if (line.contains(SECTION_TURN)) {
            var turn = extractTurn(line);
            handDTO.setTurn(turn);
        }
    }

    private void processFlop(String line) {
        var handDTO = handDTOList.getLast();
        if (line.contains(SECTION_FLOP)) {
            var flop = extractFlop(line);
            handDTO.setFlop(flop);
        }
    }

    private void processPreFlop(String line) {
        var handDTO = handDTOList.getLast();
        if (line.contains(SECTION_PRE_FLOP)) { return;}
        if (line.contains(DEALT_TO)) {
            var holdCards = extractHoldCards(line);
            handDTO.getSeats().get(holdCards.getPlayerDTO()).setHoldCards(holdCards);
        }
    }

    protected void processHeader(String line) {
        if (line.contains(START_HAND)) {
            var handDTO = extractHand(line);
            handDTO.setTournamentDTO(extractTournament(line));
            handDTOList.add(handDTO);
        } else {
            var handDTO = handDTOList.getLast();
            if (line.contains(START_TABLE)) {
                String tableId = extractTable(line);
                Integer button = extractButton(line);
                handDTO.setTableId(tableId);
                handDTO.setButton(button);
            } else
            if (line.contains(START_SEAT_POSITION)) {
                var seatDTO = extractSeat(line);
                handDTO.getSeats().put(seatDTO.getPlayerDTO(), seatDTO);
                playerDTOS.add(seatDTO.getPlayerDTO());
            }
        }
    }

    private void processActions(String line, TypeStreet typeStreet) {
        var handDTO = handDTOList.getLast();
        if (isAction(line)) {
            var action = extractAction(line);
            action.setTypeStreet(typeStreet);
            handDTO.getActions().add(action);
            var holdCards = action.getHoldCards();
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
        var handDTO = handDTOList.getLast();
        if (line.contains(SECTION_SUMMARY)) return;
        if (line.contains(START_TOTAL_POT)) {
            handDTO.setTotalPot(extractTotalPot(line));
            handDTO.setSidePot(extractSidePot(line));
        } else
        if (line.contains(START_BOARD)) {
            var board = extractBoard(line);
            handDTO.setBoard(board);
        } else{
            Integer seatId = extractSeatId(line);
            var seatDTO = handDTO.getSeatBySeatId(seatId);
            handDTO.getSeats().get(seatDTO.getPlayerDTO()).getInfoPlayerAtHandList().addAll(extractInfoPlayerAtHand(line));
        }
    }

    protected TypeFileSectionEnum verifySection(String line) {
        if (line.contains(SECTION_CHAT_MESSAGE)) return TypeFileSectionEnum.CHAT_MESSAGE;
        if (line.contains(SECTION_HEADER)) {return TypeFileSectionEnum.HEADER;}
        if (line.contains(SECTION_PRE_FLOP)) {return TypeFileSectionEnum.PRE_FLOP;}
        if (line.contains(SECTION_FLOP)) {return TypeFileSectionEnum.FLOP;}
        if (line.contains(SECTION_TURN)) {return TypeFileSectionEnum.TURN;}
        if (line.contains(SECTION_RIVER)) {return TypeFileSectionEnum.RIVER;}
        if (line.contains(SECTION_SHOWDOWN)) {return TypeFileSectionEnum.SHOWDOWN;}
        if (line.contains(SECTION_SUMMARY)) {return TypeFileSectionEnum.SUMMARY;}
        if (line.equals(SECTION_END_OF_HAND)) {return TypeFileSectionEnum.END_OF_HAND;}
        if(line.contains(SECTION_TOKEN)) {throw new InvalidSectionFileException("FOUND A NOT EVALUATED SECTION: " + line);}
        return null;
    }

    public static void main(String[] args) {
        log.info("Executed successfully");
    }
}
