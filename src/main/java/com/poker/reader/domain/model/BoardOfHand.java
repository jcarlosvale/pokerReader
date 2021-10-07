package com.poker.reader.domain.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "board_of_hand")
public class BoardOfHand implements Serializable {

    @Id
    private Long handId;

    @NotNull
    private String board;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name="handId")
    private Hand hand;
}