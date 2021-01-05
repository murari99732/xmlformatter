package xmlformatter.com.xml.formatter;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FileNotfound extends RuntimeException {
    public FileNotfound(String message) {
        super(message);
    }

    public FileNotfound(String message, Throwable cause) {
        super(message, cause);
    }
}