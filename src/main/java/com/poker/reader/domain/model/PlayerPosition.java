package com.poker.reader.domain.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "player_position")
@IdClass(HandPositionId.class)
public class PlayerPosition implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handId")
    private Hand hand;

    @Id
    private Integer position;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nickname")
    private Player player;

    @NotNull
    private Integer stack;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "playerPosition")
    private CardsOfPlayer cardsOfPlayer;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "playerPosition")
    private WinPosition winPosition;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "playerPosition")
    private LosePosition losePosition;
}
