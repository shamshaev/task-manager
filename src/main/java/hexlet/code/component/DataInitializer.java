package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.CustomUserDetailsService;
import hexlet.code.util.LabelKit;
import hexlet.code.util.TaskStatusKit;
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

    @Autowired
    private TaskStatusKit taskStatusKit;

    @Autowired
    private LabelKit labelKit;

    public void run(ApplicationArguments args) {

        if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
            initializeAdmin();
        }

        if (taskStatusRepository.findAll().isEmpty()) {
            initializeTaskStatuses();
        }

        if (labelRepository.findAll().isEmpty()) {
            initializeLabels();
        }
    }

    public void initializeAdmin() {
        var userData = new User();
        userData.setEmail("hexlet@example.com");
        userData.setPasswordDigest("qwerty");
        customUserDetailsService.createUser(userData);
    }

    public void initializeTaskStatuses() {
        for (var slug : taskStatusKit.getSlugs()) {
            var taskStatus = new TaskStatus();
            taskStatus.setSlug(slug);
            taskStatus.setName(taskStatusKit.getName(slug));
            taskStatusRepository.save(taskStatus);
        }
    }

    public void initializeLabels() {
        for (var labelName : labelKit.getLabelNames()) {
            var label = new Label();
            label.setName(labelName);
            labelRepository.save(label);
        }
    }
}
