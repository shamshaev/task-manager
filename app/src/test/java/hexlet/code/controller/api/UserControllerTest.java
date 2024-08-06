package hexlet.code.controller.api;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;

import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
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

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private final Faker faker = new Faker();

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getPasswordDigest), () -> faker.internet().password())
                .create();
    }

    @Test
    public void testIndex() throws Exception {
        //given
        userRepository.save(testUser);
        //when
        MvcResult result = mockMvc.perform(get("/api/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        MockHttpServletResponse response = result.getResponse();
        assertThatJson(response.getContentAsString()).isArray();
    }

    @Test
    public void testShow() throws Exception {
        //given
        userRepository.save(testUser);
        //when
        MvcResult result = mockMvc.perform(get("/api/users/" + testUser.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        MockHttpServletResponse response = result.getResponse();
        assertThatJson(response.getContentAsString()).and(
                v -> v.node("email").isEqualTo(testUser.getEmail()),
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName())
        );
    }

    @Test
    public void testCreate() throws Exception {
        //given
        //var dto = userMapper.map(testUser);
        var dto = new UserCreateDTO();
        dto.setEmail("anna@gmail.com");
        dto.setFirstName("anna");
        dto.setPassword("545");
        //when
        RequestBuilder request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated());
        //then
        var user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with email '" + dto.getEmail() + "' not found"));
        assertNotNull(user);
        assertThat(user.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(user.getLastName()).isEqualTo(dto.getLastName());
    }

    @Test
    public void testUpdate() throws Exception {
        //given
        userRepository.save(testUser);

        var dto = new UserUpdateDTO();
        dto.setFirstName(JsonNullable.of("updated_name"));
        //when
        RequestBuilder request = put("/api/users/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
        //then
        var user = userRepository.findByEmail(testUser.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with email '" + testUser.getEmail() + "' not found"));
        assertThat(user.getFirstName()).isEqualTo(dto.getFirstName().get());
    }

    @Test
    public void testDestroy() throws Exception {
        //given
        userRepository.save(testUser);
        //when
        mockMvc.perform(delete("/api/users/" + testUser.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
        //then
        assertThat(userRepository.existsById(testUser.getId())).isEqualTo(false);
    }
}