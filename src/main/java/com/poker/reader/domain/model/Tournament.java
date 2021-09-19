package com.poker.reader.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "tournaments")
public class Tournament implements Serializable {

    @Id
    @Size(max = 20)
    private String tournamentId;

    @NotNull
    @Size(max = 255)
    private String fileName;

    @NotNull
    private LocalDate playedAt;

    @NotNull
    private LocalDateTime createdAt;
}
