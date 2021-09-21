package com.poker.reader.domain.service;

import com.poker.reader.domain.model.Player;
import com.poker.reader.domain.model.Seat;
import com.poker.reader.domain.repository.PlayerRepository;
import com.poker.reader.domain.repository.SeatRepository;
import com.poker.reader.domain.util.Converter;
import com.poker.reader.view.rs.dto.PlayerDto;
import java.util.ArrayList;
import java.util.List;
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

    private final PlayerRepository playerRepository;
    private final SeatRepository seatRepository;

    public Page<PlayerDto> findPaginated(Pageable pageable) {

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
}
