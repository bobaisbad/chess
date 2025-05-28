package exceptions;

public class UnauthorizedException extends ParentException {
    public UnauthorizedException(String message, int statusCode) {
        super(message, statusCode);
    }
}
