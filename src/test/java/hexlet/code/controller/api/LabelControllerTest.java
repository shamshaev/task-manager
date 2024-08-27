package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.util.ModelGenerator;
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
class LabelControllerTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper labelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelGenerator modelGenerator;

    private Label testLabel;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testLabel = Instancio.of(modelGenerator.getLabelModel())
                .create();
        labelRepository.save(testLabel);
    }

    @Test
    public void testIndex() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/labels").with(jwt()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        List<LabelDTO> labelDTOS = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() { });
        var actual = labelDTOS.stream().map(labelMapper::map).toList();
        var expected = labelRepository.findAll();
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/labels/" + testLabel.getId()).with(jwt()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertThatJson(response.getContentAsString()).and(
                v -> v.node("name").isEqualTo(testLabel.getName())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getLabelModel())
                .create();

        RequestBuilder request = post("/api/labels")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated());

        var label = labelRepository.findByName(data.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Label with name '"
                        + data.getName() + "' not found"));
        assertNotNull(label);
        assertThat(label.getName()).isEqualTo(data.getName());
    }

    @Test
    public void testUpdate() throws Exception {
        var dto = new LabelUpdateDTO();
        dto.setName(JsonNullable.of("updated_name"));

        RequestBuilder request = put("/api/labels/" + testLabel.getId())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());

        var label = labelRepository.findById(testLabel.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Label with id '"
                        + testLabel.getId() + "' not found"));
        assertThat(label.getName()).isEqualTo(dto.getName().get());
    }

    @Test
    public void testDestroy() throws Exception {
        mockMvc.perform(delete("/api/labels/" + testLabel.getId()).with(jwt()))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertThat(labelRepository.existsById(testLabel.getId())).isEqualTo(false);
    }
}
