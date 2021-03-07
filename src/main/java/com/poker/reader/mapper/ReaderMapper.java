package com.poker.reader.mapper;

import com.poker.reader.dto.HandDTO;
import com.poker.reader.dto.PlayerDTO;
import com.poker.reader.dto.SeatDTO;
import com.poker.reader.dto.TournamentDTO;
import com.poker.reader.entity.Hand;
import com.poker.reader.entity.Player;
import com.poker.reader.entity.Seat;
import com.poker.reader.entity.Tournament;

public class ReaderMapper {
    public static Player toEntity(PlayerDTO playerDTO) {
        return Player
                .builder()
                .nickname(playerDTO.getNickname())
                .build();
    }

    public static Tournament toEntity(TournamentDTO tournamentDTO) {
        return Tournament
                .builder()
                .id(tournamentDTO.getId())
                .buyIn(tournamentDTO.getBuyIn())
                .build();
    }

    public static Hand toEntity(HandDTO handDTO) {
        return Hand
                .builder()
                .id(handDTO.getId())
                .level(handDTO.getLevel())
                .button(handDTO.getButton())
                .smallBlind(handDTO.getSmallBlind())
                .bigBlind(handDTO.getBigBlind())
                .date(handDTO.getDate())
                .tableId(handDTO.getTableId())
                .sidePot(handDTO.getSidePot())
                .totalPot(handDTO.getTotalPot())
                .build();
    }

    public static Seat toEntity(SeatDTO seatDTO) {
        Seat seat = Seat
                    .builder()
                    .seatId(seatDTO.getSeatId())
                    .stack(seatDTO.getStack())
                    .build();
        if (seatDTO.getHoldCards() != null) {
            seat.setCard1(seatDTO.getHoldCards().getCard1());
            seat.setCard2(seatDTO.getHoldCards().getCard2());
        }
        return seat;
    }
}
