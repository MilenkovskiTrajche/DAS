package finki.das.StocksDataWebApp.repository;

import finki.das.StocksDataWebApp.model.MovingAverage;
import finki.das.StocksDataWebApp.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovingAverageRepository extends JpaRepository<MovingAverage, Long> {

    List<MovingAverage> findAllBySymbolAndType(String symbol, String type);
}
