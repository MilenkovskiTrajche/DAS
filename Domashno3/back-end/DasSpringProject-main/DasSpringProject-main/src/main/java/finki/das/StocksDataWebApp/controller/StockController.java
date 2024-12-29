package finki.das.StocksDataWebApp.controller;

import finki.das.StocksDataWebApp.model.Stock;
import finki.das.StocksDataWebApp.script_service.ScraperScriptRunner;
import finki.das.StocksDataWebApp.service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(value = "/api/stocks")
//@Validated
@CrossOrigin(origins="*")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }
    @GetMapping("/data")
    public ResponseEntity<String> getData(){
        return ResponseEntity.ok("Hello from SpringBoot");
    }

    @PostMapping("/scraper")
    public ResponseEntity<?>getDataFromScraper(){
        String scriptPath="D:\\Fakultet\\3Ta godina\\Dizajn i arhitektura na softver\\3to Domasno\\test\\2minScrape\\brziot.py";

        new Thread(()->{
            ScraperScriptRunner.startScript(scriptPath);
            try {
                runIndicatorScripts();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        try {
            String scrapeUrl="http://localhost:4000/scrap";
            RestTemplate restTemplate=new RestTemplate();
            ResponseEntity<String> response=restTemplate.postForEntity(scrapeUrl,null,String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody()!=null){
                return ResponseEntity.ok(response.getBody());
            }else {
                return ResponseEntity.status(response.getStatusCode()).body("Error during scraping");
            }

        }catch (Exception e){
            return ResponseEntity.status(500).body("Error trigerring scraping:"+e.getMessage());
        }

    }

    private void runIndicatorScripts() throws IOException{
        String [] paths={
                "D:\\Fakultet\\3Ta godina\\Dizajn i arhitektura na softver\\3to Domasno\\test\\indicator\\weeklyOscToDB.py",
                "D:\\Fakultet\\3Ta godina\\Dizajn i arhitektura na softver\\3to Domasno\\test\\indicator\\weeklyMovAvgToDB.py",
                "D:\\Fakultet\\3Ta godina\\Dizajn i arhitektura na softver\\3to Domasno\\test\\indicator\\monthlyOscillatorsToDB.py",
                "D:\\Fakultet\\3Ta godina\\Dizajn i arhitektura na softver\\3to Domasno\\test\\indicator\\monthlyMovAvgToDB.py"
        };

        for (String script:paths){
            ProcessBuilder processBuilder=new ProcessBuilder("python",script);
            processBuilder.start();
            System.out.println("Running script: "+script);
        }
    }

    @GetMapping("/symbols")
    public List<String> getSymbols(){
        String folder_path="D:\\Fakultet\\3Ta godina\\Dizajn i arhitektura na softver\\final_3toDomasno\\StocksDataWebApp-master\\Fetched_Files";
        File folder=new File(folder_path);
        File [] files=folder.listFiles();
        List<String> symbols=new ArrayList<>();

        if(files!=null){
            for(File f:files){
                if(f.getName().endsWith(".csv")){
                    String fileName=f.getName();

                    String symbol=fileName.replace("_stock_data.csv","");
                    symbols.add(symbol);
                }
            }
        }
        return symbols;
    }

    @GetMapping("/submit")
    public List<String> getAllIndicators(){
        return new ArrayList<>();
    }

    @GetMapping
    public ResponseEntity<List<Stock>> getAllStockData() {
        List<Stock> stocks = stockService.findAll();
        return ResponseEntity.ok(stocks);  // Returns 200 OK with the list of stocks
    }

    @PostMapping
    public ResponseEntity<Stock> createStockData(@RequestBody Stock stockData) {
        Stock createdStock = stockService.save(stockData);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStock);  // Returns 201 Created with the created stock
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stock> getStockDataById(@PathVariable Long id) {
        return stockService.findById(id)
                .map(stock -> ResponseEntity.ok(stock))  // Returns 200 OK with the stock
                .orElseGet(() -> ResponseEntity.notFound().build());  // Returns 404 Not Found if stock is not present
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockData(@PathVariable Long id) {
        if (stockService.existsById(id)) {
            stockService.deleteById(id);
            return ResponseEntity.noContent().build();  // Returns 204 No Content for successful deletion
        }
        return ResponseEntity.notFound().build();  // Returns 404 Not Found if the stock doesn't exist
    }

    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<List<Stock>> getStockDataBySymbol(@PathVariable String symbol) {
        List<Stock> stocks = stockService.findAllBySymbol(symbol);

        if (stocks.isEmpty()) {
            return ResponseEntity.notFound().build();  // Returns 404 Not Found if no stocks are found with the symbol
        }

        return ResponseEntity.ok(stocks);  // Returns 200 OK with the list of stocks
    }
}
