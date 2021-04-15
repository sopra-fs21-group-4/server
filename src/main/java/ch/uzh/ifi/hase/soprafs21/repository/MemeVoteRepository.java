package ch.uzh.ifi.hase.soprafs21.repository;

import ch.uzh.ifi.hase.soprafs21.entity.MemeVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("memeVoteRepository")
public interface MemeVoteRepository extends JpaRepository<MemeVote, Long> {


}
