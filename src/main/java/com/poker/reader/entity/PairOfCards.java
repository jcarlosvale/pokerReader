package com.poker.reader.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Card1 is always the highest card
 */
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PairOfCards {

    public static final String PREFIX_TABLE = "poc_";

    @Id
    @Column(name = PREFIX_TABLE + "id")
    String id;

    @Column(name = PREFIX_TABLE + "card1")
    Character card1;

    @Column(name = PREFIX_TABLE + "card2")
    Character card2;

    @Column(name = PREFIX_TABLE + "suited")
    Boolean isSuited;
}
