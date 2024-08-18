package hexlet.code.util;

import org.apache.commons.lang3.StringUtils;
import java.util.Arrays;
import java.util.stream.Collectors;

public enum PreTaskStatus {
    DRAFT,
    TO_REVIEW,
    TO_BE_FIXED,
    TO_PUBLISH,
    PUBLISHED;

    public String getName() {
        return Arrays.stream(this.toString().toLowerCase().split("_"))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining());
    }

    public String getSlug() {
        return this.toString().toLowerCase();
    }
}
