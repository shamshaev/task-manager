package hexlet.code.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Getter
@AllArgsConstructor
public class TaskStatusKit {
    private final List<String> slugs = List.of("draft", "to_review", "to_be_fixed", "to_publish", "published");

    public String getName(String slug) {
        return Arrays.stream(slug.split("_"))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining());
    }
}
