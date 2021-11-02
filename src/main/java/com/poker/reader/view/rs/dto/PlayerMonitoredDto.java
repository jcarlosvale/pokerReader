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

    //1
    private String nickname;
    private String cssNickname;
    private String titleNickname;

    //2
    //general CHEN
    private Integer avgChen;
    private String cssAvgChen;
    private String titleAvgChen;

    //3
    //general showdowns
    private Integer showdowns;
    private String cssShowdowns;
    private String titleShowdowns;

    //4
    //general % showdowns
    private Integer showdownPerc;
    private String cssShowdownPerc;
    private String titleShowdownPerc;

    //5
    private Integer totalHands;
    private String cssTotalHands;
    private String titleTotalHands;

    //6
    //general cards
    private String cards;
    private String cssCards;
    private String titleCards;

    //7
    //table position
    private Integer position;
    private String cssPosition;
    private String titlePosition;

    //8
    private Integer sbCount;
    private String cssSbCount;
    private String titleSbCount;

    //9
    private Integer bbCount;
    private String cssBbCount;
    private String titleBbCount;

    //10
    private Integer btnCount;
    private String cssBtnCount;
    private String titleBtnCount;

    //11
    private Integer chen;
    private String cssChen;
    private String titleChen;

    //12
    private String handDescription;
    private String cssHandDescription;
    private String titleHandDescription;

    //13
    private String cardsOnHand;
    private String cssCardsOnHand;
    private String titleCardsOnHand;

    //14
    private String place;
    private String cssPlace;
    private String titlePlace;

    //15
    private int stackOfPlayer;
    private String cssStackOfPlayer;
    private String titleStackOfPlayer;

    //16
    private int blindsCount;
    private String cssBlindsCount;
    private String titleBlindsCount;

    //17
    private Integer noActionSeq;
    private String cssNoActionSeq;
    private String titleNoActionSeq;

    //18
    private Integer noActionPerc;
    private String cssNoActionPerc;
    private String titleNoActionPerc;

    //19
    private Integer foldSBSeq;
    private String cssFoldSBSeq;
    private String titleFoldSBSeq;

    //20
    private Integer foldSBPerc;
    private String cssFoldSBPerc;
    private String titleFoldSBPerc;

    //21
    private Integer foldBBSeq;
    private String cssFoldBBSeq;
    private String titleFoldBBSeq;

    //22
    private Integer foldBBPerc;
    private String cssFoldBBPerc;
    private String titleFoldBBPerc;

    //23
    private Integer actionBTNSeq;
    private String cssActionBTNSeq;
    private String titleActionBTNSeq;

    //24
    private Integer actionBTNPerc;
    private String cssActionBTNPerc;
    private String titleActionBTNPerc;

    //25
    private Integer actionBTNCount;
    private String cssActionBTNCount;
    private String titleActionBTNCount;

    //26
    private Integer foldBBCount;
    private String cssFoldBBCount;
    private String titleFoldBBCount;

    //27
    private Integer foldSBCount;
    private String cssFoldSBCount;
    private String titleFoldSBCount;

    //28
    private Integer noActionCount;
    private String cssNoActionCount;
    private String titleNoActionCount;
}
