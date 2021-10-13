package com.poker.reader.domain.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.MapsId;
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
@Table(name = "stats")
@IdClass(HandPositionId.class)
public class Stats implements Serializable {

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

    //how many hands without playing, excluding BB, SB
    @NotNull
    private int noActionCount;

    @NotNull
    private int sbCount;

    @NotNull
    private int bbCount;

    @NotNull
    private int buttonCount;

    @NotNull
    private int latePositionCount;

    @NotNull
    private int middlePositionCount;

    @NotNull
    private int earlyPositionCount;
}
