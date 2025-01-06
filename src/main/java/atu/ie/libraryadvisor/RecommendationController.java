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
        Recommendation rec = recommendationRepository.findByUserId(userId);
        if (rec == null) {
            return ResponseEntity.ok(new Recommendation()); // or return an empty list
        }
        return ResponseEntity.ok(rec);
    }
}
