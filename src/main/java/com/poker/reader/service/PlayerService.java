package com.poker.reader.service;

import com.poker.reader.cache.Cache;
import com.poker.reader.dto.PlayerDTO;
import com.poker.reader.entity.Player;
import com.poker.reader.mapper.ReaderMapper;
import com.poker.reader.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class PlayerService {
    private final Cache<String, Player> playerCache;
    private final PlayerRepository playerRepository;

    public Player findOrPersist(PlayerDTO playerDTO) {
        log.debug("Player {}", playerDTO);
        Player player = ReaderMapper.toEntity(playerDTO);
        if(!playerCache.contains(player.getNickname())) {
            player = playerRepository.findById(player.getNickname()).orElse(playerRepository.save(player));
            playerCache.put(player.getNickname(), player);
        }
        return playerCache.get(player.getNickname());
    }
}
