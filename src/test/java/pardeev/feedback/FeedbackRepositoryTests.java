package pardeev.feedback;

import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.hamcrest.Matcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FeedbackRepositoryTests {

    public static final Feedback ALICE_FEEDBACK = Feedback.builder().name("Alice").message("Alice's feedback").build();
    public static final Feedback BOB_1_FEEDBACK = Feedback.builder().name("Bob").message("Bob's feedback").build();
    public static final Feedback BOB_2_FEEDBACK = Feedback.builder().name("Bob").message("Bob's second feedback").build();

    public static final List<Feedback> DATA = Arrays.asList(ALICE_FEEDBACK, BOB_1_FEEDBACK, BOB_2_FEEDBACK);

    @Autowired
    private FeedbackRepository repository;

    @After
    public void cleanRepository() {
        repository.deleteAll();
    }

    @Test
    public void save() {
        Feedback feedback = repository.save(ALICE_FEEDBACK);
        assertThat(repository.findOne(feedback.getId()).get(), is(feedback));
    }

    @Test
    public void findById() {
        Feedback feedback = repository.save(BOB_1_FEEDBACK);

        assertThat(repository.findOne(feedback.getId()).get(),
                hasProperty("id", allOf(notNullValue(), equalTo(feedback.getId())))
        );
    }

    @Test
    public void findByName() {
        repository.save(DATA);
        List<Feedback> result = repository.findByName("Bob").collect(Collectors.toList());

        assertThat(result,
                allOf((Matcher) hasSize(equalTo(2)),
                        hasItems(BOB_1_FEEDBACK, BOB_2_FEEDBACK))
        );
    }

    @Test
    public void findAll() {
        repository.save(DATA);
        List<Feedback> result = repository.findAll().collect(Collectors.toList());

        assertThat(result,
                allOf((Matcher) hasSize(equalTo(3)),
                        hasItems(ALICE_FEEDBACK, BOB_1_FEEDBACK, BOB_2_FEEDBACK))
        );
    }
}
