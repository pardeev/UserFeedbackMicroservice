package pardeev.feedback;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FeedbackApiPaginationTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FeedbackRepository repository;

    private MockMvc mvc;

    @Before
    public void setUpTest() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Before
    public void setupRepository() {
        IntStream.range(0, 35).mapToObj(i ->
                Feedback.builder().name("User" + (i % 3)).message("Message").createdAt(new Date()).build()
        ).forEach(repository::save);
    }

    @After
    public void cleanRepository() {
        repository.deleteAll();
    }

    @Test
    public void findAllFeedbackWithLimit() throws Exception {
        mvc.perform(get("/feedback").param("limit", "10").accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(10)));
    }

    @Test
    public void findAllFeedbackWithOffset() throws Exception {
        mvc.perform(get("/feedback").param("offset", "30").accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    public void findAllFeedbackWithLimitAndOffset() throws Exception {
        mvc.perform(get("/feedback")
                .param("offset", "10")
                .param("limit", "10")
                .accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(10)));
    }

    @Test
    public void findByNameWithLimitAndOffset() throws Exception {
        mvc.perform(get("/feedback")
                .param("name", "User0")
                .param("offset", "10")
                .param("limit", "2")
                .accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void invalidInputString() throws Exception {
        mvc.perform(get("/feedback")
                .param("offset", "BAD")
                .accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void invalidInputNegativeNumber() throws Exception {
        mvc.perform(get("/feedback")
                .param("offset", "-1")
                .accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }
}
