package com.poker.reader.domain.repository.projection;

public interface PlayerDetailsDtoProjection {
    long getTournamentId();
    long getHandId();
    String getLevel();
    String getPlayedAt();
    String getBoardShowdown();
    String getBlinds();
    String getBoard();
    int getPot();
    String getNickname();
    Integer getChen();
    String getCards();
    boolean isButton();
    boolean isSmallBlind();
    boolean isBigBlind();
    int getStackOfPlayer();
    Integer getBlindsCount();
    boolean getIsWinner();
    boolean getIsLose();
    String getHandDescription();
    String getPlace();
    String getPokerPosition();
    Integer getPosition();
}
