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

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cards")
public class Cards implements Serializable {

    @Id
    @Size(max = 3)
    private String description;

    @Size(max = 1)
    @NotNull
    private String card1;

    @Size(max = 1)
    @NotNull
    private String card2;

    @NotNull
    private Boolean suited;

    @NotNull
    private Boolean pair;

    @NotNull
    private LocalDateTime createdAt;
}
