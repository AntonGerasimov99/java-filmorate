package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "not found element")
public class NotFoundElementException extends RuntimeException {
}