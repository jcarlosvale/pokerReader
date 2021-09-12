package com.poker.reader.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.poker.reader.dto.AnalysedPlayer;
import com.poker.reader.dto.FileProcessedDto;
import com.poker.reader.entity.*;
import com.poker.reader.exception.InvalidSectionFileException;
import com.poker.reader.parser.util.TypeFileSection;
import com.poker.reader.validator.HandValidator;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.poker.reader.entity.TypeStreet.FLOP;
import static com.poker.reader.entity.TypeStreet.RIVER;
import static com.poker.reader.entity.TypeStreet.SHOWDOWN;
import static com.poker.reader.entity.TypeStreet.TURN;
import static com.poker.reader.entity.TypeStreet.*;
import static com.poker.reader.parser.util.FileParser.*;
import static com.poker.reader.parser.util.Tokens.*;
import static com.poker.reader.parser.util.TypeFileSection.*;

@Data
@Log4j2
public class FileReader {

    private final FileProcessor fileProcessor = new FileProcessor();
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

    public static void main(String[] args) throws IOException {
        String inputDirectory = args[0];
        String outputDirectory = "c:\\temp";
        FileProcessor fileProcessor = new FileProcessor();
        List<File> files = readDirectory(inputDirectory);
        Set<String> filesAlreadyProcessed = readFilesProcessed(outputDirectory);
        Set<String> currentFilesProcessed = new HashSet<>();
        int count = 1;
        for(File file: files) {
            String fileName = file.getName();
            System.out.println("Processing " + fileName);
            long start = System.currentTimeMillis();
            if(!filesAlreadyProcessed.contains(fileName)) {
                List<String> lines = FileUtils.readLines(file, "utf-8");
                Optional<FileProcessedDto> optionalFileProcessedDto = fileProcessor.process(lines);
                if (optionalFileProcessedDto.isPresent()) {
                    filesAlreadyProcessed.add(fileName);
                    currentFilesProcessed.add(fileName);
                    saveProcess(outputDirectory, fileName, fileProcessor.getAnalysis());
                    saveJson(outputDirectory, fileName, optionalFileProcessedDto.get());
                }
                System.out.println("Processed " + count + "/" + files.size() + " " + (System.currentTimeMillis() - start) + "ms");
                count++;
            } else {
                System.out.println("Already processed " + count + "/" + files.size() + " " + (System.currentTimeMillis() - start) + "ms");
            }
        }
        saveSummary(outputDirectory, currentFilesProcessed);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Summarizar? (y/n)");
        String option = scanner.nextLine();
        if(option.charAt(0) == 'y') {
            System.out.println("Summarizing...");
            summarize(outputDirectory);
        }
        System.out.println("END");
    }

    private static void summarize(String outputDirectory) throws IOException {
        String directory = outputDirectory + "\\output";
        List<File> files = (List<File>) FileUtils.listFiles(new File(directory), new String[]{"json"}, false);
        int count = 1;
        FileProcessedDto fileProcessedDtoTotal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        for(File file: files) {
            String fileName = file.getName();
            System.out.println("Processing " + fileName);
            long start = System.currentTimeMillis();
            if(fileProcessedDtoTotal == null) {
                fileProcessedDtoTotal = objectMapper.readValue(file,FileProcessedDto.class);
            } else {
                fileProcessedDtoTotal = merge(fileProcessedDtoTotal, objectMapper.readValue(file,
                        FileProcessedDto.class));
            }
            System.out.println("Updated summary " + count + "/" + files.size() + " " + (System.currentTimeMillis() - start) +
                    "ms");
        }
        saveJsonSummary(outputDirectory, "player-summary.json", fileProcessedDtoTotal);
    }

    private static void saveJsonSummary(String outputDirectory, String filename,
                                        FileProcessedDto fileProcessedDto) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        FileUtils.write(new File(outputDirectory + File.separator + filename),
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(fileProcessedDto) , "UTF-8");

    }

    private static FileProcessedDto merge(FileProcessedDto fileProcessedDto1, FileProcessedDto fileProcessedDto2) {
        Preconditions.checkArgument(!fileProcessedDto1.getTournament().equals(fileProcessedDto2.getTournament()),
                "invalid files merging, same tournament!!");

        int totalHands = fileProcessedDto1.getTotalHands() + fileProcessedDto2.getTotalHands();
        Map<String, AnalysedPlayer> mapAnalysedPlayerByPlayer = new HashMap<>();

        //loading to map
        fileProcessedDto1
                .getAnalysedPlayers()
                .forEach(analysedPlayer -> mapAnalysedPlayerByPlayer.put(analysedPlayer.getPlayer(), analysedPlayer));

        //reading dto 2
        fileProcessedDto2
                .getAnalysedPlayers()
                .forEach(analysedPlayer -> merge(analysedPlayer, mapAnalysedPlayerByPlayer));
    }

    private static void merge(AnalysedPlayer analysedPlayer, Map<String, AnalysedPlayer> mapAnalysedPlayerByPlayer) {
        if (mapAnalysedPlayerByPlayer.containsKey(analysedPlayer.getPlayer())) {
            analysedPlayer.
            AnalysedPlayer analysedPlayerFromMap = mapAnalysedPlayerByPlayer.get(analysedPlayer.getPlayer());
            analysedPlayerFromMap.getHands().stream().findFirst()
        } else {
            mapAnalysedPlayerByPlayer.put(analysedPlayer.getPlayer(), analysedPlayer);
        }
    }

    private static void saveSummary(String outputDirectory, Set<String> filesProcessed) throws IOException {
        final String FILENAME_SUMMARY = "summary.txt";
        String filename = outputDirectory + File.separator + FILENAME_SUMMARY;
        File summaryFile = new File(filename);
        FileUtils.deleteQuietly(summaryFile);
        FileUtils.writeLines(summaryFile, filesProcessed);
    }

    private static Set<String> readFilesProcessed(String outputDirectory) throws IOException {
        final String FILENAME_SUMMARY = "summary.txt";
        String filename = outputDirectory + File.separator + FILENAME_SUMMARY;
        File summaryFile = new File(filename);
        if (summaryFile.exists()) {
            return new HashSet<>(FileUtils.readLines(new File(filename), "utf-8"));
        }
        return new HashSet<>();
    }

    private static void saveJson(String outputDirectory, String fileNameProcessed, FileProcessedDto fileProcessedDto)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String directory = outputDirectory + "\\output";
        String fileWithoutExtension = FilenameUtils.removeExtension(fileNameProcessed);
        String fileName = fileWithoutExtension + ".json";
        FileUtils.write(new File(directory + File.separator + fileName),
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(fileProcessedDto) , "UTF-8");
    }

    private static void saveProcess(String outputDirectory, String fileNameProcessed, StringBuilder analysis)
            throws IOException {
        String directory = outputDirectory + "\\output";
        String fileName = "result-" + fileNameProcessed;
        FileUtils.write(new File(directory + File.separator + fileName), analysis.toString(), "UTF-8");
    }
}
