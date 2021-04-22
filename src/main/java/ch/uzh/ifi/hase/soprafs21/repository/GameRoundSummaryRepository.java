package ch.uzh.ifi.hase.soprafs21.repository;

import ch.uzh.ifi.hase.soprafs21.entity.GameRoundSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("gameRoundSummaryRepository")
public interface GameRoundSummaryRepository extends JpaRepository<GameRoundSummary, Long> {


}
