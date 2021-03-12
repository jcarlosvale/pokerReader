package com.poker.reader.service;

import com.poker.reader.cache.Cache;
import com.poker.reader.dto.HandDTO;
import com.poker.reader.entity.Hand;
import com.poker.reader.entity.Tournament;
import com.poker.reader.mapper.ReaderMapper;
import com.poker.reader.repository.HandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


@Service
@Log4j2
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class HandService {

    private final Cache<Long, Hand> handCache;
    private final HandRepository handRepository;

    public Hand findOrPersist(Tournament tournament, HandDTO handDTO) {
        log.debug("Hand {}",handDTO);
        Hand hand = ReaderMapper.toEntity(handDTO);
        if (!handCache.contains(hand.getId())) {
            hand.setTournament(tournament);
            hand = handRepository.findById(hand.getId()).orElse(handRepository.save(hand));
            handCache.put(hand.getId(), hand);
        }
        return handCache.get(hand.getId());
    }

    public Hand find(Long handId) {
        return Objects.nonNull(handCache.get(handId)) ?
                handCache.get(handId) :
                handRepository.findById(handId).orElse(null);
    }
}
