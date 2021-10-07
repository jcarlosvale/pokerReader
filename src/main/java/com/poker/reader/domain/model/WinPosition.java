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
@Table(name = "win_position")
@IdClass(HandPositionId.class)
public class WinPosition {
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
    private boolean showdown;

    @NotNull
    private int pot;

    private String handDescription;
}
