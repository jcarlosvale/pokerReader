package com.poker.reader.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "fold_position")
@IdClass(HandPositionId.class)
public class FoldPosition {
    @Id
    private Long hand;

    @Id
    private Integer position;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumns({
            @JoinColumn(name="hand"),
            @JoinColumn(name="position")
    })
    private PlayerPosition playerPosition;

    @NotNull
    private String round;

    @NotNull
    private Boolean bet;
}
