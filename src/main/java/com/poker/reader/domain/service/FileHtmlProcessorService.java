package com.poker.reader.domain.service;

import com.poker.reader.domain.model.Hand;
import com.poker.reader.domain.model.Player;
import com.poker.reader.domain.model.Seat;
import com.poker.reader.domain.repository.HandRepository;
import com.poker.reader.domain.repository.PlayerRepository;
import com.poker.reader.domain.repository.SeatRepository;
import com.poker.reader.domain.repository.TournamentRepository;
import com.poker.reader.domain.util.Converter;
import com.poker.reader.view.rs.dto.PlayerDto;
import com.poker.reader.view.rs.dto.TournamentDto;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Component
public class FileHtmlProcessorService {

    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;
    private final SeatRepository seatRepository;
    private final HandRepository handRepository;

    public Page<PlayerDto> findPaginatedPlayers(Pageable pageable) {

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();

        Page<Player> pagePlayers = playerRepository.findAll(pageable);
        List<PlayerDto> playerDtoList = new ArrayList<>();

        for(Player player: pagePlayers.getContent()) {
            List<Seat> seatsFromPlayer = seatRepository.findByPlayer(player);
            playerDtoList.add(Converter.toPlayerDto(player, seatsFromPlayer));
        }

        return new PageImpl<>(playerDtoList, PageRequest.of(currentPage, pageSize), playerRepository.count());
    }

    public Page<TournamentDto> findPaginatedTournaments(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();

        var tournamentsDtoList =
                tournamentRepository
                        .findAll(pageable)
                        .getContent()
                        .stream()
                        .map(tournament ->
                                TournamentDto
                                        .builder()
                                        .tournamentId(tournament.getTournamentId())
                                        .fileName(tournament.getFileName())
                                        .createdAt(tournament.getCreatedAt())
                                        .build())
                        .collect(Collectors.toList());

        return new PageImpl<>(tournamentsDtoList, PageRequest.of(currentPage, pageSize), tournamentRepository.count());

    }

    public List<PlayerDto> getLastPlayersFromTournament(String tournamentId) {
        Hand hand = handRepository.findMostRecent(tournamentId);
        return
        seatRepository
                .findByHand(hand)
                .stream()
                .filter(Predicate.not(seat -> seat.getPlayer().getNickname().equals("jcarlos.vale")))
                .map(seat -> {
                    List<Seat> seatsFromPlayer = seatRepository.findByPlayer(seat.getPlayer());
                    return Converter.toPlayerDto(seat.getPlayer(), seatsFromPlayer);
                })
                .collect(Collectors.toList());
    }
}
