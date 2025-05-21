package dataaccess;

public class ParentException extends Exception {
    private final int statusCode;

    public ParentException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int StatusCode() {
        return statusCode;
    }
}
