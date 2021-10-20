package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.HandPosition;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HandPositionRepository extends JpaRepository<HandPosition, Long> {

    String FIND_HAND_POSITION_NOT_PROCESSED =
            "select * \n"
                    + "from hand_position hp\n"
                    + "where hp.hand not in (select hand from table_position tp)";
    @Query(nativeQuery = true, value = FIND_HAND_POSITION_NOT_PROCESSED)
    List<HandPosition> findAllNotProcessed();
}
