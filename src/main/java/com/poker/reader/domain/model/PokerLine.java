package com.poker.reader.domain.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "pokerline")
@IdClass(PokerLineId.class)
public class PokerLine implements Serializable {

    @Id
    private Long pokerFileId;

    @Id
    private Long lineNumber;

    @NotNull
    @Size(max = 50)
    private String section;

    @NotNull
    @Size(max = 255)
    private String line;
}
