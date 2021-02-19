package com.poker.reader.service;

import com.poker.reader.configuration.PokerReaderProperties;
import com.poker.reader.parser.FileReaderProcessor;
import com.poker.reader.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class FileReaderService {

    private final PlayerRepository playerRepository;
    private final FileReaderProcessor fileReaderProcessor;
    private final PokerReaderProperties pokerReaderProperties;

    public List<String> processFilesFromFolder() throws IOException {
        List<String> listFiles = new ArrayList<>();
        String directory = pokerReaderProperties.getFolder();
        List<File> files = fileReaderProcessor.readDirectory(directory);
        int count = 0;
        for (File file : files) {
            log.info("File {} / {}: {}", ++count, files.size(), file.getAbsolutePath());
            fileReaderProcessor.readFile(file.getAbsolutePath());
            listFiles.add(file.getAbsolutePath());
        }
        log.info(" Persisting {} Players", fileReaderProcessor.getPlayers().size());
        playerRepository.saveAll(fileReaderProcessor.getPlayers());
        playerRepository.flush();
        return listFiles;
    }
}
