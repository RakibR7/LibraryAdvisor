package atu.ie.libraryadvisor;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    @Autowired
    private RecommendationRepository recommendationRepository;
    @Autowired
    private BorrowHistoryRepository borrowHistoryRepository;
    @Autowired
    private RestTemplate restTemplate;

    @RabbitListener(queues = RabbitMQConfig.BORROW_BOOK_QUEUE)
    public void handleBorrowMessage(String msg) {
        Long userId = parseUser(msg);
        Long bookId = parseBook(msg);
        if (userId == null || bookId == null) return;
        borrowHistoryRepository.save(new BorrowHistory(userId, bookId, LocalDateTime.now()));
        updateRecommendations(userId);
    }

    @RabbitListener(queues = RabbitMQConfig.RETURN_BOOK_QUEUE)
    public void handleReturnMessage(String msg) {
        Long userId = parseUser(msg);
        Long bookId = parseBook(msg);
        if (userId == null || bookId == null) return;
        BorrowHistory record = borrowHistoryRepository.findByUserId(userId).stream()
                .filter(b -> b.getBookId().equals(bookId) && b.getReturnedDate() == null)
                .findFirst().orElse(null);
        if (record != null) {
            record.setReturnedDate(LocalDateTime.now());
            borrowHistoryRepository.save(record);
        }
        updateRecommendations(userId);
    }

    private void updateRecommendations(Long userId) {
        List<BorrowHistory> all = borrowHistoryRepository.findByUserId(userId);
        if (all.isEmpty()) {
            clearRec(userId);
            return;
        }
        Set<String> userGenres = new HashSet<>();
        Set<String> userAuthors = new HashSet<>();
        for (BorrowHistory bh : all) {
            BookDto info = getBook(bh.getBookId());
            if (info != null) {
                if (info.getGenre() != null) userGenres.add(info.getGenre().toLowerCase());
                if (info.getAuthor() != null) userAuthors.add(info.getAuthor().toLowerCase());
            }
        }
        BookDto[] allBooks = restTemplate.getForObject("http://libraryhub:8081/api/books", BookDto[].class);
        if (allBooks == null) {
            clearRec(userId);
            return;
        }
        List<Long> matches = Arrays.stream(allBooks).filter(b -> {
            String g = b.getGenre() == null ? "" : b.getGenre().toLowerCase();
            String a = b.getAuthor() == null ? "" : b.getAuthor().toLowerCase();
            return userGenres.contains(g) || userAuthors.contains(a);
        }).map(BookDto::getId).collect(Collectors.toList());
        saveRec(userId, matches);
    }

    private BookDto getBook(Long bookId) {
        try {
            return restTemplate.getForObject("http://libraryhub:8081/api/books/" + bookId, BookDto.class);
        } catch (Exception e) {
            return null;
        }
    }

    private void clearRec(Long userId) {
        Recommendation r = recommendationRepository.findByUserId(userId);
        if (r != null) {
            r.setRecommendedBookIds(new ArrayList<>());
            recommendationRepository.save(r);
        }
    }

    private void saveRec(Long userId, List<Long> ids) {
        Recommendation r = recommendationRepository.findByUserId(userId);
        if (r == null) r = new Recommendation(userId, ids);
        else {
            r.setRecommendedBookIds(ids);
        }
        recommendationRepository.save(r);
    }

    private Long parseUser(String msg) {
        try {
            int i = msg.indexOf("User ");
            if (i == -1) return null;
            int c = msg.indexOf(",", i);
            String s = msg.substring(i + 5, c).trim();
            return Long.parseLong(s);
        } catch (Exception e) {
            return null;
        }
    }

    private Long parseBook(String msg) {
        try {
            int i = msg.indexOf("Book ");
            if (i == -1) return null;
            String s = msg.substring(i + 5).trim();
            return Long.parseLong(s);
        } catch (Exception e) {
            return null;
        }
    }
}
