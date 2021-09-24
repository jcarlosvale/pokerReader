package com.poker.reader.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;


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
/*
    @NotNull
    private LocalDateTime playedAt;
*/
    @NotNull
    private LocalDateTime createdAt;
}
