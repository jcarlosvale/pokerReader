package com.poker.reader.domain.service;

import static com.poker.reader.domain.util.Util.readFilesFromDirectory;
import static com.poker.reader.domain.util.Util.readLinesFromFile;

import com.poker.reader.configuration.PokerReaderProperties;
import java.io.File;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class FileReaderService {

    private final PokerReaderProperties pokerReaderProperties;
    private final FileProcessorService fileProcessorService;
    private final FileImportService fileImportService;

    public String importPokerHistoryFiles() throws IOException {
        log.info("Importing files from folder {} ...", pokerReaderProperties.getFolderPokerFiles());
        int analyzedFiles = importFilesFromDirectory();
        String message = String.format("Analyzed %d files from folder %s", analyzedFiles, pokerReaderProperties.getFolderPokerFiles());
        log.info(message);
        return message;
    }

    private int importFilesFromDirectory() throws IOException {
        List<File> filesToBeProcessed = readFilesFromDirectory(pokerReaderProperties.getFolderPokerFiles(), "txt");
        int analyzedFiles = 0;
        int count = 0;
        for(File file: filesToBeProcessed) {
            String fileName = file.getName();
            log.info("Processing " + fileName);
            long start = System.currentTimeMillis();
            fileImportService.importFile(fileName, readLinesFromFile(file));
            analyzedFiles++;
            long end = System.currentTimeMillis();
            count++;
            log.info("Processed " + count + "/" + filesToBeProcessed.size() + " " + (end - start) + "ms");
        }
        return analyzedFiles;
    }

}
