package xmlformatter.com.xml.formatter;

public class ExceptionValue extends RuntimeException {
    public ExceptionValue(String message) {
        super(message);
    }

    public ExceptionValue(String message, Throwable cause) {
        super(message, cause);
    }
}
