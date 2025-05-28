package exceptions;

public class TakenException extends ParentException {
    public TakenException(String message, int statusCode) {
        super(message, statusCode);
    }
}
