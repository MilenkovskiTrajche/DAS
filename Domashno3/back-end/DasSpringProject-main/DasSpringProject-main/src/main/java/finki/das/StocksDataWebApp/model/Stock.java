package finki.das.StocksDataWebApp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;
    private LocalDate date;
    private Double lastTransaction;
    private Double maks;
    private Double min;
    private Double averagePrice;
    private Double promPercent;
    private Long quantity;
    private Long revenueBEST;
    private Long totalRevenue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getLastTransaction() {
        return lastTransaction;
    }

    public void setLastTransaction(Double lastTransaction) {
        this.lastTransaction = lastTransaction;
    }

    public Double getMaks() {
        return maks;
    }

    public void setMaks(Double maks) {
        this.maks = maks;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(Double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public Double getPromPercent() {
        return promPercent;
    }

    public void setPromPercent(Double promPercent) {
        this.promPercent = promPercent;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getRevenueBEST() {
        return revenueBEST;
    }

    public void setRevenueBEST(Long revenueBEST) {
        this.revenueBEST = revenueBEST;
    }

    public Long getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Long totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
