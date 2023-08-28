package ru.practicum.shareit;

import org.junit.jupiter.api.Test;

import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.StateIsNotSupportException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerTest {

    @Test
    void handleStateIsNotSupportException() {
        ErrorHandler errorHandler = new  ErrorHandler();
        StateIsNotSupportException e = new StateIsNotSupportException("test");
        assertEquals("test", errorHandler.handleStateIsNotSupportException(e).getError());
    }
}