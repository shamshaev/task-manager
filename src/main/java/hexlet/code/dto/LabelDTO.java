package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class LabelDTO {

    private Long id;

    private String name;

    @JsonIgnore
    private Set<Long> taskIds;

    private String createdAt;
}
