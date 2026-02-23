package tech.sjostrom.entsoedbservice;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface SpotPriceRepository extends JpaRepository<SpotPrice, Long> {
    List<SpotPrice> findByAreaOrderByTimestampDesc(String area);
    List<SpotPrice> findByTimestampBetweenOrderByTimestamp(LocalDateTime start, LocalDateTime end);
    List<SpotPrice> findByAreaAndTimestampBetweenOrderByTimestamp(String area, LocalDateTime start, LocalDateTime end);
}