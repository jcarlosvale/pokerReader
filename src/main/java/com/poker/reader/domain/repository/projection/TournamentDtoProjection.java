package com.poker.reader.domain.repository.projection;

public interface TournamentDtoProjection {
    Long getTournamentId();

    String getFileName();

    String getPlayedAt();

    long getHands();

    int getPlayers();

    int getShowdowns();
}
