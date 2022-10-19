package com.playtika.finalproject.controllers;

import com.playtika.finalproject.models.exceptions.NotAValidEmailException;
import com.playtika.finalproject.models.exceptions.NotAValidPasswordException;
import com.playtika.finalproject.models.exceptions.NotAValidUsernameException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionControllerHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {NotAValidPasswordException.class, NotAValidEmailException.class, NotAValidUsernameException.class})
    protected ResponseEntity<Object> handleConflict(RuntimeException exception, WebRequest request) {
        String bodyOfResponse  = exception.getMessage();
        return handleExceptionInternal(exception,bodyOfResponse,new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleConflict(Exception exception, WebRequest request) {
        String bodyOfResponse  = exception.getMessage();
        return handleExceptionInternal(exception,bodyOfResponse,new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
