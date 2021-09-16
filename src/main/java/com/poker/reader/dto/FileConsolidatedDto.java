package com.poker.reader.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileConsolidatedDto {
    private int totalHands;
    private int totalPlayers;
    private int totalTournaments;
    private Set<String> tournaments;
    private Set<String> players;
    private List<AnalysedPlayer> analysedPlayers;

    @JsonCreator
    public FileConsolidatedDto(
            @JsonProperty("totalHands") int totalHands,
            @JsonProperty("totalPlayers") int totalPlayers,
            @JsonProperty("totalTournaments") int totalTournaments,
            @JsonProperty("tournaments") Set<String> tournaments,
            @JsonProperty("players") Set<String> players,
            @JsonProperty("analysedPlayers") List<AnalysedPlayer> analysedPlayers) {
        this.totalHands = totalHands;
        this.totalPlayers = totalPlayers;
        this.totalTournaments = totalTournaments;
        this.tournaments = tournaments;
        this.players = players;
        this.analysedPlayers = analysedPlayers;
    }
}
