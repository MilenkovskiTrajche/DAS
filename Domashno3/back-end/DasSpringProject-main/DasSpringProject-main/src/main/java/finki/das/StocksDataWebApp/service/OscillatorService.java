package finki.das.StocksDataWebApp.service;

import finki.das.StocksDataWebApp.model.MovingAverage;
import finki.das.StocksDataWebApp.model.Oscillator;
import finki.das.StocksDataWebApp.model.Stock;

import java.util.List;
import java.util.Optional;

public interface OscillatorService {
    public List<Oscillator> findAll();

    public Oscillator save(Oscillator oscillator);

    public Optional<Oscillator> findById(Long id);

    public void deleteById(Long id);

    boolean existsById(Long id);

    List<Oscillator> findAllBySymbolAndType(String symbol, String type);
}
