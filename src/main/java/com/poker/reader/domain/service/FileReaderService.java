package com.poker.reader.domain.service;

import com.poker.reader.configuration.PokerReaderProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.poker.reader.domain.util.Util.readFilesFromDirectory;
import static com.poker.reader.domain.util.Util.readLinesFromFile;

@Service
@Log4j2
@RequiredArgsConstructor
public class FileReaderService {

    private final PokerReaderProperties pokerReaderProperties;
    private final FileImportService fileImportService;

    public String importPokerHistoryFiles() throws IOException {
        long start = System.currentTimeMillis();

        log.info("Importing files from folder {} ...", pokerReaderProperties.getFolderPokerFiles());

        List<File> filesToBeProcessed = readFilesFromDirectory(pokerReaderProperties.getFolderPokerFiles(), "txt");
        int importedFiles = importFilesFromDirectory(filesToBeProcessed);
        String message = String.format("Imported %d / %d files in %d ms from folder %s",
                importedFiles, filesToBeProcessed.size(), (System.currentTimeMillis() - start),
                pokerReaderProperties.getFolderPokerFiles());

        log.info(message);
        return message;
    }

    private int importFilesFromDirectory(List<File> filesToBeProcessed) throws IOException {
        int importedFiles = 0;
        int count = 0;
        for(File file: filesToBeProcessed) {
            String fileName = file.getName();
            log.info("Processing " + fileName);
            long start = System.currentTimeMillis();
            if (fileImportService.importFile(fileName, readLinesFromFile(file))) importedFiles++;
            long end = System.currentTimeMillis();
            count++;
            log.info("Processed " + count + "/" + filesToBeProcessed.size() + " " + (end - start) + "ms");
        }
        return importedFiles;
    }

}
