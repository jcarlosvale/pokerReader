package com.poker.reader.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cards_of_player")
@IdClass(CardsOfPlayerId.class)
public class CardsOfPlayer {
    @Id
    private Long handId;
    @Id
    private String cards;
    @Id
    private Integer position;
}
