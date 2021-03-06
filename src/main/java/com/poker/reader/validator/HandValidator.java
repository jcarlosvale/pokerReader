package com.poker.reader.validator;

import com.poker.reader.dto.*;
import com.poker.reader.exception.InvalidCardException;
import com.poker.reader.exception.InvalidPlayerException;
import com.poker.reader.exception.InvalidSeatException;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.Set;

@Log4j2
public class HandValidator {
    public static boolean validate(HandDTO handDTO) {
        validateTournament(handDTO);
        validateCards(handDTO);
        validatePlayers(handDTO);
        validatePositions(handDTO);
        return true;
    }

    private static void validatePositions(HandDTO handDTO) {
        log.debug("Validating positions, HAND: {}", handDTO.getId());
        for(SeatDTO seatDTO : handDTO.getSeats().values()) {
            for(InfoPlayerAtHand infoPlayerAtHand : seatDTO.getInfoPlayerAtHandList()) {
                if (infoPlayerAtHand.getInfo().equals(TypeInfo.BUTTON)) {
                    if (!seatDTO.getSeatId().equals(handDTO.getButton())) {
                        log.error("Invalid BUTTON seat: {}, HAND: {}", seatDTO.getSeatId(), handDTO.getId());
                        throw new InvalidSeatException("Invalid BUTTON seat");
                    }
                }

                if (infoPlayerAtHand.getInfo().equals(TypeInfo.SMALL_BLIND)) {
                    for(Action action : handDTO.getActions()) {
                        if (action.getTypeAction().equals(TypeAction.SMALL_BLIND)) {
                            if (!seatDTO.getPlayerDTO().equals(action.getPlayerDTO())) {
                                log.error("Invalid SMALL_BLIND seat: {}, HAND: {}", seatDTO.getSeatId(), handDTO.getId());
                                throw new InvalidSeatException("Invalid SMALL_BLIND seat");
                            }
                        }
                    }
                }

                if (infoPlayerAtHand.getInfo().equals(TypeInfo.BIG_BLIND)) {
                    for(Action action : handDTO.getActions()) {
                        if (action.getTypeAction().equals(TypeAction.BIG_BLIND)) {
                            if (!seatDTO.getPlayerDTO().equals(action.getPlayerDTO())) {
                                log.error("Invalid BIG_BLIND seat: {}, HAND: {}", seatDTO.getSeatId(), handDTO.getId());
                                throw new InvalidSeatException("Invalid BIG_BLIND seat");
                            }
                        }
                    }
                }
            }
        }
    }

    private static void validatePlayers(HandDTO handDTO) {
        log.debug("Validating players and seats, HAND: {}", handDTO.getId());
        for(Map.Entry<PlayerDTO, SeatDTO> entry : handDTO.getSeats().entrySet()) {
            if (!entry.getValue().getPlayerDTO().equals(entry.getKey())) {
                log.error("Error mapping Player: {} and Seat: {}, HAND: {}", entry.getKey().getNickname(),
                        entry.getValue().getPlayerDTO().getNickname(), handDTO.getId());
                throw new InvalidPlayerException("Error mapping Player and Seat.");
            }

            if(entry.getValue().getHoldCards() != null) {
                if (!entry.getValue().getHoldCards().getPlayerDTO().equals(entry.getKey())) {
                    log.error("Error mapping Player: {} and HoldCards: {}, HAND: {}", entry.getKey().getNickname(),
                            entry.getValue().getHoldCards().getPlayerDTO().getNickname(), handDTO.getId());
                    throw new InvalidPlayerException("Error mapping Player and HoldCard.");
                }
            }
        }

        log.debug("Validating players and actions.");
        Set<PlayerDTO> playerDTOS = handDTO.getSeats().keySet();
        for(Action action : handDTO.getActions()) {
            if(!playerDTOS.contains(action.getPlayerDTO())) {
                log.error("Error mapping Player: {} and Actions, HAND: {}", action.getPlayerDTO(), handDTO.getId());
                throw new InvalidPlayerException("Error mapping Player and Actions.");
            }
        }
    }

    private static void validateCards(HandDTO handDTO) {
        log.debug("Validating flop, HAND: {}", handDTO.getId());
        Flop flop = handDTO.getFlop();
        Board board = handDTO.getBoard();
        if (handDTO.getFlop() != null || flop != null){
            if (!(board.getCard1().equals(flop.getCard1()) &&
                    board.getCard2().equals(flop.getCard2()) &&
                    board.getCard3().equals(flop.getCard3()))) {
                log.error("Missing flop card in the BOARD or HAND: {}", handDTO.getId());
                throw new InvalidCardException("Missing flop card in the BOARD or HAND.");
            }
        }

        log.debug("Validating turn.");
        Turn turn = handDTO.getTurn();
        if (turn != null) {
            if (!board.getCard4().equals(turn.getCard())) {
                log.error("Missing turn card in the BOARD or HAND: {}", handDTO.getId());
                throw new InvalidCardException("Missing turn card in the BOARD or HAND.");
            }
        }

        log.debug("Validating river.");
        River river = handDTO.getRiver();
        if (river != null) {
            if (!board.getCard5().equals(river.getCard())) {
                log.error("Missing river card in the BOARD or HAND: {}", handDTO.getId());
                throw new InvalidCardException("Missing river card in the BOARD or HAND.");
            }
        }

        log.debug("Validating actions and info player card.");
        for(Action action : handDTO.getActions()) {
            if (action.getTypeAction().equals(TypeAction.SHOW_HAND)) {
                if ((action.getHoldCards() == null) ||
                    (action.getHoldCards() != handDTO.getSeats().get(action.getPlayerDTO()).getHoldCards())) {
                    log.error("Invalid HOLDCARDS between ACTION: {} and SEAT: {}", action, handDTO.getSeats().get(action.getPlayerDTO()));
                    throw new InvalidCardException("Invalid HOLDCARDS between ACTION and SEAT.");
                }
            }
        }
    }

    private static void validateTournament(HandDTO handDTO) {
        log.debug("Validating Tournament, HAND: {}", handDTO.getId());
        if(null == handDTO.getTournamentDTO()) {
            log.error("Tournament is null.");
            throw new NullPointerException("Tournament is null.");
        }
    }
}
