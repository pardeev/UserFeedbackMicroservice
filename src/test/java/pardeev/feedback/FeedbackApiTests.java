package pardeev.feedback;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FeedbackApiTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FeedbackRepository repository;

    private MockMvc mvc;

    public static final Feedback ALICE_FEEDBACK = Feedback.builder().name("Alice").message("Alice's feedback").build();
    public static final Feedback BOB_1_FEEDBACK = Feedback.builder().name("Bob").message("Bob's feedback").build();
    public static final Feedback BOB_2_FEEDBACK = Feedback.builder().name("Bob").message("Bob's second feedback").build();

    public static final List<Feedback> DATA = Arrays.asList(ALICE_FEEDBACK, BOB_1_FEEDBACK, BOB_2_FEEDBACK);

    @Before
    public void setUpTest() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Before
    public void setupRepository() {
        repository.save(DATA);
    }

    @After
    public void cleanRepository() {
        repository.deleteAll();
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void sendValidFeedback() throws Exception {
        Feedback feedback = Feedback.builder().name("Tom").message("Tom's message").build();

        mvc.perform(post("/feedback").contentType(APPLICATION_JSON_UTF8).content(json(feedback)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is(feedback.getName())))
                .andExpect(jsonPath("$.message", is(feedback.getMessage())))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    public void sendInvalidFeedback() throws Exception {
        Feedback feedback = new Feedback(null, null, null, null);
        mvc.perform(post("/feedback").content(json(feedback)).contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void sendEmptyFeedback() throws Exception {
        mvc.perform(post("/feedback").contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void findAllFeedback() throws Exception {
        mvc.perform(get("/feedback").accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].name", hasItems("Alice", "Bob")));
    }

    @Test
    public void findFeedbackByName() throws Exception {
        mvc.perform(get("/feedback").param("name", "Bob").accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.name == 'Bob')]", hasSize(2)));
    }

    @Test
    public void findFeedbackByNameNotExist() throws Exception {
        mvc.perform(get("/feedback").param("name", "Nobody").accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    public void findFeedBackByNameEmptyInput() throws Exception {
        mvc.perform(get("/feedback").param("name", "").accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    public void findFeedbackById() throws Exception {
        Feedback feedback = repository.findAll().findFirst().get();
        mvc.perform(get("/feedback/" + feedback.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(feedback.getId().toString())))
                .andExpect(jsonPath("$.name", is(feedback.getName())));
    }

    @Test
    public void findFeedbackByIdNotExist() throws Exception {
        mvc.perform(get("/feedback/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findFeedbackByIdInvalidInput() throws Exception {
        mvc.perform(get("/feedback/string"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void notAcceptableMediaType() throws Exception {
        mvc.perform(get("/feedback").accept(MediaType.APPLICATION_XML))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void unsupportedMediaType() throws Exception {
        mvc.perform(post("/feedback").contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void defaultMediaType() throws Exception {
        mvc.perform(get("/feedback"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8));
    }

    protected String json(Object o) throws IOException {
        return objectMapper.writeValueAsString(o);
    }
}
