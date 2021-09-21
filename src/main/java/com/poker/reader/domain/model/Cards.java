package com.poker.reader.domain.model;

import com.poker.reader.domain.util.CardUtil;
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
import lombok.NonNull;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cards")
public class Cards implements Serializable, Comparable<Cards> {

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

    public static Cards from(String description) {
        Cards cards = new Cards();
        cards.setDescription(description);
        cards.setCard1(String.valueOf(description.charAt(0)));
        cards.setCard2(String.valueOf(description.charAt(1)));
        cards.setSuited(description.length() > 2 && description.charAt(2) == 's');
        cards.setPair(cards.getCard1().equals(cards.getCard2()));
        cards.setCreatedAt(LocalDateTime.now());
        return cards;
    }

    @Override
    public int compareTo(@NonNull Cards other) {
        if (this.equals(other)) return 0;
        if (Boolean.TRUE.equals(this.pair)) {
            if (Boolean.TRUE.equals(other.pair)) {
                return CardUtil.valueOf(other.card1) - CardUtil.valueOf(this.card1);
            } else {
                return -1;
            }
        } else {
            if (Boolean.TRUE.equals(other.pair)) {
                return 1;
            } else {
                if(this.card1.equals(other.card1) && this.card2.equals(other.card2)) { //iguais
                    if (Boolean.TRUE.equals(this.suited)) return -1;
                    return 1;
                } else {
                    if (this.card1.equals(other.card1)) {
                        return CardUtil.valueOf(other.card2) - CardUtil.valueOf(this.card2);
                    } else {
                        return CardUtil.valueOf(other.card1) - CardUtil.valueOf(this.card1);
                    }
                }
            }
        }
    }
}
