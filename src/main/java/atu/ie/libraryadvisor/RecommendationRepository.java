package atu.ie.libraryadvisor;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    Recommendation findByUserId(Long userId);
}
