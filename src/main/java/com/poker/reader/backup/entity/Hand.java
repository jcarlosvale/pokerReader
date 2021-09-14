package com.poker.reader.backup.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;

@Builder
@Data
public class Hand {
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
    private Tournament tournament;
    private final Map<Player,Seat> seats = new HashMap<>();
    private final List<Action> actions = new ArrayList<>();

    public Seat getSeatBySeatId(Integer seatId) {
        Optional<Seat> foundSeat = seats.values().stream().filter(seat -> seat.getSeatId().equals(seatId)).findFirst();
        return foundSeat.orElse(null);
    }
}
