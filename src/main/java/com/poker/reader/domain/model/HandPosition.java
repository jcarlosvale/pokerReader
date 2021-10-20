package com.poker.reader.domain.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(name = "hand_position")
public class HandPosition implements Serializable {

    @Id
    private Long hand;

    @NotNull
    int numberOfPlayers;

    @NotNull
    int minPos;

    @NotNull
    int maxPos;

    @NotNull
    int button;

    @NotNull
    String positions;
}
