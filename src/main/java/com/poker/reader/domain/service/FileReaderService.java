package com.poker.reader.domain.service;

import static com.poker.reader.domain.util.Util.readFilesFromDirectory;
import static com.poker.reader.domain.util.Util.readLinesFromFile;

import com.poker.reader.configuration.PokerReaderProperties;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class FileReaderService {

    private final PokerReaderProperties pokerReaderProperties;
    private final FileProcessorService fileProcessorService;

    @PostConstruct
    private void processPokerHistoryFiles() throws IOException {
        log.info("Processing files from folder {} ...", pokerReaderProperties.getFolderPokerFiles());
        int processedFiles = processFilesFromDirectory();
        log.info("Processed new {} files from folder.", processedFiles);
    }

    private int processFilesFromDirectory() throws IOException {
        List<File> filesToBeProcessed = readFilesFromDirectory(pokerReaderProperties.getFolderPokerFiles(), "txt");
        int processedFilesCount = 0;
        int count = 0;
        for(File file: filesToBeProcessed) {
            String fileName = file.getName();
            log.info("Processing " + fileName);
            long start = System.currentTimeMillis();
            fileProcessorService.processFile(fileName, readLinesFromFile(file));
            processedFilesCount++;
            long end = System.currentTimeMillis();
            count++;
            log.info("Processed " + count + "/" + filesToBeProcessed.size() + " " + (end - start) + "ms");
        }
        return processedFilesCount;
    }

}
