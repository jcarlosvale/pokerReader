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
@Table(name = "table_position")
@IdClass(HandPositionId.class)
public class TablePosition implements Serializable {

    @Id
    private Long hand;

    @Id
    private Integer position;

    @NotNull
    @Size(max = 20)
    String pokerPosition;
}
