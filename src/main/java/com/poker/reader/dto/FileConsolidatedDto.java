package com.poker.reader.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileConsolidatedDto {
    private Set<String> tournaments;
    private int totalHands;
    private Set<String> players;
    private List<AnalysedPlayer> analysedPlayers;

    public int getTotalPlayers() {
        return players.size();
    }

    public int getTotalTournaments() {
        return tournaments.size();
    }

    @JsonCreator
    public FileConsolidatedDto(
            @JsonProperty("tournaments") Set<String> tournaments,
            @JsonProperty("totalHands") int totalHands,
            @JsonProperty("players") Set<String> players,
            @JsonProperty("analysedPlayers") List<AnalysedPlayer> analysedPlayers) {
        this.tournaments = tournaments;
        this.totalHands = totalHands;
        this.players = players;
        this.analysedPlayers = analysedPlayers;
    }
}
