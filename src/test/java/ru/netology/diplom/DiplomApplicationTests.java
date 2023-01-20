package ru.netology.diplom;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.*;
import org.springframework.test.context.junit4.*;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DiplomApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGreeting() {
        String body = this.restTemplate.getForObject("/", String.class);
        assertThat(body).isEqualTo("Welcome to Cloud Storage");
    }

}