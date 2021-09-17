package com.poker.reader.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poker.reader.dto.AnalysedPlayer;
import com.poker.reader.dto.FileProcessedDto;
import com.poker.reader.parser.processor.FileHtmlProcessor;
import com.poker.reader.parser.processor.FileMergeProcessor;
import com.poker.reader.parser.processor.FileProcessor;
import java.io.File;
import java.io.IOException;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

@Log4j2
public class FileReader {

    /**
     * Retrieves the list of file from a given directory
     * @param directory
     * @param extensionFiles
     * @return list of files
     */
    public static List<File> readFilesFromDirectory(String directory, String ... extensionFiles) {
        return (List<File>) FileUtils.listFiles(new File(directory), extensionFiles, false);
    }

    /**
     * Returns the lines from a give filepath
     * @param filePath
     * @return list of line
     * @throws IOException
     */
    public static List<String> readLinesFromFile(String filePath) throws IOException {
        return readLinesFromFile(new File(filePath));
    }

    public static List<String> readLinesFromFile(File file) throws IOException {
        log.debug(" FILE: " + file.getAbsolutePath());
        return FileUtils.readLines(file, "utf-8");
    }

    public static void processFilesFromDirectory(String inputDirectory, String outputDirectory) throws IOException {
        FileProcessor fileProcessor = new FileProcessor();
        List<File> filesToBeProcessed = readFilesFromDirectory(inputDirectory, "txt");
        int count = 1;
        for(File file: filesToBeProcessed) {
            String fileName = file.getName();
            log.info("Processing " + fileName);
            long start = System.currentTimeMillis();
            FileProcessedDto fileProcessedDto = fileProcessor.process(readLinesFromFile(file));
            saveJson(outputDirectory, fileName, fileProcessedDto);
            long end = System.currentTimeMillis();
            log.info("Processed " + count + "/" + filesToBeProcessed.size() + " " + (end - start) + "ms");
            count++;
        }
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

    private static void mergeFilesFromDirectory(String inputDirectory, String outputDirectory) throws IOException {
        FileMergeProcessor fileMergeProcessor = new FileMergeProcessor(outputDirectory);
        List<File> filesToMerge = readFilesFromDirectory(inputDirectory, "json");
        fileMergeProcessor.mergeFiles(filesToMerge);
    }

    private static void generatePlayersTableFile(String outputDirectory) throws IOException {
        FileMergeProcessor fileMergeProcessor = new FileMergeProcessor(outputDirectory);
        List<AnalysedPlayer> analysedPlayersList = fileMergeProcessor.loadConsolidatedFile()
                .getAnalysedPlayers();
        FileHtmlProcessor.updatePlayersTableFile(analysedPlayersList);
    }

    public static void main(String[] args) throws IOException {
        String inputDirectory = args[0];
        String outputDirectory = "c:\\temp";
        processFilesFromDirectory(inputDirectory, outputDirectory);
        mergeFilesFromDirectory(outputDirectory + "\\output", outputDirectory);
        generatePlayersTableFile(outputDirectory);
        log.info("END");
    }

}
