package com.poker.reader.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

import static com.poker.reader.util.Util.faceValue;

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
    Long id;

    @Column(name = PREFIX_TABLE + "card1")
    Character card1;

    @Column(name = PREFIX_TABLE + "card2")
    Character card2;

    @Column(name = PREFIX_TABLE + "suited")
    Boolean isSuited;

    @OneToMany(mappedBy = "pairOfCards", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<Seat> seat;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PairOfCards pairOfCards = (PairOfCards) o;

        return (isSuited == pairOfCards.isSuited) &&
                ((faceValue(card1) == faceValue(pairOfCards.card1) && faceValue(card2) == faceValue(pairOfCards.card2)) ||
                (faceValue(card1) == faceValue(pairOfCards.card2) && faceValue(card2) == faceValue(pairOfCards.card1)));
    }

    @Override
    public int hashCode() {
        if (faceValue(card1) > faceValue(card2)) {
            return Objects.hash(isSuited, card1, card2);
        } else {
            return Objects.hash(isSuited, card2, card1);
        }
    }
}
