package hexlet.code.util;

import net.datafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DataUtils {

    @Bean
    public Faker getFaker() {
        return new Faker();
    }
}
