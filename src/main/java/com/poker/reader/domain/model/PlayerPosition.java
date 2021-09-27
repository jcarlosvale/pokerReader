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

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "player_position")
@IdClass(PlayerPositionId.class)
public class PlayerPosition {
    @Id
    private Long handId;

    @Id
    private Integer position;

    @NotNull
    private String nickname;

    @NotNull
    private Integer stack;
}
