package atu.ie.libraryadvisor;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests the GET /api/recommendations/{userId} endpoint.
 */
@WebMvcTest(RecommendationController.class)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationRepository recommendationRepository;

    @Test
    void testGetRecommendations_found() throws Exception {
        Recommendation rec = new Recommendation(2L, java.util.List.of(10L, 11L));
        rec.setId(100L);

        when(recommendationRepository.findByUserId(2L)).thenReturn(rec);

        mockMvc.perform(get("/api/recommendations/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(2))
                .andExpect(jsonPath("$.recommendedBookIds[0]").value(10));
    }

    @Test
    void testGetRecommendations_notFound() throws Exception {
        when(recommendationRepository.findByUserId(99L)).thenReturn(null);

        mockMvc.perform(get("/api/recommendations/99").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // The controller returns 200 with an empty object
                .andExpect(jsonPath("$.id").doesNotExist()) // or check something else
                .andExpect(jsonPath("$.recommendedBookIds").isEmpty());
    }
}
