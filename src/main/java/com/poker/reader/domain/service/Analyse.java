package com.poker.reader.domain.service;

import com.poker.reader.domain.util.CardUtil;
import com.poker.reader.view.rs.dto.PlayerMonitoredDto;
import com.poker.reader.view.rs.dto.RecommendationDto;
import java.util.Objects;

public class Analyse {

    public static final int MIN_BLINDS = 18;

    public static RecommendationDto analyseStack(long stackFromHero, Integer avgStack, int blinds) {
        String recommendation;
        String css;
        if(blinds <= 10) {
            recommendation = "ALL IN";
            css = PlayerStyle.LIMPER.getCss();
        }
        else if ((stackFromHero < avgStack) && (blinds <= MIN_BLINDS)){
            recommendation = "ALL IN < " + MIN_BLINDS;
            css = PlayerStyle.LIMPER.getCss();
        }
        else if (stackFromHero > avgStack) {
            recommendation = "ABOVE AVG STACK";
            css = PlayerStyle.TIGHT.getCss();
        }
        else {
            recommendation = "PLAY!";
            css = PlayerStyle.TIGHT.getCss();
        }
        return RecommendationDto.builder().recommendation(recommendation).css(css).build();
    }

    public static void analyseCssPlayer(PlayerMonitoredDto playerMonitoredDto, int avgStack) {

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
                playerStyleBlindsCount,
                playerStyleNoActionPerc
        );
        playerStyleNickname = playerStyleNickname.equals(PlayerStyle.NONE) ? playerStyleAvgChen : playerStyleNickname;

