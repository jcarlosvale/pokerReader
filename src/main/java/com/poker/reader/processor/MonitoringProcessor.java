package com.poker.reader.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class MonitoringProcessor {
/*
    @Value("${consolidated-file-directory}")
    private String consolidatedFileDirectory;

    @Value("${html-directory}")
    private String htmlDirectory;


    FileProcessorService fileProcessorService = new FileProcessorService();
    Set<String> players = new HashSet<>();
    private Map<String, AnalysedPlayer> mapOfAnalysedPlayersByPlayer;
    //TODO: remove these fields
    private Set<String> playersFromLastHand;
    private String tournament;


    @PostConstruct
    void setup() throws IOException {
        FileMergeProcessor fileMergeProcessor = new FileMergeProcessor(consolidatedFileDirectory);
        FileConsolidatedDto consolidatedFile = fileMergeProcessor.loadConsolidatedFile();
        mapOfAnalysedPlayersByPlayer =
                convertToMapOfAnalysedPlayersByPlayer(consolidatedFile.getAnalysedPlayers());
    }

    public void processFile(String filePath) throws IOException {
        log.info("Updating info from " + filePath);
        List<String> linesFromModifiedfile = readLinesFromFile(filePath);
        FileProcessedDto processedFile = fileProcessorService.processFile(fileName, linesFromModifiedfile);
        playersFromLastHand = fileProcessorService.getPlayersFromLastHand();
        tournament = processedFile.getTournament();
        generateTableInfo(processedFile);
        log.info("updated info");
    }

    private void generateTableInfo(FileProcessedDto processedFile) throws IOException {
        int prevSize = players.size();
        players.addAll(processedFile.getPlayers());
        if (!playersFromLastHand.isEmpty()) {
            log.info("Updating PLAYERS...");
            log.info("Tournament " + tournament);
            log.info("Tournament " + tournament);
            log.info("Tournament " + tournament);


            List<AnalysedPlayer> analysedPlayerList =
                    playersFromLastHand.stream()
                            .filter(player -> mapOfAnalysedPlayersByPlayer.containsKey(player))
                            .map(player -> mapOfAnalysedPlayersByPlayer.get(player))
                            .collect(Collectors.toList());

            FileHtmlProcessorService.updatePlayersTableFile(analysedPlayerList, tournament, htmlDirectory);

            log.info("UPDATED PLAYERS!!");
        } else {
            log.info("NOT NECESSARY UPDATE PLAYERS");
        }
    }

 */
}