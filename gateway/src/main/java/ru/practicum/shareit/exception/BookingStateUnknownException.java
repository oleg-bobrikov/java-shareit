package ru.practicum.shareit.exception;

public class BookingStateUnknownException extends RuntimeException{
    public BookingStateUnknownException(String message) {
        super(message);
    }
}
