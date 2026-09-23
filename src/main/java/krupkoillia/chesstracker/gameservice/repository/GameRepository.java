package krupkoillia.chesstracker.gameservice.repository;

import java.util.Optional;
import krupkoillia.chesstracker.gameservice.model.GameEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<GameEntity, Long> {

    @EntityGraph(attributePaths = "moves")
    Page<GameEntity> findAllByUserId(Long userId, Pageable pageable);

    boolean existsByIdAndUserId(Long id, Long userId);

    Optional<GameEntity> findByIdAndUserId(Long id, Long userId);

    void deleteByIdAndUserId(Long id, Long userId);

}
