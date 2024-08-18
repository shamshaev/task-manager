package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class LabelDTO {

    private Long id;

    private String name;

    private Set<Long> taskIds = new HashSet<>();

    private String createdAt;
}
