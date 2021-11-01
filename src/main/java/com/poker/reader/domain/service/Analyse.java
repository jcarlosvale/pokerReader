package com.poker.reader.domain.service;

import com.poker.reader.view.rs.dto.PlayerMonitoredDto;
import com.poker.reader.view.rs.dto.RecommendationDto;

import java.util.HashMap;
import java.util.Map;

public class Analyse {

    public static final int MIN_BLINDS = 18;

    public static RecommendationDto analyseStack(long stackFromHero, Integer avgStack, int blinds) {
        String recommendation = null;
        String css = null;
        if(blinds <= 10) {
            recommendation = "ALL IN, LESS THAN 10 BLINDS";
            css = PlayerStyle.SUPER_LOOSE.getCss();
        }
        else if ((stackFromHero < avgStack) && (blinds <= MIN_BLINDS)){
            recommendation = "ALL IN, LESS THAN AVERAGE BLINDS < " + MIN_BLINDS;
            css = PlayerStyle.SUPER_LOOSE.getCss();
        }
        else if (stackFromHero > avgStack) {
            recommendation = "ABOVE AVG STACK";
            css = PlayerStyle.SUPER_TIGHT.getCss();
        }
        else {
            recommendation = "PLAY!";
            css = PlayerStyle.TIGHT.getCss();
        }
        return RecommendationDto.builder().recommendation(recommendation).css(css).build();
    }

    public static void analysePlayer(PlayerMonitoredDto playerMonitoredDto, int avgStack) {

        PlayerStyle playerStyleAvgChen = analyseAvgChen(playerMonitoredDto.getAvgChen());
        PlayerStyle playerStyleStackOfPlayer = analyseStackOfPlayer(playerMonitoredDto.getStackOfPlayer(), avgStack);
        PlayerStyle playerStyleBlindsCount = analyseBlindsCount(playerMonitoredDto.getBlindsCount());
        PlayerStyle playerStyleNoActionSeq = analyseNoActionSeq(playerMonitoredDto.getNoActionSeq());
        PlayerStyle playerStyleNoActionPerc = analyseNoActionPerc(playerMonitoredDto.getNoActionPerc());
        PlayerStyle playerStyleFoldSBSeq = analyseFoldBlindsSeq(playerMonitoredDto.getFoldSBSeq());
        PlayerStyle playerStyleFoldSBPerc = analyseFoldBlindsPerc(playerMonitoredDto.getFoldSBPerc());
        PlayerStyle playerStyleFoldBBSeq = analyseFoldBlindsSeq(playerMonitoredDto.getFoldBBSeq());
        PlayerStyle playerStyleFoldBBPerc = analyseFoldBlindsPerc(playerMonitoredDto.getFoldBBPerc());
        PlayerStyle playerStyleActionBTNSeq = analyseActionBTNSeq(playerMonitoredDto.getActionBTNSeq());
        PlayerStyle playerStyleActionBTNPerc = analyseActionBTNPerc(playerMonitoredDto.getActionBTNPerc());
        PlayerStyle playerStyleShowdowns = analyseShowdowns(playerMonitoredDto.getShowdowns());
        PlayerStyle playerStyleShowdownPerc = analyseShowdownPerc(playerMonitoredDto.getShowdownPerc());
        PlayerStyle playerStyleTotalHands = analyseTotalHands(playerMonitoredDto.getTotalHands());

        PlayerStyle playerStyleNickname = analyse(
                playerStyleAvgChen,
                playerStyleStackOfPlayer,
                playerStyleBlindsCount,
                playerStyleNoActionSeq,
                playerStyleNoActionPerc,
                playerStyleFoldSBSeq,
                playerStyleFoldSBPerc,
                playerStyleFoldBBSeq,
                playerStyleFoldBBPerc,
                playerStyleActionBTNSeq,
                playerStyleActionBTNPerc,
                playerStyleShowdowns,
                playerStyleShowdownPerc,
                playerStyleTotalHands
        );

        playerMonitoredDto.setCssAvgChen(playerStyleAvgChen.getCss());
        playerMonitoredDto.setCssStackOfPlayer(playerStyleStackOfPlayer.getCss());
        playerMonitoredDto.setCssBlindsCount(playerStyleBlindsCount.getCss());
        playerMonitoredDto.setCssNoActionSeq(playerStyleNoActionSeq.getCss());
        playerMonitoredDto.setCssNoActionPerc(playerStyleNoActionPerc.getCss());
        playerMonitoredDto.setCssFoldSBSeq(playerStyleFoldSBSeq.getCss());
        playerMonitoredDto.setCssFoldSBPerc(playerStyleFoldSBPerc.getCss());
        playerMonitoredDto.setCssFoldBBSeq(playerStyleFoldBBSeq.getCss());
        playerMonitoredDto.setCssFoldBBPerc(playerStyleFoldBBPerc.getCss());
        playerMonitoredDto.setCssActionBTNSeq(playerStyleActionBTNSeq.getCss());
        playerMonitoredDto.setCssActionBTNPerc(playerStyleActionBTNPerc.getCss());
        playerMonitoredDto.setCssShowdowns(playerStyleShowdowns.getCss());
        playerMonitoredDto.setCssShowdownPerc(playerStyleShowdownPerc.getCss());
        playerMonitoredDto.setCssTotalHands(playerStyleTotalHands.getCss());

        playerMonitoredDto.setCssNickname(playerStyleNickname.getCss());
    }

