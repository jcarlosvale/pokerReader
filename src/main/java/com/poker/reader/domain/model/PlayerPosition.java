package com.poker.reader.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

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
    private String nickname;
    @Id
    private Integer position;
}
