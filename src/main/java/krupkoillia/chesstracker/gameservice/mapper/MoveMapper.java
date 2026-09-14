package krupkoillia.chesstracker.gameservice.mapper;

import krupkoillia.chesstracker.gameservice.config.MapStructConfig;
import krupkoillia.chesstracker.gameservice.dto.MoveResponseDto;
import krupkoillia.chesstracker.gameservice.model.MoveEntity;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface MoveMapper {

    MoveResponseDto toDto(MoveEntity model);

}
