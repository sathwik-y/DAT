package employee.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BetApplication {


	public static void main(String[] args) {
		SpringApplication.run(BetApplication.class, args);
	}

}
