package com.poker.reader.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "hand_consolidation")
@IdClass(HandPositionId.class)
public class HandConsolidation implements Serializable {

    @NotNull
    private Long tournamentId;

    @NotNull
    private Integer tableId;

    private String board;

    @Id
    private Long hand;

    private Integer numberOfPlayers;

    @NotNull
    @Size(max = 20)
    private String level;

    @NotNull
    private Integer smallBlind;

    @NotNull
    private Integer bigBlind;

    @NotNull
    private Integer totalPot;

    @NotNull
    @Size(max = 255)
    private String nickname;

    @Id
    private Integer position;

    @Size(max = 20)
    private String place;

    @Size(max = 5)
    private String cardsDescription;

    @Size(max = 1)
    private String card1;

    @Size(max = 1)
    private String card2;

    private Integer chen;

    @Size(max = 3)
    private String normalised;

    private Boolean pair;

    private Boolean suited;

    private Integer stackOfPlayer;

    private String foldRound;

    private Boolean noBet;

    private String loseHandDescription;

    private String winHandDescription;

    private Integer winPot;

    private Boolean winShowdown;

    @NotNull
    private LocalDateTime playedAt;
}
