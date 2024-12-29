package finki.das.StocksDataWebApp.service.impl;

import finki.das.StocksDataWebApp.model.MovingAverage;
import finki.das.StocksDataWebApp.repository.MovingAverageRepository;
import finki.das.StocksDataWebApp.service.MovingAverageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovingAverageServiceImpl implements MovingAverageService {

    private final MovingAverageRepository movingAverageRepository;

    public MovingAverageServiceImpl(MovingAverageRepository movingAverageRepository) {
        this.movingAverageRepository = movingAverageRepository;
    }

    @Override
    public List<MovingAverage> findAll() {
        return movingAverageRepository.findAll();
    }

    @Override
    public MovingAverage save(MovingAverage movingAverage) {
        return movingAverageRepository.save(movingAverage);
    }

    @Override
    public Optional<MovingAverage> findById(Long id) {
        return movingAverageRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        movingAverageRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return movingAverageRepository.existsById(id);
    }

    @Override
    public List<MovingAverage> findAllBySymbolAndType(String symbol, String type) {
        return movingAverageRepository.findAllBySymbolAndType(symbol, type);
    }
}
