package com.poker.reader.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Seat {
    /*
    TODO: missing fields holdCards, infoPlayerAtHandList
     */
    public static final String PREFIX_TABLE = "seat_";

    @Id
    @GeneratedValue
    @Column(name = PREFIX_TABLE + "surrogateKey")
    private Long id;

    @Column(name = PREFIX_TABLE + "id")
    private Integer seatId;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = PREFIX_TABLE + "id_hand")
    private Hand hand;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = PREFIX_TABLE + "player")
    private Player player;

    @ManyToOne
    @JoinColumn(name = PREFIX_TABLE + "id_poc")
    private PairOfCards pairOfCards;

    @Column(name = PREFIX_TABLE + "card1")
    private String card1;

    @Column(name = PREFIX_TABLE + "card2")
    private String card2;

    @Column(name = PREFIX_TABLE + "stack")
    private Long stack;
}

