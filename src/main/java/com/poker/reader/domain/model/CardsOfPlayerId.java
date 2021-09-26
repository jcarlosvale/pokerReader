package com.poker.reader.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardsOfPlayerId implements Serializable {
    private Long handId;
    private String cards;
    private Integer position;
}