    private static PlayerStyle analyse(PlayerStyle ... playerStyles) {
        if (playerStyles.length == 0) {
            return PlayerStyle.NONE;
        } else {
            Map<PlayerStyle, Integer> countPlayerStyles = new HashMap<>();
            for(PlayerStyle playerStyle: playerStyles) {
                countPlayerStyles.put(playerStyle, countPlayerStyles.getOrDefault(playerStyle, 0) + 1);
            }
            PlayerStyle maxPlayerStyle = PlayerStyle.NONE;
            int maxStyle = -1;
            for(PlayerStyle playerStyle : countPlayerStyles.keySet()) {
                int temp = countPlayerStyles.get(playerStyle);
                if (temp > maxStyle) {
                    maxStyle = temp;
                    maxPlayerStyle = playerStyle;
                }
            }
            return maxPlayerStyle;
        }
    }

    private static PlayerStyle analyseTotalHands(Integer totalHands) {
        if (totalHands == null) {
            return PlayerStyle.NONE;
        }
        if (totalHands >= 70) {
            return PlayerStyle.SUPER_TIGHT;
        }
        if (totalHands >= 50) {
            return PlayerStyle.TIGHT;
        }
        if (totalHands >= 20) {
            return PlayerStyle.LIMPER;
        }
        if (totalHands >= 10) {
            return PlayerStyle.LOOSE;
        }
        return PlayerStyle.SUPER_LOOSE;
    }

    private static PlayerStyle analyseShowdownPerc(Integer showdownPerc) {
        if (showdownPerc == null) {
            return PlayerStyle.NONE;
        }
        if (showdownPerc >= 70) {
            return PlayerStyle.SUPER_LOOSE;
        }
        if (showdownPerc >= 60) {
            return PlayerStyle.LOOSE;
        }
        if (showdownPerc >= 50) {
            return PlayerStyle.LIMPER;
        }
        if (showdownPerc >= 30) {
            return PlayerStyle.TIGHT;
        }
        if (showdownPerc >= 10) {
            return PlayerStyle.SUPER_TIGHT;
        }
        return PlayerStyle.AGGRESSIVE;
    }

    private static PlayerStyle analyseShowdowns(Integer showdowns) {
        if (showdowns == null) {
            return PlayerStyle.NONE;
        }
        if (showdowns >= 20) {
            return PlayerStyle.SUPER_LOOSE;
        }
        if (showdowns >= 15) {
            return PlayerStyle.LOOSE;
        }
        if (showdowns >= 10) {
            return PlayerStyle.LIMPER;
        }
        if (showdowns >= 5) {
            return PlayerStyle.TIGHT;
        }
        if (showdowns >= 3) {
            return PlayerStyle.SUPER_TIGHT;
        }
        return PlayerStyle.AGGRESSIVE;
    }

    private static PlayerStyle analyseActionBTNPerc(Integer actionBTNPerc) {
        if (actionBTNPerc == null) {
            return PlayerStyle.NONE;
        }
        if (actionBTNPerc >= 70) {
            return PlayerStyle.AGGRESSIVE;
        }
        if (actionBTNPerc >= 60) {
            return PlayerStyle.SUPER_LOOSE;
        }
        if (actionBTNPerc >= 50) {
            return PlayerStyle.LOOSE;
        }
        if (actionBTNPerc >= 30) {
            return PlayerStyle.LIMPER;
        }
        return PlayerStyle.SUPER_TIGHT;
    }

