package server;

/**
 * Container for an error message to accompany non-200 HTTP returns.
 */
public record ErrorResult(String message) {}
