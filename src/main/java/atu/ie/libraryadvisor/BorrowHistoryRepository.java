package atu.ie.libraryadvisor;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BorrowHistoryRepository extends JpaRepository<BorrowHistory, Long> {
    List<BorrowHistory> findByUserId(Long userId);
}
