package com.poker.reader.view.rs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentDto {

    private Long tournamentId;

    private String fileName;

    private String createdAt;

    private long hands;

    private int players;

    private int showdowns;
}
