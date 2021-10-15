package com.poker.reader.view.rs.dto;

import static com.google.common.base.Preconditions.checkArgument;

import com.poker.reader.domain.repository.projection.StackDtoProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class PlayerMonitoredDto {

    private PlayerDto playerDto;
    private StackDtoProjection stackDtoProjection;

    public PlayerMonitoredDto(PlayerDto playerDto, StackDtoProjection stackDtoProjection) {
        checkArgument(playerDto.getNickname().equals(stackDtoProjection.getNickname()), "inconsistent monitored player");
        this.playerDto = playerDto;
        this.stackDtoProjection = stackDtoProjection;
    }
}
