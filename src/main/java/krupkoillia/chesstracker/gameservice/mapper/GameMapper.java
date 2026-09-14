package krupkoillia.chesstracker.gameservice.mapper;

import krupkoillia.chesstracker.gameservice.config.MapStructConfig;
import krupkoillia.chesstracker.gameservice.dto.GameResponseDto;
import krupkoillia.chesstracker.gameservice.model.GameEntity;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface GameMapper {

    GameResponseDto toDto(GameEntity model);

}
