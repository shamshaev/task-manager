package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.util.ModelGenerator;
import hexlet.code.util.UserUtils;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelGenerator modelGenerator;

    private Task testTask;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testTask = Instancio.of(modelGenerator.getTaskModel())
                .create();
        var testStatus = Instancio.of(modelGenerator.getTaskStatusModel())
                .create();
        taskStatusRepository.save(testStatus);
        testTask.setTaskStatus(testStatus);
        testTask.setAssignee(userUtils.getTestUser());
        var label = Instancio.of(modelGenerator.getLabelModel())
                .create();
        labelRepository.save(label);
        testTask.setTaskLabels(Set.of(label));
        taskRepository.save(testTask);

        var testTask2 = Instancio.of(modelGenerator.getTaskModel())
                .create();
        testTask2.setTaskStatus(testStatus);
        taskRepository.save(testTask2);
    }

    @Test
    public void testIndex() throws Exception {

        MvcResult result = mockMvc.perform(get("/api/tasks").with(jwt()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        List<TaskDTO> taskDTOS = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() { });
        var actual = taskDTOS.stream().map(taskMapper::map).toList();
        var expected = taskRepository.findAll();
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testFilteredIndex() throws Exception {
        var titleCont = testTask.getName().substring(1);
        var assigneeId = testTask.getAssignee().getId();
        var status = testTask.getTaskStatus().getSlug();
        var labelId = testTask.getTaskLabels().stream()
                .map(Label::getId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("testTask with id " + testTask.getId()
                        + " has no 'taskLabels' field in the testFilteredIndex"));

        System.out.println(testTask);

        MvcResult result = mockMvc.perform(get("/api/tasks?titleCont=" + titleCont + "&assigneeId="
                        + assigneeId + "&status=" + status + "&labelId=" + labelId).with(jwt()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertThatJson(response.getContentAsString())
                .isArray()
                .hasSize(1);
    }

    @Test
    public void testShow() throws Exception {

        MvcResult result = mockMvc.perform(get("/api/tasks/" + testTask.getId()).with(jwt()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertThatJson(response.getContentAsString()).and(
                v -> v.node("title").isEqualTo(testTask.getName()),
                v -> v.node("content").isEqualTo(testTask.getDescription())
        );
    }

    @Test
    public void testCreate() throws Exception {
        testTask.setName("new_name");
        var dto = taskMapper.map(testTask);

        RequestBuilder request = post("/api/tasks")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        MvcResult result = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        TaskDTO responseDTO = objectMapper.readValue(response.getContentAsString(), TaskDTO.class);
        Task task = taskRepository.findById(responseDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Task with id '"
                        + responseDTO.getId() + "' not found"));
        assertNotNull(task);
        assertThat(task.getName()).isEqualTo(dto.getTitle());
        assertThat(task.getDescription()).isEqualTo(dto.getContent());
    }

    @Test
    public void testUpdate() throws Exception {

        var dto = new TaskUpdateDTO();
        dto.setTitle(JsonNullable.of("updated_name"));

        RequestBuilder request = put("/api/tasks/" + testTask.getId())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());

        var task = taskRepository.findById(testTask.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Task with id '"
                        + testTask.getId() + "' not found"));
        assertThat(task.getName()).isEqualTo(dto.getTitle().get());
    }

    @Test
    public void testDestroy() throws Exception {

        mockMvc.perform(delete("/api/tasks/" + testTask.getId()).with(jwt()))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertThat(taskRepository.existsById(testTask.getId())).isEqualTo(false);
    }
}