    private static PlayerStyle analyseActionBTNSeq(Integer actionBTNSeq) {
        if (actionBTNSeq == null) {
            return PlayerStyle.NONE;
        }
        if (actionBTNSeq >= 5) {
            return PlayerStyle.AGGRESSIVE;
        }
        if (actionBTNSeq >= 3) {
            return PlayerStyle.SUPER_LOOSE;
        }
        if (actionBTNSeq >= 2) {
            return PlayerStyle.LOOSE;
        }
        if (actionBTNSeq >= 1) {
            return PlayerStyle.LIMPER;
        }
        return PlayerStyle.SUPER_TIGHT;
    }

    private static PlayerStyle analyseFoldBlindsPerc(Integer foldBlindsPerc) {
        if (foldBlindsPerc == null) {
            return PlayerStyle.NONE;
        }
        if (foldBlindsPerc >= 70) {
            return PlayerStyle.FREE_BLIND;
        }
        if (foldBlindsPerc >= 60) {
            return PlayerStyle.SUPER_TIGHT;
        }
        if (foldBlindsPerc >= 50) {
            return PlayerStyle.TIGHT;
        }
        if (foldBlindsPerc >= 30) {
            return PlayerStyle.LIMPER;
        }
        return PlayerStyle.AGGRESSIVE;

    }

    private static PlayerStyle analyseFoldBlindsSeq(Integer foldBlindsSeq) {
        if (foldBlindsSeq == null) {
            return PlayerStyle.NONE;
        }
        if (foldBlindsSeq >= 5) {
            return PlayerStyle.FREE_BLIND;
        }
        if (foldBlindsSeq >= 3) {
            return PlayerStyle.SUPER_TIGHT;
        }
        if (foldBlindsSeq >= 2) {
            return PlayerStyle.TIGHT;
        }
        if (foldBlindsSeq >= 1) {
            return PlayerStyle.LIMPER;
        }
        return PlayerStyle.AGGRESSIVE;
    }

    private static PlayerStyle analyseNoActionPerc(Integer noActionPerc) {
        if (noActionPerc == null) {
            return PlayerStyle.NONE;
        }
        if (noActionPerc >= 70) {
            return PlayerStyle.SUPER_TIGHT;
        }
        if (noActionPerc >= 60) {
            return PlayerStyle.TIGHT;
        }
        if (noActionPerc >= 50) {
            return PlayerStyle.LIMPER;
        }
        if (noActionPerc >= 30) {
            return PlayerStyle.LOOSE;
        }
        return PlayerStyle.SUPER_LOOSE;
    }

    private static PlayerStyle analyseNoActionSeq(Integer noActionSeq) {
        if (noActionSeq == null) {
            return PlayerStyle.NONE;
        }
        if (noActionSeq >= 10) {
            return PlayerStyle.SUPER_TIGHT;
        }
        if (noActionSeq >= 5) {
            return PlayerStyle.TIGHT;
        }
        if (noActionSeq >= 3) {
            return PlayerStyle.LIMPER;
        }
        if (noActionSeq >= 1) {
            return PlayerStyle.LOOSE;
        }
        return PlayerStyle.SUPER_LOOSE;
    }

    private static PlayerStyle analyseBlindsCount(int blindsCount) {
        if (blindsCount >= 40) {
            return PlayerStyle.SUPER_TIGHT;
        }
        if (blindsCount >= MIN_BLINDS) {
            return PlayerStyle.TIGHT;
        }
        if (blindsCount >= 10) {
            return PlayerStyle.LIMPER;
        }
        if (blindsCount >= 5) {
            return PlayerStyle.LOOSE;
        }
        return PlayerStyle.SUPER_LOOSE;
    }

    private static PlayerStyle analyseStackOfPlayer(int stackOfPlayer, int avgStack) {
        if (stackOfPlayer >= avgStack) {
            return PlayerStyle.TIGHT;
        } else {
            return PlayerStyle.LOOSE;
        }
    }

    public static PlayerStyle analyseAvgChen(Integer avgChenValue) {
        if (avgChenValue == null) {
            return PlayerStyle.NONE;
        } else {
            if (avgChenValue >= 10) return PlayerStyle.SUPER_TIGHT;
            if (avgChenValue >= 8) return PlayerStyle.TIGHT;
            if (avgChenValue >= 5) return PlayerStyle.LIMPER;
            return PlayerStyle.LOOSE;
        }
    }
}
