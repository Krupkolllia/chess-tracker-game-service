package krupkoillia.chesstracker.gameservice.repository;

import krupkoillia.chesstracker.gameservice.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {

}
