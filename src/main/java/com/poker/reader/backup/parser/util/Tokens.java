package com.poker.reader.backup.parser.util;

import java.util.HashSet;
import java.util.Set;

public class Tokens {
    private Tokens() {}

    public static final String START_HAND = "PokerStars Hand #";
    public static final String END_HAND = ": Tournament";

    public static final String START_TOURNAMENT = "Tournament #";
    public static final String END_TOURNAMENT = ",";

    public static final String START_BUY_IN_PRIZE = " $";
    public static final String END_BUY_IN_PRIZE = "+$";

    public static final String START_BUY_IN_RAKE = "+$";
    public static final String END_BUY_IN_RAKE = " USD Hold'em No Limit";


    public static final String START_LEVEL = "- Level ";
    public static final String END_LEVEL = " (";

    public static final String START_SMALL_BLIND = " (";
    public static final String END_SMALL_BLIND = "/";

    public static final String START_BIG_BLIND = "/";
    public static final String END_BIG_BLIND = ")";

    public static final String START_DATE = "[";
    public static final String END_DATE = "]";

    public static final String START_TABLE = "Table '";
    public static final String END_TABLE = "' ";

    public static final String START_BUTTON = "#";
    public static final String END_BUTTON = " is the button";

    public static final String START_SEAT_POSITION = "Seat ";
    public static final String END_SEAT_POSITION = ": ";

    public static final String START_PLAYER = ": ";
    public static final String END_PLAYER = " (";

    public static final String START_STACK = " (";
    public static final String END_STACK = " in chips)";

    public static final String START_CARD = " [";
    public static final String END_CARD = "]";

    public static final String START_TURN = "] [";
    public static final String END_TURN = "]";

    public static final String START_RIVER = "] [";
    public static final String END_RIVER = "]";

    public static final String START_COLLECTED_FROM_POT = "collected (";
    public static final String END_COLLECTED_FROM_POT = ")";

    public static final String START_TOTAL_POT = "Total pot ";
    public static final String END_TOTAL_POT = " .";

    public static final String START_SIDE_POT = "Side pot ";
    public static final String END_SIDE_POT = " .";

    public static final String START_BOARD = "BoardOfHand [";
    public static final String END_BOARD = "]";

    public static final String START_SCORING = "] (";
    public static final String END_SCORING = ")";

    public static final String START_WON_POT = "won (";
    public static final String END_WON_POT = ")";

    //Keywords
    public static final String DEALT_TO = "Dealt to ";

    //ACTIONS
    public static final String ANTE_ACTION = ": posts the ante ";
    public static final String SMALL_BLIND_ACTION = ": posts small blind ";
    public static final String BIG_BLIND_ACTION = ": posts big blind ";
    public static final String FOLD_ACTION = ": folds";
    public static final String CALL_ACTION = ": calls ";
    public static final String CHECK_ACTION = ": checks";
    public static final String BETS_ACTION = ": bets ";
    public static final String RAISE_ACTION = ": raises ";
    public static final String ALL_IN_ACTION = " and is all-in";
    public static final String NO_SHOW_HAND_ACTION = " doesn't show hand";
    public static final String SHOW_HAND_ACTION = " shows [";
    public static final String MUCKS_HAND_ACTION = "mucks hand";

    //TYPE INFO
    public static final String START_COLLECTED_FROM_POT_INFO    = START_COLLECTED_FROM_POT;
    public static final String FOLDED_BEFORE_FLOP_TYPE_INFO     = " folded before Flop";
    public static final String FOLDED_ON_THE_RIVER_TYPE_INFO    = " folded on the River";
    public static final String BUTTON_TYPE_INFO                 = "(button)";
    public static final String BIG_BLIND_TYPE_INFO              = "(big blind)";
    public static final String SMALL_BLIND_TYPE_INFO            = "(small blind)";
    public static final String FINISHED_THE_TOURNAMENT_INFO     = "finished the tournament";
    public static final String SHOWED_HAND_INFO                 = "showed [";
    public static final String WON_INFO                         = "won (";
    public static final String LOST_INFO                         = "lost with ";

    //FILE SECTION
    public static final String SECTION_CHAT_MESSAGE = " said, \"";
    public static final String SECTION_TOKEN = "*** ";
    public static final String SECTION_HEADER = START_HAND;
    public static final String SECTION_PRE_FLOP = "*** HOLE CARDS ***";
    public static final String SECTION_FLOP = "*** FLOP ***";
    public static final String SECTION_TURN = "*** TURN ***";
    public static final String SECTION_RIVER = "*** RIVER ***";
    public static final String SECTION_SHOWDOWN = "*** SHOW DOWN ***";
    public static final String SECTION_SUMMARY = "*** SUMMARY ***";
    public static final String SECTION_END_OF_HAND = "";

    //Util Sets
    public static final Set<String> SEAT_TOKENS_SET = new HashSet<>();
    static {
        for (int i = 0; i < 15; i++) {
            SEAT_TOKENS_SET.add("Seat " + i + ":");
        }
    }
}
