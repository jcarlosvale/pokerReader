package com.poker.reader.domain.repository.projection;

public interface StackDtoProjection {
    Long getTournamentId();
    Long getHandId();
    String getNickname();
    Integer getStackOfPlayer();
    Integer getBigBlind();
    Integer getBlinds();
    Integer getPot();
}
