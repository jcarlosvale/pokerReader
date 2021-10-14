package com.poker.reader.domain.repository.projection;

public interface HandDtoProjection {
    long getTournamentId();
    long getHandId();
    String getLevel();
    String getBlinds();
    int getPlayers();
    int getShowdowns();
    String getPlayedAt();
    int getPot();
    String getBoard();
    String getBoardShowdown();
}
