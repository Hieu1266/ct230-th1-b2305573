package vn.edu.bookstore.legacy.adapter.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LegacyProblemHandlerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void testProblemTranslation() throws Exception {
        mvc.perform(get("/api/v1/legacy/books/invalid-isbn-format"))
                .andExpect(status().isBadRequest());
    }
}
