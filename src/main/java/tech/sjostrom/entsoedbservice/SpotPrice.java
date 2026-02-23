package tech.sjostrom.entsoedbservice;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "spot_prices")
public class SpotPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;
    private BigDecimal price;
    private String currency;
    private String area;

    public SpotPrice() {}

    public SpotPrice(LocalDateTime timestamp, BigDecimal price, String currency, String area) {
        this.timestamp = timestamp;
        this.price = price;
        this.currency = currency;
        this.area = area;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
}