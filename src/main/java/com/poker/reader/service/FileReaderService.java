package com.poker.reader.service;

import com.poker.reader.configuration.PokerReaderProperties;
import com.poker.reader.dto.HandDTO;
import com.poker.reader.dto.PlayerDTO;
import com.poker.reader.dto.SeatDTO;
import com.poker.reader.entity.*;
import com.poker.reader.mapper.ReaderMapper;
import com.poker.reader.parser.FileReaderProcessor;
import com.poker.reader.repository.HandRepository;
import com.poker.reader.repository.PlayerRepository;
import com.poker.reader.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
        int count = 0;
        for (File file : files) {
            log.info("File {} / {}: {}", ++count, files.size(), file.getAbsolutePath());
            processFile(file);
            listFiles.add(file.getAbsolutePath());
        }
        return listFiles;
    }

    protected void processFile(File file) throws IOException {
        fileReaderProcessor.readFile(file.getAbsolutePath());
        LinkedList<HandDTO> handDTOList = fileReaderProcessor.getHandDTOList();
        Set<Tournament> tournamentSet = new HashSet<>();
        for (HandDTO handDTO : handDTOList) {
            Tournament tournament = tournamentService.findOrPersist(handDTO.getTournamentDTO());
            Hand hand = handService.findOrPersist(tournament, handDTO);
            for(Map.Entry<PlayerDTO, SeatDTO> seatDTOEntry : handDTO.getSeats().entrySet()) {
                Player player = playerService.findOrPersist(seatDTOEntry.getKey());
                PairOfCards pairOfCards = pairOfCardsService.findOrPersist(seatDTOEntry.getValue().getHoldCards());
                Seat seat = seatService.findOrPersist(hand, player, pairOfCards, seatDTOEntry.getValue());
            }
        }
    }
}
