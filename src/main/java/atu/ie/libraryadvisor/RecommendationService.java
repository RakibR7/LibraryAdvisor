package atu.ie.libraryadvisor;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationService {

    @Autowired
    private RecommendationRepository recommendationRepository;

    // Listener for RabbitMQ messages
    @RabbitListener(queues = "recommendation.queue")
    public void updateRecommendations(String message) {
        String[] data = message.split(",");
        Long userId = Long.parseLong(data[0]);
        Long bookId = Long.parseLong(data[1]);
        String action = data[2]; // "borrow" or "return"

        // Example: Generate simple recommendations based on action
        Recommendation recommendation = recommendationRepository.findByUserId(userId);
        if (recommendation == null) {
            recommendation = new Recommendation();
            recommendation.setUserId(userId);
            recommendation.setRecommendedBookIds(new ArrayList<>());
        }

        if (action.equals("borrow")) {
            // Example logic: Recommend books of the same genre as the borrowed book
            List<Long> newRecommendations = generateRecommendations(bookId);
            recommendation.getRecommendedBookIds().addAll(newRecommendations);
        } else if (action.equals("return")) {
            // Optional: Adjust recommendations on book return
        }

        recommendationRepository.save(recommendation);
    }

    private List<Long> generateRecommendations(Long bookId) {
        // Replace with real recommendation logic (e.g., query BookService)
        List<Long> recommendations = new ArrayList<>();
        recommendations.add(bookId + 1); // Dummy recommendation logic
        recommendations.add(bookId + 2);
        return recommendations;
    }
}
