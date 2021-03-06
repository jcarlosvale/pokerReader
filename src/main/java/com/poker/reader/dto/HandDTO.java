package com.poker.reader.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;

@Builder
@Data
public class HandDTO {
    private Long id;
    private String level;
    private Integer button;
    private Integer smallBlind;
    private Integer bigBlind;
    private LocalDate date;
    private String tableId;
    private Flop flop;
    private Turn turn;
    private River river;
    private Long sidePot;
    private Long totalPot;
    private Board board;
    private TournamentDTO tournamentDTO;
    private final Map<PlayerDTO, SeatDTO> seats = new HashMap<>();
    private final List<Action> actions = new ArrayList<>();

    public SeatDTO getSeatBySeatId(Integer seatId) {
        Optional<SeatDTO> foundSeat = seats.values().stream().filter(seat -> seat.getSeatId().equals(seatId)).findFirst();
        return foundSeat.orElse(null);
    }
}
