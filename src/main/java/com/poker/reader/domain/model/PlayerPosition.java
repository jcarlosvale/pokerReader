package com.poker.reader.domain.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
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
    private Long handId;

    @Id
    private Integer position;

    @NotNull
    private String nickname;

    @NotNull
    private Integer stack;
}
