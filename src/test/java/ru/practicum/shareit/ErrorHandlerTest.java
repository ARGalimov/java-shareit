package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.*;

import javax.persistence.PersistenceException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerTest {

    @Test
    void handleUserExistException() {
        ErrorHandler errorHandler = new ErrorHandler();
        UserExistException e = new UserExistException("test");
        assertEquals("test", errorHandler.handleUserExistException(e).getError());
    }

    @Test
    void handleNullObjectException() {
        ErrorHandler errorHandler = new ErrorHandler();
        NullObjectException e = new NullObjectException("test");
        assertEquals("test", errorHandler.handleNullObjectException(e).getError());
    }

    @Test
    void handleUNullPointerException() {
        ErrorHandler errorHandler = new ErrorHandler();
        NullPointerException e = new NullPointerException("test");
        assertEquals("test", errorHandler.handleNullPointerException(e).getError());
    }

    @Test
    void handleObjectNotFoundException() {
        ErrorHandler errorHandler = new ErrorHandler();
        ObjectNotFoundException e = new ObjectNotFoundException("test");
        assertEquals("test", errorHandler.handleObjectNotFoundException(e).getError());
    }

    @Test
    void handleStateIsNotSupportException() {
        ErrorHandler errorHandler = new ErrorHandler();
        StateIsNotSupportException e = new StateIsNotSupportException("test");
        assertEquals("test", errorHandler.handleStateIsNotSupportException(e).getError());
    }

    @Test
    void handleIllegalArgumentException() {
        ErrorHandler errorHandler = new ErrorHandler();
        IllegalArgumentException e = new IllegalArgumentException("test");
        assertEquals("test", errorHandler.handleIllegalArgumentException(e).getError());
    }

    @Test
    void handlePersistenceException() {
        ErrorHandler errorHandler = new ErrorHandler();
        PersistenceException e = new PersistenceException("test");
        assertEquals("test", errorHandler.handlePersistenceException(e).getError());
    }
}