package com.poker.reader.domain.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "players")
public class Player implements Serializable {

    @Id
    @Size(max = 255)
    private String nickname;

    @NotNull
    private Integer totalHands;

    @NotNull
    private Integer showdowns;

    @NotNull
    private Long avgChen;

    @NotNull
    private Long sumChen;

    @NotNull
    private LocalDate playedAt;

    @NotNull
    private LocalDateTime createdAt;
}
