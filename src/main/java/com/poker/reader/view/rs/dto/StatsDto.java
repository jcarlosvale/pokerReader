package com.poker.reader.view.rs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsDto {

    private long tournamentId;

    private long handId;

    private String level;

    private String playedAt;

    private String boardShowdown;

    private String blinds;

    private int pot;

    private Integer chen;

    private String cards;

    private boolean isButton;

    private boolean isSmallBlind;

    private boolean isBigBlind;

    private int stackOfPlayer;

    private Integer blindsCount;

    private boolean isWinner;

    private boolean isLose;

    private String handDescription;

    private String place;

    private String pokerPosition;

    private Integer position;

    private String nickname;

    private int total;

    //how many hands without playing, excluding BB, SB
    private Integer noActionCount;

    private Integer noActionPerc;

    private Integer noActionSeq;

    private Integer foldSBSeq;

    private Integer foldSBCount;

    private Integer foldSBPerc;

    private Integer foldBBSeq;

    private Integer foldBBCount;

    private Integer foldBBPerc;

    private Integer actionBTNSeq;

    private Integer actionBTNCount;

    private Integer actionBTNPerc;

    private int sbCount;

    private int bbCount;

    private int btnCount;

    private int utgCount;

    private int mpCount;

    private int ljCount;

    private int hjCount;

    private int coCount;

    private String labelNoActionMonitoring;

    private String labelNoActionSBMonitoring;

    private String labelNoActionBBMonitoring;

    private String labelActionBTNMonitoring;
}
