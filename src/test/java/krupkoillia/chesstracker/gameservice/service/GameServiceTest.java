package krupkoillia.chesstracker.gameservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import krupkoillia.chesstracker.gameservice.dto.GameResponseDto;
import krupkoillia.chesstracker.gameservice.dto.UploadGameRequestDto;
import krupkoillia.chesstracker.gameservice.exception.EntityNotFoundException;
import krupkoillia.chesstracker.gameservice.loader.GameLoader;
import krupkoillia.chesstracker.gameservice.mapper.GameMapper;
import krupkoillia.chesstracker.gameservice.model.GameEntity;
import krupkoillia.chesstracker.gameservice.model.enums.AnalysisStatus;
import krupkoillia.chesstracker.gameservice.model.enums.Color;
import krupkoillia.chesstracker.gameservice.repository.GameRepository;
import krupkoillia.chesstracker.gameservice.security.SecurityUtil;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    private static final Long MOCK_USER_ID = 42L;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameMapper gameMapper;

    @Mock
    private GameLoader gameLoader;

    private MockedStatic<SecurityUtil> securityUtilMock;

    @InjectMocks
    private GameService gameService;

    @BeforeEach
    void setUp() {
        securityUtilMock = mockStatic(SecurityUtil.class);
        securityUtilMock.when(SecurityUtil::getAuthenticatedUserId)
                .thenReturn(MOCK_USER_ID);

    }

    @AfterEach
    void tearDown() {
        securityUtilMock.close();
    }

    @Test
    @DisplayName("""
            findAll method should
            return all user's games
            """)
    void findAll_ShouldReturnAllUsersGames() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);

        GameEntity gameEntity1 = Instancio.of(GameEntity.class)
                .set(field(GameEntity::getUserId), MOCK_USER_ID)
                .create();

        GameEntity gameEntity2 = Instancio.of(GameEntity.class)
                .set(field(GameEntity::getUserId), MOCK_USER_ID)
                .create();

        GameResponseDto gameResponseDto1 = Instancio.of(GameResponseDto.class)
                .set(field(GameResponseDto::id), gameEntity1.getId())
                .create();
        GameResponseDto gameResponseDto2 = Instancio.of(GameResponseDto.class)
                .set(field(GameResponseDto::id), gameEntity2.getId())
                .create();

        List<GameEntity> gameEntities = List.of(gameEntity1, gameEntity2);
        List<GameResponseDto> expected = List.of(gameResponseDto1, gameResponseDto2);

        when(gameRepository.findAllByUserId(MOCK_USER_ID, pageable))
                .thenReturn(new PageImpl<>(gameEntities));

        when(gameMapper.toDto(gameEntity1))
                .thenReturn(gameResponseDto1);

        when(gameMapper.toDto(gameEntity2))
                .thenReturn(gameResponseDto2);

        // When
        Page<GameResponseDto> actual = gameService.findAll(pageable);

        // Then
        assertThat(actual.getContent())
                .hasSize(2)
                .containsExactlyElementsOf(expected);

        assertThat(actual.getTotalElements())
                .isEqualTo(2);

        securityUtilMock.verify(SecurityUtil::getAuthenticatedUserId);

        verify(gameRepository).findAllByUserId(MOCK_USER_ID, pageable);
        verify(gameMapper).toDto(gameEntity1);
        verify(gameMapper).toDto(gameEntity2);

    }

    @Test
    @DisplayName("""
            findById method with existing game by id and user id
            should return this game
            """)
    void findById_WithExistingGame_ShouldReturnThisGame() {
        // Given
        GameEntity gameEntity = Instancio.of(GameEntity.class)
                .set(field(GameEntity::getUserId), MOCK_USER_ID)
                .create();

        GameResponseDto expected = Instancio.of(GameResponseDto.class)
                .set(field(GameResponseDto::id), gameEntity.getId())
                .create();

        when(gameRepository.findByIdAndUserId(gameEntity.getId(), MOCK_USER_ID))
                .thenReturn(Optional.of(gameEntity));

        when(gameMapper.toDto(gameEntity))
                .thenReturn(expected);

        // When
        GameResponseDto actual = gameService.findById(gameEntity.getId());

        // Then
        assertThat(actual).isEqualTo(expected);

        securityUtilMock.verify(SecurityUtil::getAuthenticatedUserId);
        securityUtilMock.verifyNoMoreInteractions();

        verify(gameRepository).findByIdAndUserId(gameEntity.getId(), MOCK_USER_ID);
        verify(gameMapper).toDto(gameEntity);

    }

    @Test
    @DisplayName("""
            findById method when game not found should
            throw EntityNotFoundException
            """)
    void findById_WhenGameNotFound_ShouldThrowEntityNotFoundException() {
        // Given
        Long invalidId = 404L;

        when(gameRepository.findByIdAndUserId(invalidId, MOCK_USER_ID))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> gameService.findById(invalidId))
                .isExactlyInstanceOf(EntityNotFoundException.class);

        securityUtilMock.verify(SecurityUtil::getAuthenticatedUserId);
        securityUtilMock.verifyNoMoreInteractions();

        verify(gameRepository).findByIdAndUserId(invalidId, MOCK_USER_ID);

        verifyNoInteractions(gameMapper);

    }

    @Test
    @DisplayName("""
            upload method with valid request should
            return uploaded game
            """)
    void upload_WithValidRequest_ShouldReturnUploadedGame() {
        // Given
        String pgn = """
                    [White "Magnus Carlsen"]
                    [Black "Hikaru Nakamura"]
                    [Result "1/2-1/2"]
                    [WhiteElo "3380"]
                    [BlackElo "3407"]
                    [TimeControl "300"]
                    1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 1/2-1/2
                    """;

        UploadGameRequestDto requestDto = new UploadGameRequestDto(pgn, Color.WHITE);

        GameEntity gameEntity = Instancio.create(GameEntity.class);

        when(gameLoader.load(pgn))
                .thenReturn(gameEntity);

        // When
        gameService.upload(requestDto);

        // Then
        assertThat(gameEntity.getUserId()).isEqualTo(MOCK_USER_ID);
        assertThat(gameEntity.getPgn()).isEqualTo(pgn);
        assertThat(gameEntity.getUserColor()).isEqualTo(requestDto.userColor());
        assertThat(gameEntity.getAnalysisStatus()).isEqualTo(AnalysisStatus.NOT_REQUESTED);

        securityUtilMock.verify(SecurityUtil::getAuthenticatedUserId);
        securityUtilMock.verifyNoMoreInteractions();

        verify(gameLoader).load(pgn);
        verify(gameRepository).saveAndFlush(gameEntity);
        verify(gameMapper).toDto(gameEntity);

        verifyNoMoreInteractions(gameMapper);
    }

    @Test
    @DisplayName("""
            deleteById method with existing game should
            delete the game
            """)
    void deleteById_WithExistingGame_ShouldDeleteTheGame() {
        // Given
        Long id = 43L;

        when(gameRepository.existsByIdAndUserId(id, MOCK_USER_ID))
                .thenReturn(true);

        // When
        gameService.deleteById(id);

        // Then
        securityUtilMock.verify(SecurityUtil::getAuthenticatedUserId);
        securityUtilMock.verifyNoMoreInteractions();

        verify(gameRepository).existsByIdAndUserId(id, MOCK_USER_ID);
        verify(gameRepository).deleteByIdAndUserId(id, MOCK_USER_ID);

    }

    @Test
    @DisplayName("""
            deleteById method when game not found should
            throw EntityNotFoundException
            """)
    void deleteById_WhenGameNotFound_ShouldThrowEntityNotFoundException() {
        // Given
        Long id = 43L;

        when(gameRepository.existsByIdAndUserId(id, MOCK_USER_ID))
                .thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> gameService.deleteById(id))
                .isExactlyInstanceOf(EntityNotFoundException.class);

        securityUtilMock.verify(SecurityUtil::getAuthenticatedUserId);
        securityUtilMock.verifyNoMoreInteractions();

        verify(gameRepository).existsByIdAndUserId(id, MOCK_USER_ID);

        verify(gameRepository, never()).deleteByIdAndUserId(id, MOCK_USER_ID);
    }

}
