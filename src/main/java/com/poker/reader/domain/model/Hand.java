package com.poker.reader.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "hands")
public class Hand {

    @Id
    private Long handId;

    @NotNull
    private Integer tableId;

    @NotNull
    @Size(max = 20)
    private String level;

    @NotNull
    private Integer smallBlind;

    @NotNull
    private Integer bigBlind;

    @NotNull
    private LocalDateTime playedAt;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tournamentId")
    private Tournament tournament;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "hand")
    private PotOfHand potOfHand;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "hand")
    private BoardOfHand boardOfHand;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "hand")
    private List<PlayerPosition> playerPositions;
}
