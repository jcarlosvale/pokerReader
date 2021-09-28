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

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "blind_position")
@IdClass(HandPositionId.class)
public class BlindPosition {
    @Id
    private Long handId;

    @Id
    private Integer position;

    @NotNull
    private String place;
}
