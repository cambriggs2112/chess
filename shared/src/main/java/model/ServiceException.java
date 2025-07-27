package model;

/**
 * Indicates there was an error in a service that warrants a non-200 HTTP code
 */
public class ServiceException extends RuntimeException {
    private int httpCode;

    public ServiceException(String message, int httpCode) {
        super(message);
        this.httpCode = httpCode;
    }

    public int getHTTPCode() {
        return httpCode;
    }
}
