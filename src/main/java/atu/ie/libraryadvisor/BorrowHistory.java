package atu.ie.libraryadvisor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class BorrowHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long bookId;
    private LocalDateTime borrowedDate;
    private LocalDateTime returnedDate;
    public BorrowHistory() {}
    public BorrowHistory(Long userId, Long bookId, LocalDateTime borrowedDate) {
        this.userId = userId;
        this.bookId = bookId;
        this.borrowedDate = borrowedDate;
    }
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public LocalDateTime getBorrowedDate() { return borrowedDate; }
    public void setBorrowedDate(LocalDateTime borrowedDate) { this.borrowedDate = borrowedDate; }
    public LocalDateTime getReturnedDate() { return returnedDate; }
    public void setReturnedDate(LocalDateTime returnedDate) { this.returnedDate = returnedDate; }
}
