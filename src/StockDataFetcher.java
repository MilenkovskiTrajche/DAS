import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.ParseException;
import java.time.format.DateTimeParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class StockDataFetcher {

    private static final String BASE_URL = "https://www.mse.mk/mk/stats/symbolhistory/";
    private static final String FOLDER_NAME = "Fetched_Files";
    private static final String DATA_FILE = "_stock_data.csv"; // Store data in a CSV file


    public static class DropdownExtractor {
        public static List<String> getDropdownValues() throws IOException {
            List<String> values = new ArrayList<>(); // Create a list to store option values
            Document doc = Jsoup.connect(BASE_URL + "STB").get();
            Element dropdown = doc.getElementById("Code");

            if (dropdown != null) {
                Elements options = dropdown.getElementsByTag("option");
                for (Element option : options) {
                    String value = option.attr("value");
                    if (!value.matches(".*\\d.*")) { // Check if value does not contain any numbers
                        values.add(value); // Add each option value without numbers to the list
                    }
                }
            }
            return values; // Return the list of values without numbers
        }
    }



    public void fetchHistoricalData(String symbol, String fromDate, String toDate) {

        String firstDateFromWeb = getFirstDateFromWeb(symbol);
        String lastSavedDate = "0" + getLastSavedDate(symbol);

        // If the first date from the website matches the last saved date, skip scraping
        if (firstDateFromWeb != null && firstDateFromWeb.equals(lastSavedDate)) {
            System.out.println("NO new data for " + symbol + " (same date: " + firstDateFromWeb + ")");
            return; // Skip scraping if the dates are the same
        }
        // Set up Selenium WebDriver
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);
        try {
            String url = BASE_URL + symbol;
            driver.get(url);

            // Use WebDriverWait instead of Thread.sleep
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.pollingEvery(Duration.ofMillis(500));
            fillDateFields(wait, fromDate, toDate);
            selectStockCode(wait, symbol);

            // Click the "Прикажи" button
            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[type='submit'][value='Прикажи']")));
            submitButton.click();

            try {
                // Wait for the "no results" message to be visible
                WebElement noResultsMessage = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.panel-body.footnote div.row.no-results"))
                );
                if (noResultsMessage.isDisplayed()) {
                    System.out.println("No data available.");
                    saveWordByWordNoData(symbol,fromDate,toDate,"No data available");
                }
            } catch (Exception e) {
                // The "no results" message was not found, so the table should be visible now
                System.out.println("Data available, waiting for table to load.");
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table")));

                Set<String> uniqueData = new HashSet<>();
                fetchData(driver, uniqueData,symbol);
            }
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
        } finally {
            driver.quit();
        }
    }

    private String getFirstDateFromWeb(String symbol) {
        try {
            // Set up Selenium WebDriver to fetch the page
            System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            WebDriver driver = new ChromeDriver(options);
            String url = BASE_URL + symbol;
            driver.get(url);

            // Wait for the table to load
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table")));

            // Get the first row of the table (excluding the header)
            WebElement firstRow = driver.findElement(By.cssSelector("table tbody tr"));
            List<WebElement> cells = firstRow.findElements(By.tagName("td"));
            if (!cells.isEmpty()) {
                // The first column contains the date (e.g., 05.11.2024)
                String dateString = cells.getFirst().getText().trim();
                driver.quit();
                return dateString; // Return the date
            }
        } catch (Exception e) {
            System.out.println("Error fetching date from the website: " + e.getMessage());
        }
        return null; // Return null if the date could not be fetched
    }


    private String getLastSavedDate(String symbol) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d.M.yyyy"); // Adjust to your date format

        try (Stream<String> lines = Files.lines(Paths.get(FOLDER_NAME + "\\" + symbol + DATA_FILE))) {
            Optional<LocalDate> latestDate = lines
                    //.filter(line -> line.startsWith(symbol + ",")) // Filter lines for the specific symbol
                    .map(line -> line.split(",")[0].replace("\"", "").trim()) // Get the first column (date), remove quotes and trim whitespace
                    .map(date -> LocalDate.parse(date, dateFormatter)) // Parse the date string to LocalDate
                    .max(Comparator.naturalOrder()); // Get the most recent date

            return latestDate.map(dateFormatter::format).orElse(null); // Format back to String if a date was found
        } catch (IOException e) {
            System.out.println("No previous data found for " + symbol);
            return null;
        } catch (DateTimeParseException e) {
            System.out.println("Date parsing error for symbol " + symbol + ": " + e.getMessage());
            return null; // Handle parsing errors gracefully
        }
    }


    private void saveWordByWordNoData(String symbol, String fromdate,String toDate,String msg) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FOLDER_NAME + "\\" + symbol + DATA_FILE, true))) {
            writer.write("\"" + fromdate + "\"");//01.01.2014
            writer.write(",");//dr kolona
            writer.write("\"" + msg + "\"");//no data
            writer.newLine();//nov red

            writer.write("\"" + toDate + "\"");//31.12.2014
            writer.write(","); // dr kolona
            writer.write("\"" + msg + "\"");//no data
            writer.newLine();//nov red

            //writer.newLine();//nov red za nareden pat
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void saveWordByWord(String symbol, String[] words) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FOLDER_NAME + "\\" + symbol + DATA_FILE, true))) {
            for (int i = 0; i < words.length; i++) {
                String word = words[i].trim();
                writer.write("\"" + word + "\"");
                if (i < words.length - 1) {
                    writer.write(","); // Separate with a comma
                }
            }
            writer.newLine();

        } catch (IOException e) {
            System.out.println("Error occurred in saveWORDbyWord" +  e);
        }
    }

    private void fillDateFields(WebDriverWait wait, String fromDate, String toDate) {
        WebElement fromDateField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("FromDate")));
        fromDateField.clear();
        fromDateField.sendKeys(fromDate);

        WebElement toDateField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ToDate")));
        toDateField.clear();
        toDateField.sendKeys(toDate);
    }

    private void selectStockCode(WebDriverWait wait, String symbol) {
        WebElement codeDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Code")));
        codeDropdown.click();
        WebElement option = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//option[@value='" + symbol + "']")));
        option.click();
    }

    private void fetchData(WebDriver driver, Set<String> uniqueData, String symbol) throws InterruptedException {
        boolean hasMoreData = true;
        List<WebElement> previousRows = driver.findElements(By.cssSelector("table tbody tr"));
        while (hasMoreData) {
            Document doc = Jsoup.parse(driver.getPageSource());
            Elements rows = doc.select("table tbody tr");

            if (rows.isEmpty()) {
                System.out.println("No data found for the given date range.");
                break;
            }

            for (Element row : rows) {
                Elements tds = row.select("td");
                if (tds.size() >= 9) {
                    String entry = String.join("\t", tds.eachText());
                    if (uniqueData.add(entry)) {
                        String[] rowData = new String[tds.size()];
                        for (int i = 0; i < tds.size(); i++) {
                            rowData[i] = tds.get(i).text();
                        }
                        saveWordByWord(symbol, rowData);
                    }
                }
            }
            Thread.sleep(1000); // Adjust based on loading speed; consider removing if data loads instantly
            List<WebElement> newRows = driver.findElements(By.cssSelector("table tbody tr"));
            hasMoreData = newRows.size() > previousRows.size();
            previousRows = newRows;
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
            long startTime = System.currentTimeMillis();

            File folderFetcher = new File(FOLDER_NAME);
            if (!folderFetcher.exists()) {
                if(folderFetcher.mkdir()){
                    System.out.println("Folder created");
                }
            }
            StockDataFetcher fetcher = new StockDataFetcher();
            List<String> dropdownValues = DropdownExtractor.getDropdownValues();
            //Use ExecutorService for parallel processing
            ExecutorService executor = Executors.newFixedThreadPool(5); // Adjust pool size based on system capability
            for (String symbol : dropdownValues) {
                executor.submit(() -> {
                    String lastDate = fetcher.getLastSavedDate(symbol);
                    String fromDate;
                    SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                    String toDate = currentDateFormat.format(new Date()); // e.g., "05.11.2024"

                    // Use DateTimeFormatter to parse the date string
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    LocalDate localDate = LocalDate.parse(toDate, formatter);

                    int year=0, month=0, day=0;
                    int yearToDate = localDate.getYear(); // Extract the year
                    int monthToDate = localDate.getMonthValue();
                    int dayToDate = localDate.getDayOfMonth();

                    if (lastDate != null) {
                        System.out.println("Last date: " + lastDate + " for symbol: " + symbol);
                        SimpleDateFormat[] dateFormats = {
                                new SimpleDateFormat("dd.MM.yyyy"),
                                new SimpleDateFormat("d.MM.yyyy"),
                                new SimpleDateFormat("dd.M.yyyy"),
                                new SimpleDateFormat("d.M.yyyy")
                        };

                        Date parsedDate = null;

                        // Try parsing the 'lastDate' with different formats
                        for (SimpleDateFormat df : dateFormats) {
                            try {
                                parsedDate = df.parse(lastDate);
                                break;  // If parsing is successful, break out of the loop
                            } catch (ParseException e) {
                                // Ignore the exception and try the next format
                            }
                        }
                        // If parsed successfully, extract the day, month, and year
                        if (parsedDate != null) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(parsedDate);

                            day = calendar.get(Calendar.DAY_OF_MONTH);
                            month = calendar.get(Calendar.MONTH) + 1;  // Month is 0-based, so add 1
                            year = calendar.get(Calendar.YEAR);

                        } else {
                            System.out.println("Unable to parse the lastDate: " + lastDate);
                        }
                        if(yearToDate == year && monthToDate == month && dayToDate == day) {
                            System.out.println("ALL data fetched! - " + symbol);
                        }else{
                            if(yearToDate==year){
                                System.out.println("Fetching data from " + lastDate + " to " + toDate + " for " + symbol);
                                fetcher.fetchHistoricalData(symbol,lastDate,toDate);
                            }else {
                                for (int i = year; i <= yearToDate; i++) {
                                    String to_date = toDate + "." + month + "." + i; // Update to correct year
                                    fetcher.fetchHistoricalData(symbol, lastDate, to_date);
                                }
                            }
                        }
                    }
                    else {
                        for (int i = 10; i > 0; i--) {
                            int fromYear = 2024 - i;
                            fromDate = "01.01." + fromYear;
                            toDate = "31.12." + fromYear;
                            System.out.println("Fetching data from " + fromDate + " to " + toDate + " for " + symbol);
                            fetcher.fetchHistoricalData(symbol, fromDate, toDate);
                        }
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);


        long endTime = System.currentTimeMillis();
            long durationInMilliseconds = endTime - startTime;
            double durationInSeconds = durationInMilliseconds / 1000.0;
            double durationInMinutes = durationInMilliseconds / 60000.0;

            System.out.println("\nALL DONE\nTotal time taken: " + durationInMilliseconds + " milliseconds.");
            System.out.println("Total time taken: " + durationInSeconds + " seconds.");
            System.out.println("Total time taken: " + durationInMinutes + " minutes.");

    }

}
