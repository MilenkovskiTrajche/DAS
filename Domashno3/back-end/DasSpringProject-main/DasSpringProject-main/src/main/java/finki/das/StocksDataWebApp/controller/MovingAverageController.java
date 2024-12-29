package finki.das.StocksDataWebApp.controller;

import finki.das.StocksDataWebApp.model.MovingAverage;
import finki.das.StocksDataWebApp.service.MovingAverageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/moving-averages")
public class MovingAverageController {

    private final MovingAverageService movingAverageService;

    public MovingAverageController(MovingAverageService movingAverageService) {
        this.movingAverageService = movingAverageService;
    }

    @GetMapping
    public ResponseEntity<List<MovingAverage>> getAllMovingAverages() {
        List<MovingAverage> movingAverages = movingAverageService.findAll();
        return new ResponseEntity<>(movingAverages, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovingAverage> getMovingAverageById(@PathVariable Long id) {
        Optional<MovingAverage> movingAverage = movingAverageService.findById(id);
        return movingAverage
                .map(ma -> new ResponseEntity<>(ma, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<MovingAverage> createMovingAverage(@RequestBody MovingAverage movingAverage) {
        MovingAverage savedMovingAverage = movingAverageService.save(movingAverage);
        return new ResponseEntity<>(savedMovingAverage, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovingAverage> updateMovingAverage(@PathVariable Long id, @RequestBody MovingAverage movingAverage) {
        if (!movingAverageService.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        movingAverage.setId(id);
        MovingAverage updatedMovingAverage = movingAverageService.save(movingAverage);
        return new ResponseEntity<>(updatedMovingAverage, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovingAverage(@PathVariable Long id) {
        if (!movingAverageService.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        movingAverageService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<List<MovingAverage>> getMovingAveragesBySymbolAndType(@RequestParam String symbol, @RequestParam String type) {
        List<MovingAverage> movingAverages = movingAverageService.findAllBySymbolAndType(symbol, type);
        return new ResponseEntity<>(movingAverages, HttpStatus.OK);
    }
}
