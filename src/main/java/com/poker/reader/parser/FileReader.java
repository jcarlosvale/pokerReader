package com.poker.reader.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poker.reader.dto.AnalysedPlayer;
import com.poker.reader.dto.FileProcessedDto;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

@Data
@Log4j2
public class FileReader {

    /**
     * Retrieves the list of file from a given directory
     * @param directoryPath
     * @param extensionFiles
     * @return list of files
     */
    public static List<File> readFilesFromDirectory(String directoryPath, String ... extensionFiles) {
        return (List<File>) FileUtils.listFiles(new File(directoryPath), extensionFiles, false);
    }

    /**
     * Returns the lines from a give filepath
     * @param filePath
     * @return list of line
     * @throws IOException
     */
    public static List<String> readLinesFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        log.debug(" FILE: " + file.getAbsolutePath());
        return FileUtils.readLines(file, "utf-8");
    }

    public static void main(String[] args) throws IOException {
        String inputDirectory = args[0];
        String outputDirectory = "c:\\temp";
        FileProcessor fileProcessor = new FileProcessor();
        List<File> files = readFilesFromDirectory(inputDirectory);
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
                    //saveProcess(outputDirectory, fileName, fileProcessor.getAnalysis());
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
/*        Preconditions.checkArgument(!fileProcessedDto1.getTournament().equals(fileProcessedDto2.getTournament()),
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
                .forEach(analysedPlayer -> merge(analysedPlayer, mapAnalysedPlayerByPlayer));*/
        return null;
    }

    private static void merge(AnalysedPlayer analysedPlayer, Map<String, AnalysedPlayer> mapAnalysedPlayerByPlayer) {
/*
        if (mapAnalysedPlayerByPlayer.containsKey(analysedPlayer.getPlayer())) {
            analysedPlayer.
            AnalysedPlayer analysedPlayerFromMap = mapAnalysedPlayerByPlayer.get(analysedPlayer.getPlayer());
            analysedPlayerFromMap.getHands().stream().findFirst()
        } else {
            mapAnalysedPlayerByPlayer.put(analysedPlayer.getPlayer(), analysedPlayer);
        }
 */
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
