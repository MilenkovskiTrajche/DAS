package finki.das.StocksDataWebApp.service;

import finki.das.StocksDataWebApp.model.MovingAverage;
import finki.das.StocksDataWebApp.model.Stock;

import java.util.List;
import java.util.Optional;

public interface MovingAverageService {
    public List<MovingAverage> findAll();

    public MovingAverage save(MovingAverage movingAverage);

    public Optional<MovingAverage> findById(Long id);

    public void deleteById(Long id);

    boolean existsById(Long id);

    List<MovingAverage> findAllBySymbolAndType(String symbol, String type);
}