package com.poker.reader.domain.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PokerLineId implements Serializable {

    private Long pokerFileId;
    private Long lineNumber;

}
