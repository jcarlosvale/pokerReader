package com.poker.reader.domain.model;

import java.io.Serializable;
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
@Table(name = "tournaments")
public class Tournament implements Serializable {

    @Id
    @Size(max = 20)
    private String tournamentId;

    @NotNull
    @Size(max = 255)
    private String fileName;

    @NotNull
    private LocalDateTime createdAt;
}
