package finki.das.StocksDataWebApp.service;

import finki.das.StocksDataWebApp.model.Stock;
import finki.das.StocksDataWebApp.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockServiceImpl implements StockService{

    private final StockRepository stockRepository;

    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public List<Stock> findAll() {
        return stockRepository.findAll();
    }

    @Override
    public Stock save(Stock stock) {
        return stockRepository.save(stock);
    }

    @Override
    public Optional<Stock> findById(Long id) {
        return stockRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        stockRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return stockRepository.existsById(id);
    }

    @Override
    public List<Stock> findAllBySymbol(String symbol) {
        return stockRepository.findAllBySymbol(symbol);
    }
}
