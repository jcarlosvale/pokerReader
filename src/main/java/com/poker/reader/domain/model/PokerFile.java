package com.poker.reader.domain.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
@Table(name = "pokerfile")
public class PokerFile implements Serializable {

    @Id
    @GeneratedValue
    private Long pokerFileId;

    @NotNull
    @Size(max = 255)
    private String fileName;

    @NotNull
    private Boolean isProcessed;

    @NotNull
    private LocalDateTime createdAt;

}
