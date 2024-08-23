package hexlet.code.util;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
public class LabelKit {
    private final List<String> labelNames = List.of("bug", "feature");
}
