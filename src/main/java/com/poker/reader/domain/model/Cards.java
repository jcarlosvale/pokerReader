package com.poker.reader.domain.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "cards")
public class Cards implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(max = 255)
    public String player;

    @Size(max = 3)
    @NotNull
    private String description;

    @NotNull
    private String rawCards;

    @Size(max = 1)
    @NotNull
    private String card1;

    @Size(max = 1)
    @NotNull
    private String card2;

    @NotNull
    private boolean suited;

    @NotNull
    private boolean pair;

    @NotNull
    private long counter;

    @NotNull
    private long chenValue;

    @NotNull
    private LocalDateTime createdAt;

    public Cards(String player, String description, String rawCards, String card1, String card2,
            boolean suited, boolean pair,
            long counter, long chenValue, LocalDateTime createdAt) {
        this.player = player;
        this.description = description;
        this.rawCards = rawCards;
        this.card1 = card1;
        this.card2 = card2;
        this.suited = suited;
        this.pair = pair;
        this.counter = counter;
        this.chenValue = chenValue;
        this.createdAt = createdAt;
    }
}
