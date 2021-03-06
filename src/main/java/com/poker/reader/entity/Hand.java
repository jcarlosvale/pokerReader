package com.poker.reader.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Hand {
    /*
    TODO: missing fields flop, turn, river, board, seats, actions
     */
    public static final String PREFIX_TABLE = "hand_";

    @Id
    @Column(name = PREFIX_TABLE + "id")
    private Long id;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = PREFIX_TABLE + "id_tournament")
    private Tournament tournament;

    @OneToMany(mappedBy = "hand", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<Seat> seats;

    @Column(name = PREFIX_TABLE + "level")
    private String level;

    @Column(name = PREFIX_TABLE + "button")
    private Integer button;

    @Column(name = PREFIX_TABLE + "smallblind")
    private Integer smallBlind;

    @Column(name = PREFIX_TABLE + "bigblind")
    private Integer bigBlind;

    @Column(name = PREFIX_TABLE + "date")
    private LocalDate date;

    @Column(name = PREFIX_TABLE + "tableid")
    private String tableId;

    @Column(name = PREFIX_TABLE + "sidepot")
    private Long sidePot;

    @Column(name = PREFIX_TABLE + "totalpot")
    private Long totalPot;
}
