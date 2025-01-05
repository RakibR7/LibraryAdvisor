package atu.ie.libraryadvisor;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationService {

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private RestTemplate restTemplate;

    @RabbitListener(queues = RabbitMQConfig.BORROW_BOOK_QUEUE)
    public void handleBorrowMessage(String message) {
        System.out.println("Advisor received BORROW message: " + message);

        // Example message: "Borrowed: User 2, Book 1"
        // We'll parse user=2, book=1 from the string.
        Long userId = parseUserId(message);
        Long bookId = parseBookId(message);

        // If null or parse error, just ignore
        if (userId == null || bookId == null) {
            return;
        }

        // Grab the Book info from LibraryHub
        BookDto borrowedBook = getBookInfo(bookId);
        if (borrowedBook == null) {
            return; // can't do anything without book info
        }

        // Generate recommendations (super naive: same genre)
        List<Long> recommendedIds = generateRecommendations(borrowedBook);

        // Update or create the user's recommendation entry
        saveUserRecommendations(userId, recommendedIds);
    }

    @RabbitListener(queues = RabbitMQConfig.RETURN_BOOK_QUEUE)
    public void handleReturnMessage(String message) {
        System.out.println("Advisor received RETURN message: " + message);

        // You can do something similar if you want to adjust recs on return
        // For now, we won't do anything special.
    }

    private Long parseUserId(String message) {
        // e.g. "Borrowed: User 2, Book 1"
        // We'll do a quick substring. In production, you'd do something safer.
        // Or if your actual message is "2,1", then parse with split(",").
        try {
            // We'll look for "User " and then read the integer after it
            int userIndex = message.indexOf("User ");
            if (userIndex == -1) return null;

            // e.g. "User 2"
            int commaIndex = message.indexOf(",", userIndex);
            String userSub = message.substring(userIndex + 5, commaIndex).trim(); // 5 = length("User ")
            return Long.parseLong(userSub);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Long parseBookId(String message) {
        // e.g. "Book 1"
        try {
            int bookIndex = message.indexOf("Book ");
            if (bookIndex == -1) return null;
            String bookSub = message.substring(bookIndex + 5).trim(); // 5 = length("Book ")
            return Long.parseLong(bookSub);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private BookDto getBookInfo(Long bookId) {
        // If both microservices are in Docker, use "libraryhub:8081"
        // If local dev, "http://localhost:8081"
        String url = "http://libraryhub:8081/api/books/" + bookId;
        try {
            return restTemplate.getForObject(url, BookDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Long> generateRecommendations(BookDto borrowedBook) {
        // For example: find all books that share the same genre
        // except the borrowed one.
        String url = "http://libraryhub:8081/api/books";
        BookDto[] allBooks = restTemplate.getForObject(url, BookDto[].class);
        if (allBooks == null) return List.of();

        List<Long> recommended = new ArrayList<>();
        for (BookDto b : allBooks) {
            if (b.getGenre() != null
                    && b.getGenre().equalsIgnoreCase(borrowedBook.getGenre())
                    && !b.getId().equals(borrowedBook.getId())) {
                recommended.add(b.getId());
            }
        }
        return recommended;
    }

    private void saveUserRecommendations(Long userId, List<Long> newRecIds) {
        Recommendation rec = recommendationRepository.findByUserId(userId);
        if (rec == null) {
            rec = new Recommendation(userId, newRecIds);
        } else {
            // just add them if you want to accumulate
            rec.getRecommendedBookIds().addAll(newRecIds);
        }
        recommendationRepository.save(rec);
    }
}
