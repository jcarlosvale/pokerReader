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
    private String titleNickname;

    private Integer avgChen;
    private String cssAvgChen;
    private String titleChen;

    private Integer showdowns;
    private String cssShowdowns;

    private Integer showdownPerc;
    private String cssShowdownPerc;

    private Integer totalHands;
    private String cssTotalHands;

    private String cards;

    private Integer position;
    private Integer sbCount;
    private Integer bbCount;
    private Integer btnCount;
    private Integer chen;
    private String handDescription;
    private String cardsOnHand;
    private String place;

    private int stackOfPlayer;
    private String cssStackOfPlayer;
    private String titleStackOfPlayer;

    private int blindsCount;
    private String cssBlindsCount;
    private String titleBlindsCount;

    private Integer noActionSeq;
    private String cssNoActionSeq;
    private String titleNoAction;

    private Integer noActionPerc;
    private String cssNoActionPerc;

    private Integer foldSBSeq;
    private String cssFoldSBSeq;
    private String titleFoldSB;

    private Integer foldSBPerc;
    private String cssFoldSBPerc;

    private Integer foldBBSeq;
    private String cssFoldBBSeq;
    private String titleFoldBB;

    private Integer foldBBPerc;
    private String cssFoldBBPerc;

    private Integer actionBTNSeq;
    private String cssActionBTNSeq;
    private String titleActionBTN;

    private Integer actionBTNPerc;
    private String cssActionBTNPerc;
}
