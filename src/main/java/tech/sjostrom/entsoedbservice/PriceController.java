package tech.sjostrom.entsoedbservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/prices")
public class PriceController {

    @Autowired
    private SpotPriceRepository repository;

    @GetMapping
    public List<SpotPrice> getAllPrices() {
        return repository.findAll();
    }

    @GetMapping("/area/{area}")
    public List<SpotPrice> getByArea(@PathVariable String area) {
        return repository.findByAreaOrderByTimestampDesc(area);
    }

    @GetMapping("/today")
    public List<SpotPrice> getToday() {
        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = start.plusDays(1);
        return repository.findByTimestampBetweenOrderByTimestamp(start, end);
    }
}