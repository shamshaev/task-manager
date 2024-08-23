package hexlet.code.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
@AllArgsConstructor
public class LabelKit {
    private final List<String> labelNames = List.of("bug", "feature");
}
