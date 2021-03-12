package com.poker.reader.service;

import com.poker.reader.cache.Cache;
import com.poker.reader.dto.TournamentDTO;
import com.poker.reader.entity.Tournament;
import com.poker.reader.mapper.ReaderMapper;
import com.poker.reader.repository.TournamentRepository;
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
public class TournamentService {
    private final Cache<Long, Tournament> tournamentCache;
    private final TournamentRepository tournamentRepository;

    public Tournament findOrPersist(TournamentDTO tournamentDTO) {
        log.debug("Tournament {}", tournamentDTO);
        Tournament tournament = ReaderMapper.toEntity(tournamentDTO);
        if (!tournamentCache.contains(tournament.getId())) {
            tournament = tournamentRepository.findById(tournament.getId()).orElse(tournamentRepository.save(tournament));
            tournamentCache.put(tournament.getId(), tournament);
        }
        return tournamentCache.get(tournament.getId());
    }

    public Tournament find(Long tournamentId) {
        return Objects.nonNull(tournamentCache.get(tournamentId)) ?
                tournamentCache.get(tournamentId) :
                tournamentRepository.findById(tournamentId).orElse(null);
    }
}
