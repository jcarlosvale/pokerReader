package dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class TournamentDTO {
    private Long id;
    private BigDecimal buyIn;
}
