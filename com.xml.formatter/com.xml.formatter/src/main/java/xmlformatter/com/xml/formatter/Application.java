package xmlformatter.com.xml.formatter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import xmlformatter.com.xml.formatter.FileValue.PropertiesFile;

@SpringBootApplication
@EnableConfigurationProperties({ PropertiesFile.class })
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
