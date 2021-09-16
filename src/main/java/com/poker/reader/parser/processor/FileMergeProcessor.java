package com.poker.reader.parser.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poker.reader.dto.AnalysedPlayer;
import com.poker.reader.dto.FileConsolidatedDto;
import com.poker.reader.dto.FileProcessedDto;
import com.poker.reader.dto.NormalisedCardsDto;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;

@Log4j2
public class FileMergeProcessor {

    private final static String FILE_CONSOLIDATED = "consolidated.json";

    private FileConsolidatedDto fileConsolidatedDto;
    private final String consolidatedFile;
    private final ObjectMapper objectMapper;

    public FileMergeProcessor(String outputDirectory) {
        this.consolidatedFile = outputDirectory + File.separator + FILE_CONSOLIDATED;
        this.objectMapper = new ObjectMapper();
    }

    public void mergeFiles(List<File> filesToMerge) throws IOException {
        loadConsolidatedFile();
        int count = 1;
        for(File file: filesToMerge) {
            long start = System.currentTimeMillis();
            log.info("Merging " + file.getName());
            mergeFiles(file);
            long end = System.currentTimeMillis();
            log.info("Merged " + count + "/" + filesToMerge.size() + " " + (end - start) + "ms");
            count++;
        }
        //set #players
        fileConsolidatedDto.setTotalPlayers(fileConsolidatedDto.getPlayers().size());
        fileConsolidatedDto.setTotalTournaments(fileConsolidatedDto.getTournaments().size());
        //set #tournaments
        FileUtils.write(new File(consolidatedFile),
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(fileConsolidatedDto) , "UTF-8");
    }

    private void mergeFiles(File file) throws IOException {
        FileProcessedDto fileProcessed = objectMapper.readValue(file, FileProcessedDto.class);
        //verify tournament has been processed
        if (fileConsolidatedDto.getTournaments().contains(fileProcessed.getTournament())) {
            log.info("Tournament already processed.");
        } else {
            fileConsolidatedDto.getTournaments().add(fileProcessed.getTournament());
            fileConsolidatedDto.setTotalHands(fileConsolidatedDto.getTotalHands()+ fileProcessed.getTotalHands());
            fileConsolidatedDto.getPlayers().addAll(fileProcessed.getPlayers());
            //analysed players
            Map<String, AnalysedPlayer> mapOfAnalysedPlayersByPlayer =
                    convertToMapOfAnalysedPlayersByPlayer(fileConsolidatedDto.getAnalysedPlayers());
            for(AnalysedPlayer analysedPlayer : fileProcessed.getAnalysedPlayers()) {
//                if(analysedPlayer.getPlayer().equals("thunder402")) {
//                    System.out.println("debug");
//                }
                if (mapOfAnalysedPlayersByPlayer.containsKey(analysedPlayer.getPlayer())) {
                    AnalysedPlayer analysedPlayerFromConsolidated =
                            mapOfAnalysedPlayersByPlayer.get(analysedPlayer.getPlayer());
                    mapOfAnalysedPlayersByPlayer.put(analysedPlayer.getPlayer(),
                            mergeAnalysedPlayers(analysedPlayerFromConsolidated, analysedPlayer));
                } else {
                    mapOfAnalysedPlayersByPlayer.put(analysedPlayer.getPlayer(), analysedPlayer);
                }
            }
            fileConsolidatedDto.setAnalysedPlayers(new ArrayList<>(mapOfAnalysedPlayersByPlayer.values()));
        }
    }

    private AnalysedPlayer mergeAnalysedPlayers(AnalysedPlayer analysedPlayerFromConsolidated, AnalysedPlayer analysedPlayer) {

        Map<NormalisedCardsDto, Integer> normalisedCardsFromConsolidated =
                analysedPlayerFromConsolidated.getNormalisedCardsMap();

        analysedPlayer
                .getNormalisedCardsMap()
                .forEach((normalisedCardsDto, counter) -> {
                    if (normalisedCardsFromConsolidated.containsKey(normalisedCardsDto)) {
                        int counterFromConsolidated = normalisedCardsFromConsolidated.get(normalisedCardsDto);
                        normalisedCardsFromConsolidated.put(normalisedCardsDto, counter + counterFromConsolidated);
                    } else {
                        normalisedCardsFromConsolidated.put(normalisedCardsDto, counter);
                    }
                });

        analysedPlayerFromConsolidated.getRawCards().addAll(analysedPlayer.getRawCards());

        return analysedPlayerFromConsolidated;
    }

    private Map<String, AnalysedPlayer> convertToMapOfAnalysedPlayersByPlayer(List<AnalysedPlayer> analysedPlayers) {
        Map<String, AnalysedPlayer> mapOfAnalysedPlayersByPlayer = new HashMap<>();
        analysedPlayers
                .forEach(analysedPlayer -> mapOfAnalysedPlayersByPlayer.put(analysedPlayer.getPlayer(), analysedPlayer));
        return mapOfAnalysedPlayersByPlayer;
    }

    public FileConsolidatedDto loadConsolidatedFile() throws IOException {
        if (Files.exists(Paths.get(consolidatedFile))) {
            fileConsolidatedDto = objectMapper.readValue(new File(consolidatedFile), FileConsolidatedDto.class);
        } else {
            fileConsolidatedDto =
                    new FileConsolidatedDto(0, 0, 0, new HashSet<>(), new HashSet<>(), new ArrayList<>());
        }
        return fileConsolidatedDto;
    }
}
