package exception;

public class InvalidSectionFileException extends RuntimeException {
    public InvalidSectionFileException(String msg) {
        super(msg);
    }
}