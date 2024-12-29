package finki.das.StocksDataWebApp.service;

import finki.das.StocksDataWebApp.model.Stock;

import java.util.List;
import java.util.Optional;

public interface StockService {
    public List<Stock> findAll();

    public Stock save(Stock stock);

    public Optional<Stock> findById(Long id);

    public void deleteById(Long id);
    boolean existsById(Long id);

    List<Stock> findAllBySymbol(String symbol);
}
