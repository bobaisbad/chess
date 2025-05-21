package dataaccess;

public class BadRequestException extends ParentException {
    public BadRequestException(String message, int statusCode) {
        super(message, statusCode);
    }
}
