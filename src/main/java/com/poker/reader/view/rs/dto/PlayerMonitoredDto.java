package com.poker.reader.view.rs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerMonitoredDto {

    private String nickname;
    private String cssNickname;

    private Integer avgChen;
    private String cssAvgChen;

    private int stackOfPlayer;
    private String cssStackOfPlayer;

    private int blindsCount;
    private String cssBlindsCount;

    private Integer noActionSeq;
    private String cssNoActionSeq;

    private Integer noActionPerc;
    private String cssNoActionPerc;

    private Integer foldSBSeq;
    private String cssFoldSBSeq;

    private Integer foldSBPerc;
    private String cssFoldSBPerc;

    private Integer foldBBSeq;
    private String cssFoldBBSeq;

    private Integer foldBBPerc;
    private String cssFoldBBPerc;

    private Integer actionBTNSeq;
    private String cssActionBTNSeq;

    private Integer actionBTNPerc;
    private String cssActionBTNPerc;

    private Integer showdowns;
    private String cssShowdowns;

    private Integer showdownPerc;
    private String cssShowdownPerc;

    private Integer totalHands;
    private String cssTotalHands;

    private String cards;
}
