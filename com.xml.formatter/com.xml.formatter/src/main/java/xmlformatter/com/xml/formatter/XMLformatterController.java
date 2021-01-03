package xmlformatter.com.xml.formatter;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableConfigurationProperties({
	PropertiesStorage.class
})
public class XMLformatterController {
	


}
