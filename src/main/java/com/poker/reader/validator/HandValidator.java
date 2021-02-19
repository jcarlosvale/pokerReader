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
        log.debug("Validating positions, HAND: {}", hand.getId());
        for(Seat seat : hand.getSeats().values()) {
            for(InfoPlayerAtHand infoPlayerAtHand : seat.getInfoPlayerAtHandList()) {
                if (infoPlayerAtHand.getInfo().equals(TypeInfo.BUTTON)) {
                    if (!seat.getSeatId().equals(hand.getButton())) {
                        log.error("Invalid BUTTON seat: {}, HAND: {}", seat.getSeatId(), hand.getId());
                        throw new InvalidSeatException("Invalid BUTTON seat");
                    }
                }

                if (infoPlayerAtHand.getInfo().equals(TypeInfo.SMALL_BLIND)) {
                    for(Action action : hand.getActions()) {
                        if (action.getTypeAction().equals(TypeAction.SMALL_BLIND)) {
                            if (!seat.getPlayer().equals(action.getPlayer())) {
                                log.error("Invalid SMALL_BLIND seat: {}, HAND: {}", seat.getSeatId(), hand.getId());
                                throw new InvalidSeatException("Invalid SMALL_BLIND seat");
                            }
                        }
                    }
                }

                if (infoPlayerAtHand.getInfo().equals(TypeInfo.BIG_BLIND)) {
                    for(Action action : hand.getActions()) {
                        if (action.getTypeAction().equals(TypeAction.BIG_BLIND)) {
                            if (!seat.getPlayer().equals(action.getPlayer())) {
                                log.error("Invalid BIG_BLIND seat: {}, HAND: {}", seat.getSeatId(), hand.getId());
                                throw new InvalidSeatException("Invalid BIG_BLIND seat");
                            }
                        }
                    }
                }
            }
        }
    }

    private static void validatePlayers(Hand hand) {
        log.debug("Validating players and seats, HAND: {}", hand.getId());
        for(Map.Entry<Player, Seat> entry : hand.getSeats().entrySet()) {
            if (!entry.getValue().getPlayer().equals(entry.getKey())) {
                log.error("Error mapping Player: {} and Seat: {}, HAND: {}", entry.getKey().getNickname(),
                        entry.getValue().getPlayer().getNickname(), hand.getId());
                throw new InvalidPlayerException("Error mapping Player and Seat.");
            }

            if(entry.getValue().getHoldCards() != null) {
                if (!entry.getValue().getHoldCards().getPlayer().equals(entry.getKey())) {
                    log.error("Error mapping Player: {} and HoldCards: {}, HAND: {}", entry.getKey().getNickname(),
                            entry.getValue().getHoldCards().getPlayer().getNickname(), hand.getId());
                    throw new InvalidPlayerException("Error mapping Player and HoldCard.");
                }
            }
        }

        log.debug("Validating players and actions.");
        Set<Player> players = hand.getSeats().keySet();
        for(Action action : hand.getActions()) {
            if(!players.contains(action.getPlayer())) {
                log.error("Error mapping Player: {} and Actions, HAND: {}", action.getPlayer(), hand.getId());
                throw new InvalidPlayerException("Error mapping Player and Actions.");
            }
        }
    }

    private static void validateCards(Hand hand) {
        log.debug("Validating flop, HAND: {}", hand.getId());
        Flop flop = hand.getFlop();
        Board board = hand.getBoard();
        if (hand.getFlop() != null || flop != null){
            if (!(board.getCard1().equals(flop.getCard1()) &&
                    board.getCard2().equals(flop.getCard2()) &&
                    board.getCard3().equals(flop.getCard3()))) {
                log.error("Missing flop cards in the BOARD or HAND: {}", hand.getId());
                throw new InvalidCardException("Missing flop cards in the BOARD or HAND.");
            }
        }

        log.debug("Validating turn.");
        Turn turn = hand.getTurn();
        if (turn != null) {
            if (!board.getCard4().equals(turn.getCard())) {
                log.error("Missing turn card in the BOARD or HAND: {}", hand.getId());
                throw new InvalidCardException("Missing turn card in the BOARD or HAND.");
            }
        }

        log.debug("Validating river.");
        River river = hand.getRiver();
        if (river != null) {
            if (!board.getCard5().equals(river.getCard())) {
                log.error("Missing river card in the BOARD or HAND: {}", hand.getId());
                throw new InvalidCardException("Missing river card in the BOARD or HAND.");
            }
        }

        log.debug("Validating actions and info player cards.");
        for(Action action : hand.getActions()) {
            if (action.getTypeAction().equals(TypeAction.SHOW_HAND)) {
                if ((action.getHoldCards() == null) ||
                    (action.getHoldCards() != hand.getSeats().get(action.getPlayer()).getHoldCards())) {
                    log.error("Invalid HOLDCARDS between ACTION: {} and SEAT: {}", action, hand.getSeats().get(action.getPlayer()));
                    throw new InvalidCardException("Invalid HOLDCARDS between ACTION and SEAT.");
                }
            }
        }
    }

    private static void validateTournament(Hand hand) {
        log.debug("Validating Tournament, HAND: {}", hand.getId());
        if(null == hand.getTournament()) {
            log.error("Tournament is null.");
            throw new NullPointerException("Tournament is null.");
        }
    }
}
