package com.poker.reader.domain.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "lose_position")
@IdClass(HandPositionId.class)
public class LosePosition {
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

    private String handDescription;
}
