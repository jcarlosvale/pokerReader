package com.poker.reader.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class Summary {
    private Integer seatId;
    private Long value;
    private Set<TypeInfo> additionalInfoPlayerSet;
}
