package ru.yandex.practicum.filmorate.exception;

public class NotFoundForDeleteException extends RuntimeException {
    public NotFoundForDeleteException(String message) {
        super(message);
    }
}