package com.poker.reader.processor;

import static com.poker.reader.parser.FileReader.readLinesFromFile;
import static com.poker.reader.parser.util.DtoOperationsUtil.convertToMapOfAnalysedPlayersByPlayer;

import com.poker.reader.dto.AnalysedPlayer;
import com.poker.reader.dto.FileConsolidatedDto;
import com.poker.reader.dto.FileProcessedDto;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class MonitoringProcessor {

    @Value("${consolidated-file-directory}")
    private String consolidatedFileDirectory;

    @Value("${html-directory}")
    private String htmlDirectory;


    FileProcessor fileProcessor = new FileProcessor();
    Set<String> players = new HashSet<>();
    private Map<String, AnalysedPlayer> mapOfAnalysedPlayersByPlayer;


    @PostConstruct
    void setup() throws IOException {
        FileMergeProcessor fileMergeProcessor = new FileMergeProcessor(consolidatedFileDirectory);
        FileConsolidatedDto consolidatedFile = fileMergeProcessor.loadConsolidatedFile();
        mapOfAnalysedPlayersByPlayer =
                convertToMapOfAnalysedPlayersByPlayer(consolidatedFile.getAnalysedPlayers());
    }

    public void process(String filePath) throws IOException {
        log.info("Updating info from " + filePath);
        List<String> linesFromModifiedfile = readLinesFromFile(filePath);
        FileProcessedDto processedFile = fileProcessor.process(linesFromModifiedfile);
        generateTableInfo(processedFile);
        log.info("updated info");
    }

    private void generateTableInfo(FileProcessedDto processedFile) throws IOException {
        int prevSize = players.size();
        players.addAll(processedFile.getPlayers());
        if (players.size() > prevSize) {
            log.info("Updating PLAYERS...");

            List<AnalysedPlayer> analysedPlayerList =
                    players.stream()
                            .filter(player -> mapOfAnalysedPlayersByPlayer.containsKey(player))
                            .map(player -> mapOfAnalysedPlayersByPlayer.get(player))
                            .collect(Collectors.toList());

            FileHtmlProcessor.updatePlayersTableFile(analysedPlayerList, htmlDirectory);

            log.info("UPDATED PLAYERS!!");
        } else {
            log.info("NOT NECESSARY UPDATE PLAYERS");
        }
    }
}
