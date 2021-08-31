package com.poker.reader.parser;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

public class FileProcessor {

    public Set<String> loadPlayers(List<String> lines) {
        if (CollectionUtils.isEmpty(lines)) {
            return Set.of();
        } else {
            return lines
                    .stream()
                    .filter(line -> line.startsWith("Seat ") && line.endsWith(" in chips)"))
                    .map(seatLine -> StringUtils.substringBetween(seatLine, ": ", " (").trim())
                    .collect(Collectors.toSet());
        }
    }
}
/*
if (line.contains(START_SEAT_POSITION)) {
                Seat seat = extractSeat(line);
                hand.getSeats().put(seat.getPlayer(),seat);
                players.add(seat.getPlayer());
            }
 */