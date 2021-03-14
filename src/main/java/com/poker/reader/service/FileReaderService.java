package com.poker.reader.service;

import com.poker.reader.configuration.PokerReaderProperties;
import com.poker.reader.dto.HandDTO;
import com.poker.reader.dto.PlayerDTO;
import com.poker.reader.dto.SeatDTO;
import com.poker.reader.entity.Hand;
import com.poker.reader.entity.PairOfCards;
import com.poker.reader.entity.Player;
import com.poker.reader.entity.Tournament;
import com.poker.reader.parser.FileReaderProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class FileReaderService {

    private final TournamentService tournamentService;
    private final HandService handService;
    private final PlayerService playerService;
    private final PairOfCardsService pairOfCardsService;
    private final SeatService seatService;

    private final FileReaderProcessor fileReaderProcessor;
    private final PokerReaderProperties pokerReaderProperties;

    public List<String> processFilesFromFolder() throws IOException {
        List<String> listFiles = new ArrayList<>();
        String directory = pokerReaderProperties.getFolder();
        List<File> files = fileReaderProcessor.readDirectory(directory);
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            log.info("File {} / {}: {}", i+1, files.size(), file.getAbsolutePath());
            processFile(file);
            listFiles.add(file.getAbsolutePath());
        }
        return listFiles;
    }

    protected void processFile(File file) throws IOException {
        fileReaderProcessor.readFile(file.getAbsolutePath());
        LinkedList<HandDTO> handDTOList = fileReaderProcessor.getHandDTOList();
        for (int i = 0; i < handDTOList.size(); i++) {
            HandDTO handDTO = handDTOList.get(i);
            log.info("Hand {} / {}: Tournament {} HandId {}", i+1, handDTOList.size(), handDTO.getTournamentDTO().getId(), handDTO.getId());
            if (Objects.nonNull(handService.find(handDTO.getId()))) {
                log.info("Tournament {}, Hand {} already in the database", handDTO.getTournamentDTO(), handDTO.getId());
            } else {
                log.info("Saving in the database Tournament {}, Hand {}", handDTO.getTournamentDTO(), handDTO.getId());
                Tournament tournament = tournamentService.findOrPersist(handDTO.getTournamentDTO());
                Hand hand = handService.findOrPersist(tournament, handDTO);
                for (Map.Entry<PlayerDTO, SeatDTO> seatDTOEntry : handDTO.getSeats().entrySet()) {
                    Player player = playerService.findOrPersist(seatDTOEntry.getKey());
                    PairOfCards pairOfCards = pairOfCardsService.findOrPersist(seatDTOEntry.getValue().getHoldCards());
                    seatService.findOrPersist(hand, player, pairOfCards, seatDTOEntry.getValue());
                }
            }
        }
    }
}
