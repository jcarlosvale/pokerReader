package com.poker.reader.domain.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "hand_position")
public class HandPosition implements Serializable {

    @Id
    @Column(name = "hand")
    private Long hand;

    @NotNull
    @Column(name = "number_of_players")
    int numberOfPlayers;

    @NotNull
    @Column(name = "min_pos")
    int minPos;

    @NotNull
    @Column(name = "max_pos")
    int maxPos;

    @NotNull
    @Column(name = "button")
    int button;

    @NotNull
    @Column(name = "positions")
    String positions;
}
