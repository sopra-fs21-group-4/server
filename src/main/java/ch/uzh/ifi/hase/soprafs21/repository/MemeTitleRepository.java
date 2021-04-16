package ch.uzh.ifi.hase.soprafs21.repository;

import ch.uzh.ifi.hase.soprafs21.entity.MemeTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("memeTitleRepository")
public interface MemeTitleRepository extends JpaRepository<MemeTitle, Long> {


}
