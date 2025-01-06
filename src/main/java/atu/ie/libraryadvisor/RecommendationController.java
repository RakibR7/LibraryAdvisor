package atu.ie.libraryadvisor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    @Autowired
    private RecommendationRepository recommendationRepository;
    @GetMapping("/{userId}")
    public ResponseEntity<Recommendation> getRecommendations(@PathVariable Long userId) {
        Recommendation r = recommendationRepository.findByUserId(userId);
        if (r == null) return ResponseEntity.ok(new Recommendation());
        return ResponseEntity.ok(r);
    }
}
