package finki.das.StocksDataWebApp.repository;

import finki.das.StocksDataWebApp.model.Oscillator;
import finki.das.StocksDataWebApp.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OscillatorRepository extends JpaRepository<Oscillator, Long> {
    List<Oscillator> findAllBySymbolAndType(String symbol, String type);


}