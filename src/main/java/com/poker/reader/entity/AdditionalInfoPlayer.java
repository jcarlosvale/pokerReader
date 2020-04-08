package com.poker.reader.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdditionalInfoPlayer {
    private TypeInfo info;
    private Long value;
    private Player player;
}
