package ru.practicum.shareit.converter;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.exception.UnsupportedStateException;

public class StateConverter {
    public static State toState(String stateByString) {
        State state;
        try {
            state = State.valueOf(stateByString);
        } catch (IllegalArgumentException exception) {
            throw new UnsupportedStateException("Unknown state: " + stateByString);
        }
        return state;
    }
}
