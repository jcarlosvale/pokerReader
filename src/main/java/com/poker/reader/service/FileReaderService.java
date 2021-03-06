package com.poker.reader.service;

import com.poker.reader.configuration.PokerReaderProperties;
import com.poker.reader.dto.HandDTO;
import com.poker.reader.dto.PlayerDTO;
import com.poker.reader.dto.SeatDTO;
import com.poker.reader.entity.PairOfCards;
import com.poker.reader.entity.Player;
import com.poker.reader.mapper.CardsMapper;
import com.poker.reader.mapper.PlayerMapper;
import com.poker.reader.parser.FileReaderProcessor;
import com.poker.reader.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
        savePlayers();
        return listFiles;
    }

    private void savePlayers() {
//        log.info(" Persisting {} Players", fileReaderProcessor.getPlayerDTOS().size());
//        log.info(" Persisting {} Hands", fileReaderProcessor.getHandDTOList().size());
//        //players
//        Map<PlayerDTO, Player> playerSet =
//                fileReaderProcessor.getPlayerDTOS().stream().collect(Collectors.toMap(playerDTO -> playerDTO, PlayerMapper::toEntity));
//        //hands
//        for(HandDTO handDTO : fileReaderProcessor.getHandDTOList()) {
//            for(Map.Entry<PlayerDTO, SeatDTO> entry: handDTO.getSeats().entrySet()) {
//                if(entry.getValue().getHoldCards() != null) {
//                    PairOfCards pairOfCardsFromSeat =  CardsMapper.fromHoldCards(entry.getValue().getHoldCards());
//                    Set<PairOfCards> pairOfCards = playerSet.get(entry.getKey()).getCard();
//                    if (pairOfCards == null) {
//                        pairOfCards = new HashSet<>();
//                    }
//                    pairOfCards.add(pairOfCardsFromSeat);
//                }
//            }
//        }
//        playerRepository.saveAll(playerSet.values());
//        playerRepository.flush();
    }
}
