package krupkoillia.chesstracker.gameservice.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import krupkoillia.chesstracker.gameservice.model.enums.AnalysisStatus;
import krupkoillia.chesstracker.gameservice.model.enums.Color;
import krupkoillia.chesstracker.gameservice.model.enums.Result;
import krupkoillia.chesstracker.gameservice.model.enums.TimeControl;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@SQLDelete(sql = "UPDATE games SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "games")
public class GameEntity {

    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String pgn;

    @Enumerated(EnumType.STRING)
    @Column(name = "time_control", nullable = false)
    private TimeControl timeControl;

    @Column(name = "white_name")
    private String whiteName;

    @Column(name = "black_name")
    private String blackName;

    @Column(name = "white_elo")
    private Integer whiteElo;

    @Column(name = "black_elo")
    private Integer blackElo;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_color", nullable = false)
    private Color userColor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Result result;

    @OneToMany(
            mappedBy = "game",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<MoveEntity> moves = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "analysis_status", nullable = false)
    private AnalysisStatus analysisStatus;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

}
