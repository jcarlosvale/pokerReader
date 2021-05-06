package exception;

public class InvalidPlayerException extends RuntimeException {
    public InvalidPlayerException(String msg) {
        super(msg);
    }
}
