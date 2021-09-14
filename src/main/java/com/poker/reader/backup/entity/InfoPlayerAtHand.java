package com.poker.reader.backup.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InfoPlayerAtHand {
    private TypeInfo info;
    private Long value;
}
