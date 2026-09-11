package krupkoillia.chesstracker.gameservice.repository;

import krupkoillia.chesstracker.gameservice.model.Move;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoveRepository extends JpaRepository<Move, Long> {

}
