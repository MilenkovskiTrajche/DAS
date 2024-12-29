package finki.das.StocksDataWebApp.controller;

import finki.das.StocksDataWebApp.model.Oscillator;
import finki.das.StocksDataWebApp.service.OscillatorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/oscillators")
public class OscillatorController {

    private final OscillatorService oscillatorService;

    public OscillatorController(OscillatorService oscillatorService) {
        this.oscillatorService = oscillatorService;
    }

    @GetMapping
    public ResponseEntity<List<Oscillator>> getAllOscillators() {
        List<Oscillator> oscillators = oscillatorService.findAll();
        return new ResponseEntity<>(oscillators, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Oscillator> getOscillatorById(@PathVariable Long id) {
        Optional<Oscillator> oscillator = oscillatorService.findById(id);
        return oscillator
                .map(o -> new ResponseEntity<>(o, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Oscillator> createOscillator(@RequestBody Oscillator oscillator) {
        Oscillator savedOscillator = oscillatorService.save(oscillator);
        return new ResponseEntity<>(savedOscillator, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Oscillator> updateOscillator(@PathVariable Long id, @RequestBody Oscillator oscillator) {
        if (!oscillatorService.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        oscillator.setId(id);
        Oscillator updatedOscillator = oscillatorService.save(oscillator);
        return new ResponseEntity<>(updatedOscillator, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOscillator(@PathVariable Long id) {
        if (!oscillatorService.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        oscillatorService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Oscillator>> getOscillatorsBySymbolAndType(@RequestParam String symbol, @RequestParam String type) {
        List<Oscillator> oscillators = oscillatorService.findAllBySymbolAndType(symbol, type);
        return new ResponseEntity<>(oscillators, HttpStatus.OK);
    }
}