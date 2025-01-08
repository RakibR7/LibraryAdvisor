package atu.ie.libraryadvisor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BorrowHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long bookId;
    private LocalDateTime borrowedDate;
    private LocalDateTime returnedDate;
    public BorrowHistory(Long userId, Long bookId, LocalDateTime borrowedDate) {
        this.userId = userId;
        this.bookId = bookId;
        this.borrowedDate = borrowedDate;
    }
}
