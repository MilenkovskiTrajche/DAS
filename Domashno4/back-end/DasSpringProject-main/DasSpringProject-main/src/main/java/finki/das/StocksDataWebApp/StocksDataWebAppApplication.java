package finki.das.StocksDataWebApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StocksDataWebAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(StocksDataWebAppApplication.class, args);
	}

}
