package com.poker.reader.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

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
}
