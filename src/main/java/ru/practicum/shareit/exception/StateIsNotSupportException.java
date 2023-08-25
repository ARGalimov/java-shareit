package ru.practicum.shareit.exception;

public class StateIsNotSupportException extends RuntimeException {
    public StateIsNotSupportException(String message) {
        super(message);
    }
}
