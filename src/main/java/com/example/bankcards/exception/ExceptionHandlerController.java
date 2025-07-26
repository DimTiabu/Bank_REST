package com.example.bankcards.exception;

import com.example.bankcards.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@ResponseStatus(HttpStatus.BAD_REQUEST)
@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(CardNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFound(
            CardNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFound(
            UserNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage());
    }

    @ExceptionHandler(StatusAlreadySetException.class)
    @ResponseStatus(HttpStatus.CONFLICT )
    public ErrorResponse notFound(
            StatusAlreadySetException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponse(
                HttpStatus.CONFLICT .value(),
                ex.getMessage());
    }


}