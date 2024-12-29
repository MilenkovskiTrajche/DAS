package finki.das.StocksDataWebApp.service;

import finki.das.StocksDataWebApp.model.Stock;
import finki.das.StocksDataWebApp.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvFileService {

    private final StockRepository stockRepository;

    public CsvFileService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional
    public void saveAllFromFolder(String folderPath) throws IOException {
        Path folder = Paths.get(folderPath);
        if (!Files.exists(folder) || !Files.isDirectory(folder)) {
            throw new IllegalArgumentException("Invalid folder path: " + folderPath);
        }

        Files.list(folder).forEach(file -> {
            if (file.toString().endsWith(".csv")) {
                try {
                    saveCsvFile(file.toFile());
                } catch (Exception e) {
                    System.err.println("Error processing file: " + file.getFileName());
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveCsvFile(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            List<Stock> stockList = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy");

            // Extract symbol from the file name (e.g., "ADIN_stock_data.csv" -> "ADIN")
            String fileName = file.getName();
            String symbol = fileName.substring(0, fileName.indexOf('_'));

            // Reset skipHeader for each file
//            boolean skipHeader = true;

            while ((line = reader.readLine()) != null) {
//                if (skipHeader) {
//                    skipHeader = false; // Skip the first row (header)
//                    continue;
//                }

                String[] fields = line.split(",");
                Stock stock = new Stock();

                // Parse the date
                String dateString = fields[0].replace("\"", "").trim();
                stock.setDate(LocalDate.parse(dateString, formatter));

                // Use safe parsing for double fields
                stock.setLastTransaction(parseDouble(fields[1]));
                stock.setMaks(parseDouble(fields[2]));
                stock.setMin(parseDouble(fields[3]));
                stock.setAveragePrice(parseDouble(fields[4]));
                stock.setPromPercent(parseDouble(fields[5]));
                stock.setQuantity(Long.parseLong(fields[6].replace("\"", "")));
                stock.setRevenueBEST(Long.parseLong(fields[7].replace("\"", "")));
                stock.setTotalRevenue(Long.parseLong(fields[8].replace("\"", "")));

                // Set the symbol from the file name
                stock.setSymbol(symbol);

                stockList.add(stock);
            }

            stockRepository.saveAll(stockList);
        }
    }


    // Helper method to safely parse a double
    private double parseDouble(String value) {
        try {
            // If the value is empty or invalid, return 0.0 as the default
            return value.isEmpty() || value.equals("\"") ? 0.0 : Double.parseDouble(value.replace("\"", "").trim());
        } catch (NumberFormatException e) {
            // Handle invalid format gracefully by returning 0.0 or logging the error
            System.err.println("Invalid number format: " + value);
            return 0.0;  // Return default value if parsing fails
        }
    }

}
