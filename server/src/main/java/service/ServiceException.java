package service;

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
