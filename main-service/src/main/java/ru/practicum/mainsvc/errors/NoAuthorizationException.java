package ru.practicum.mainsvc.errors;

public class NoAuthorizationException extends RuntimeException {
    public NoAuthorizationException(String message) {
        super(message);
    }
}
