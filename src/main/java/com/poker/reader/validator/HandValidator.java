package com.poker.reader.validator;

import com.poker.reader.entity.*;
import com.poker.reader.exception.InvalidCardException;
import com.poker.reader.exception.InvalidPlayerException;
import com.poker.reader.exception.InvalidSeatException;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.Set;

@Log4j2
public class HandValidator {
    public static boolean validate(Hand hand) {
        validateTournament(hand);
        validateCards(hand);
        validatePlayers(hand);
        validatePositions(hand);
        return true;
    }

    private static void validatePositions(Hand hand) {
        log.info("Validating positions.");
        for(AdditionalInfoPlayer additionalInfoPlayer : hand.getAdditionalInfoPlayerList()){
            if (additionalInfoPlayer.getInfo().equals(TypeInfo.BUTTON)){
                Seat seat = hand.getSeats().get(additionalInfoPlayer.getPlayer());
                if (! seat.getSeatId().equals(hand.getButton())) {
                    log.error("Invalid BUTTON seat");
                    throw new InvalidSeatException("Invalid BUTTON seat");
                }
            }

            if (additionalInfoPlayer.getInfo().equals(TypeInfo.SMALL_BLIND)){
                Seat seat = hand.getSeats().get(additionalInfoPlayer.getPlayer());
                if (! seat.getSeatId().equals(hand.getSmallBlind())) {
                    log.error("Invalid SMALL_BLIND seat");
                    throw new InvalidSeatException("Invalid SMALL_BLIND seat");
                }
            }

            if (additionalInfoPlayer.getInfo().equals(TypeInfo.BIG_BLIND)){
                Seat seat = hand.getSeats().get(additionalInfoPlayer.getPlayer());
                if (! seat.getSeatId().equals(hand.getBigBlind())) {
                    log.error("Invalid BIG_BLIND seat");
                    throw new InvalidSeatException("Invalid BIG_BLIND seat");
                }
            }
        }
    }

    private static void validatePlayers(Hand hand) {
        log.info("Validating players and seats.");
        for(Map.Entry<Player, Seat> entry : hand.getSeats().entrySet()) {
            if (!entry.getValue().getPlayer().equals(entry.getKey())) {
                log.error("Error mapping Player and Seat.");
                throw new InvalidPlayerException("Error mapping Player and Seat.");
            }
        }

        log.info("Validating players and actions.");
        Set<Player> players = hand.getSeats().keySet();
        for(Action action : hand.getActions()) {
            if(!players.contains(action.getPlayer())) {
                log.error("Error mapping Player and Actions.");
                throw new InvalidPlayerException("Error mapping Player and Actions.");
            }
        }

        log.info("Validating players and additional information.");
        for(AdditionalInfoPlayer info : hand.getAdditionalInfoPlayerList()) {
            if(!players.contains(info.getPlayer())) {
                log.error("Error mapping Player and Additional Info.");
                throw new InvalidPlayerException("Error mapping Player and Additional Info.");
            }
        }
    }

    private static void validateCards(Hand hand) {
        log.info("Validating flop.");
        Flop flop = hand.getFlop();
        Board board = hand.getBoard();
        if (hand.getFlop() != null || flop != null){
            if (!(board.getCard1().equals(flop.getCard1()) &&
                    board.getCard2().equals(flop.getCard2()) &&
                    board.getCard3().equals(flop.getCard3()))) {
                log.error("Missing flop cards in the BOARD or HAND.");
                throw new InvalidCardException("Missing flop cards in the BOARD or HAND.");
            }
        }

        log.info("Validating turn.");
        Turn turn = hand.getTurn();
        if (turn != null) {
            if (!board.getCard4().equals(turn.getCard())) {
                log.error("Missing turn card in the BOARD or HAND.");
                throw new InvalidCardException("Missing turn card in the BOARD or HAND.");
            }
        }

        log.info("Validating river.");
        River river = hand.getRiver();
        if (river != null) {
            if (!board.getCard5().equals(river.getCard())) {
                log.error("Missing river card in the BOARD or HAND.");
                throw new InvalidCardException("Missing river card in the BOARD or HAND.");
            }
        }
    }

    private static void validateTournament(Hand hand) {
        log.info("Validating Tournament.");
        if(null == hand.getTournament()) {
            log.error("Tournament is null.");
            throw new NullPointerException("Tournament is null.");
        }
    }
}
