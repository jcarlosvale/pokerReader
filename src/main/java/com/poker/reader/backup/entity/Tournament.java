package com.poker.reader.backup.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class Tournament {
    private Long id;
    private BigDecimal buyIn;
}
