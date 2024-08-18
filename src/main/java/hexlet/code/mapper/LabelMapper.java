package hexlet.code.mapper;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.model.Label;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class LabelMapper {

    @Mapping(source = "taskIds", target = "tasks")
    public abstract Label map(LabelCreateDTO dto);

    @Mapping(source = "tasks", target = "taskIds")
    public abstract LabelDTO map(Label model);

    @Mapping(source = "taskIds", target = "tasks")
    public abstract Label map(LabelDTO dto);

    @Mapping(source = "taskIds", target = "tasks")
    public abstract void update(LabelUpdateDTO dto, @MappingTarget Label model);
}
