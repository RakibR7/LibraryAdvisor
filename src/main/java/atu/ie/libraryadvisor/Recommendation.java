package atu.ie.libraryadvisor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @ElementCollection
    private List<Long> recommendedBookIds = new ArrayList<>();
    public Recommendation(Long userId, List<Long> recommendedBookIds) {
        this.userId = userId;
        this.recommendedBookIds = recommendedBookIds;
    }
    public void setId(long l) {}
}
