package finki.das.StocksDataWebApp.service.impl;

import finki.das.StocksDataWebApp.model.MovingAverage;
import finki.das.StocksDataWebApp.model.Oscillator;
import finki.das.StocksDataWebApp.model.Stock;
import finki.das.StocksDataWebApp.repository.OscillatorRepository;
import finki.das.StocksDataWebApp.service.OscillatorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OscillatorServiceImpl implements OscillatorService {

    private final OscillatorRepository oscillatorRepository;

    public OscillatorServiceImpl(OscillatorRepository oscillatorRepository) {
        this.oscillatorRepository = oscillatorRepository;
    }

    @Override
    public List<Oscillator> findAll() {
        return oscillatorRepository.findAll();
    }

    @Override
    public Oscillator save(Oscillator oscillator) {
        return oscillatorRepository.save(oscillator);
    }

    @Override
    public Optional<Oscillator> findById(Long id) {
        return oscillatorRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        oscillatorRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return oscillatorRepository.existsById(id);
    }

    @Override
    public List<Oscillator> findAllBySymbolAndType(String symbol, String type) {
        return oscillatorRepository.findAllBySymbolAndType(symbol, type);
    }
}
