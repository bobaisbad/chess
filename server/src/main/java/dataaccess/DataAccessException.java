package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends ParentException{
    public DataAccessException(String message, int statusCode) {
        super(message, statusCode);
    }
}
