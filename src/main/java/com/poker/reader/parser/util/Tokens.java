package com.poker.reader.parser.util;

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

    public static final String START_STACK = "(";
    public static final String END_STACK = " in chips)";

    public static final String START_CARD = " [";
    public static final String END_CARD = "]";

    public static final String START_TURN = "] [";
    public static final String END_TURN = "]";

    public static final String START_RIVER = "] [";
    public static final String END_RIVER = "]";

    public static final String START_UNCALLED_BET = "Uncalled bet (";
    public static final String END_UNCALLED_BET = ") ";

    public static final String START_COLLECTED_FROM_POT = "collected ";
    public static final String END_COLLECTED_FROM_POT = " from pot";

    public static final String START_TOTAL_POT = "Total pot ";
    public static final String END_TOTAL_POT = " |";

    //Keywords
    public static final String DEALT_TO = "Dealt to ";
    public static final String RETURNED_TO = "returned to ";

    //ACTIONS
    public static final String ANTE = ": posts the ante ";
    public static final String SMALL_BLIND = ": posts small blind ";
    public static final String BIG_BLIND = ": posts big blind ";
    public static final String FOLD = ": folds ";
    public static final String CALL = ": calls ";
    public static final String CHECK = ": checks ";
    public static final String BETS = ": bets ";
    public static final String RAISE = ": raises ";
    public static final String ALL_IN = " and is all-in";
    public static final String NO_SHOW_HAND = " doesn't show hand";
}
