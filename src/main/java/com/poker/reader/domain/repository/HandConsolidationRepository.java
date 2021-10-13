package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.HandConsolidation;
import com.poker.reader.domain.model.HandPositionId;
import com.poker.reader.domain.repository.projection.PlayerDtoProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HandConsolidationRepository extends JpaRepository<HandConsolidation, HandPositionId> {
    String FIND_PLAYER_DTO =
            "select \n"
                    + "\thc.nickname as nickname,\n"
                    + "\tcount(hc.hand) as totalHands,\n"
                    + "\tsum(case when cards_description is null then 0 else 1 end) as showdowns,\n"
                    + "\tround(sum(case when cards_description is null then 0 else 1 end) * 100.0/ count(hc.hand)) as showdownStat,\n"
                    + "\tround(avg(hc.chen)) as avgChen,\n"
                    + "\tmin(hc.played_at) as createdAt,\n"
                    + "\tstring_agg(distinct hc.normalised , ', ') as cards,\n"
                    + "\tstring_agg(hc.cards_description, ', ') as rawcards,\n"
                    + "\t'd-none' as css\n"
                    + "from hand_consolidation hc\n"
                    + "where \n"
                    + "\thc.nickname in :playerList\n"
                    + "group by \n"
                    + "\thc.nickname";
    @Query(value = FIND_PLAYER_DTO, nativeQuery = true )
    List<PlayerDtoProjection> findAllPlayerDto(@Param("playerList") List<String> playerList);
}
