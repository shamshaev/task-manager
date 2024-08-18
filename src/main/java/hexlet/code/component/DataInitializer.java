package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.CustomUserDetailsService;
import hexlet.code.util.PreLabel;
import hexlet.code.util.PreTaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    public void run(ApplicationArguments args) {
        var userData = new User();
        userData.setEmail("hexlet@example.com");
        userData.setPasswordDigest("qwerty");
        customUserDetailsService.createUser(userData);

        for (PreTaskStatus preTaskStatus : PreTaskStatus.values()) {
            var taskStatus = new TaskStatus();
            taskStatus.setName(preTaskStatus.getName());
            taskStatus.setSlug(preTaskStatus.getSlug());
            taskStatusRepository.save(taskStatus);
        }

        for (PreLabel preLabel : PreLabel.values()) {
            var label = new Label();
            label.setName(preLabel.name().toLowerCase());
            labelRepository.save(label);
        }
    }
}
