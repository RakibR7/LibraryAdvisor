package atu.ie.libraryadvisor;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    // This will store recommended Book IDs
    @ElementCollection
    private List<Long> recommendedBookIds = new ArrayList<>();

    // Constructors, getters, setters

    public Recommendation() {}

    public Recommendation(Long userId, List<Long> recommendedBookIds) {
        this.userId = userId;
        this.recommendedBookIds = recommendedBookIds;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Long> getRecommendedBookIds() {
        return recommendedBookIds;
    }
    public void setRecommendedBookIds(List<Long> recommendedBookIds) {
        this.recommendedBookIds = recommendedBookIds;
    }
}
