package com.poker.reader.service;

import com.poker.reader.cache.Cache;
import com.poker.reader.dto.SeatDTO;
import com.poker.reader.entity.Hand;
import com.poker.reader.entity.PairOfCards;
import com.poker.reader.entity.Player;
import com.poker.reader.entity.Seat;
import com.poker.reader.mapper.ReaderMapper;
import com.poker.reader.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class SeatService {

    private final Cache<Long, Seat> seatCache;
    private final SeatRepository seatRepository;

    public Seat findOrPersist(Hand hand, Player player, PairOfCards pairOfCards, SeatDTO seatDTO) {
        log.debug("Seat {}", seatDTO);
        Seat seat = ReaderMapper.toEntity(seatDTO);
        if (!seatCache.contains(seat.getId())) {
            seat.setHand(hand);
            seat.setPlayer(player);
            seat.setPairOfCards(pairOfCards);
            seat = seatRepository.findFirstBySeatIdAndHandAndPlayer(seat.getSeatId(), seat.getHand(), seat.getPlayer()).orElse(seatRepository.save(seat));
            seatCache.put(seat.getId(), seat);
        }
        return seatCache.get(seat.getId());
    }
}
