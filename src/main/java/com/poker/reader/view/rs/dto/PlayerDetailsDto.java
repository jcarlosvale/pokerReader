package com.poker.reader.view.rs.dto;

import com.poker.reader.domain.repository.projection.PlayerDetailsDtoProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerDetailsDto {

    private PlayerDetailsDtoProjection playerDetailsDtoProjection;
    private String pokerTablePosition;
    private String cssChen;
    private String cssNickname;
}