        playerMonitoredDto.setCssNickname(playerStyleNickname.getCss());
        playerMonitoredDto.setCssAvgChen(playerStyleAvgChen.getCss());
        playerMonitoredDto.setCssShowdowns(playerStyleShowdowns.getCss());
        playerMonitoredDto.setCssShowdownPerc(playerStyleShowdownPerc.getCss());
        playerMonitoredDto.setCssTotalHands(playerStyleTotalHands.getCss());
        playerMonitoredDto.setCssCards(PlayerStyle.NONE.getCss());
        playerMonitoredDto.setCssPosition(PlayerStyle.NONE.getCss());
        playerMonitoredDto.setCssSbCount(PlayerStyle.NONE.getCss());
        playerMonitoredDto.setCssBbCount(PlayerStyle.NONE.getCss());
        playerMonitoredDto.setCssBtnCount(PlayerStyle.NONE.getCss());
        playerMonitoredDto.setCssChen(PlayerStyle.NONE.getCss());
        playerMonitoredDto.setCssHandDescription(PlayerStyle.NONE.getCss());
        playerMonitoredDto.setCssCardsOnHand(PlayerStyle.NONE.getCss());
        playerMonitoredDto.setCssPlace(PlayerStyle.NONE.getCss());
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
        playerMonitoredDto.setCssActionBTNCount(PlayerStyle.NONE.getCss());
        playerMonitoredDto.setCssFoldBBCount(PlayerStyle.NONE.getCss());
        playerMonitoredDto.setCssFoldSBCount(PlayerStyle.NONE.getCss());
        playerMonitoredDto.setCssNoActionCount(PlayerStyle.NONE.getCss());
    }

    private static PlayerStyle analyse(PlayerStyle ... playerStyles) {
        if (playerStyles.length == 0) {
            return PlayerStyle.NONE;
        } else {
            boolean containsSuperLoose = false;
            boolean containsLoose = false;

            for(PlayerStyle playerStyle: playerStyles) {
                if (playerStyle.equals(PlayerStyle.SUPER_LOOSE)) containsSuperLoose = true;
                if (playerStyle.equals(PlayerStyle.LOOSE)) containsLoose = true;
            }
            return containsSuperLoose ? PlayerStyle.SUPER_LOOSE : containsLoose ? PlayerStyle.LOOSE : PlayerStyle.NONE;
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

    public static void analyseTitlePlayer(PlayerMonitoredDto playerMonitoredDto, int avgStack) {

        String titleNickname = perc("showdowns", playerMonitoredDto.getShowdowns(), playerMonitoredDto.getTotalHands());
        String titleAvgChen = "title Avg Chen";
        String titleShowdowns = "title Showdowns";
        String titleShowdownPerc = "title Showdown Perc";
        String titleTotalHands = "title Total Hands";
        String titleCards = "title Cards";
        String titlePosition = "title Position";
        String titleSbCount = "title SbCount";
        String titleBbCount = "title BbCount";
        String titleBtnCount = "title Btn Count";
        String titleChen = CardUtil.sort(playerMonitoredDto.getCards());
        String titleHandDescription = "title Hand Description";
        String titleCardsOnHand = "title Cards On Hand";
        String titlePlace = "title Place";
        String titleStackOfPlayer = "title Stack Of Player";
        String titleBlindsCount = "title Blinds Count";
        String titleNoActionSeq = getTitleNoActionSeq(playerMonitoredDto);
        String titleNoActionPerc = "title No Action Perc";
        String titleFoldSBSeq = "title Fold SB Seq";
        String titleFoldSBPerc = getTitleFoldSBPerc(playerMonitoredDto);
        String titleFoldBBSeq = "title Fold BB Seq";
        String titleFoldBBPerc = getTitleFoldBBPerc(playerMonitoredDto);
        String titleActionBTNSeq = "title Action BTN Seq";
        String titleActionBTNPerc = getTitleActionBTNPerc(playerMonitoredDto);
        String titleActionBTNCount = "title Action BTN Count";
        String titleFoldBBCount = "title Fold BB Count";
        String titleFoldSBCount = "title Fold SB Count";
        String titleNoActionCount = "title No Action Count";

        playerMonitoredDto.setTitleNickname(titleNickname);
        playerMonitoredDto.setTitleAvgChen(titleAvgChen);
        playerMonitoredDto.setTitleShowdowns(titleShowdowns);
        playerMonitoredDto.setTitleShowdownPerc(titleShowdownPerc);
        playerMonitoredDto.setTitleTotalHands(titleTotalHands);
        playerMonitoredDto.setTitleCards(titleCards);
        playerMonitoredDto.setTitlePosition(titlePosition);
        playerMonitoredDto.setTitleSbCount(titleSbCount);
        playerMonitoredDto.setTitleBbCount(titleBbCount);
        playerMonitoredDto.setTitleBtnCount(titleBtnCount);
        playerMonitoredDto.setTitleChen(titleChen);
        playerMonitoredDto.setTitleHandDescription(titleHandDescription);
        playerMonitoredDto.setTitleCardsOnHand(titleCardsOnHand);
        playerMonitoredDto.setTitlePlace(titlePlace);
        playerMonitoredDto.setTitleStackOfPlayer(titleStackOfPlayer);
        playerMonitoredDto.setTitleBlindsCount(titleBlindsCount);
        playerMonitoredDto.setTitleNoActionSeq(titleNoActionSeq);
        playerMonitoredDto.setTitleNoActionPerc(titleNoActionPerc);
        playerMonitoredDto.setTitleFoldSBSeq(titleFoldSBSeq);
        playerMonitoredDto.setTitleFoldSBPerc(titleFoldSBPerc);
        playerMonitoredDto.setTitleFoldBBSeq(titleFoldBBSeq);
        playerMonitoredDto.setTitleFoldBBPerc(titleFoldBBPerc);
        playerMonitoredDto.setTitleActionBTNSeq(titleActionBTNSeq);
        playerMonitoredDto.setTitleActionBTNPerc(titleActionBTNPerc);
        playerMonitoredDto.setTitleActionBTNCount(titleActionBTNCount);
        playerMonitoredDto.setTitleFoldBBCount(titleFoldBBCount);
        playerMonitoredDto.setTitleFoldSBCount(titleFoldSBCount);
        playerMonitoredDto.setTitleNoActionCount(titleNoActionCount);

    }

    private static String getTitleActionBTNPerc(PlayerMonitoredDto playerMonitoredDto) {
        return
                "seq: " + playerMonitoredDto.getActionBTNSeq() +"\n" +
                        perc("actionBTN", playerMonitoredDto.getActionBTNCount(), playerMonitoredDto.getBtnCount());

    }

    private static String getTitleFoldBBPerc(PlayerMonitoredDto playerMonitoredDto) {
        return
                "seq: " + playerMonitoredDto.getFoldBBSeq() +"\n" +
                        perc("foldBB", playerMonitoredDto.getFoldBBCount(), playerMonitoredDto.getBbCount());

    }

    private static String getTitleFoldSBPerc(PlayerMonitoredDto playerMonitoredDto) {
        return
                "seq: " + playerMonitoredDto.getFoldSBSeq() +"\n" +
                        perc("foldSB", playerMonitoredDto.getFoldSBCount(), playerMonitoredDto.getSbCount());
    }

    private static String getTitleNoActionSeq(PlayerMonitoredDto playerMonitoredDto) {
        return
                "seq: " + playerMonitoredDto.getNoActionSeq() +"\n"
                        + perc("noAction", playerMonitoredDto.getNoActionCount(), playerMonitoredDto.getTotalHands());
    }

    private static String perc(String title, Integer count, Integer size) {
        if (Objects.isNull(size) || size == 0) return "no data";
        return title +": " + count + "/" + size + " " + (100 * count / size) + "%";
    }
}
