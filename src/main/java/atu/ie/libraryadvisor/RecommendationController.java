package atu.ie.libraryadvisor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationRepository recommendationRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<Recommendation> getRecommendations(@PathVariable Long userId) {
        Recommendation recommendation = recommendationRepository.findByUserId(userId);
        if (recommendation == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recommendation);
    }
}
