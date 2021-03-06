package com.poker.reader.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Tournament {

    public static final String PREFIX_TABLE = "tournament_";

    @Id
    @Column(name = PREFIX_TABLE + "id")
    private Long id;

    @Column(name = PREFIX_TABLE + "buyin")
    private BigDecimal buyIn;

    @OneToMany(mappedBy = "tournament", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<Hand> hands;
}
