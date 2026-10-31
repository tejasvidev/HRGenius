package com.hrgenius.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(AuthenticationException.class)
    ProblemDetail authenticationFailure(AuthenticationException exception) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Authentication failed");
        detail.setType(URI.create("https://hrgenius.local/problems/authentication-failed"));
        detail.setTitle("Authentication failed");
        return detail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validationFailure(MethodArgumentNotValidException exception) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed");
        detail.setType(URI.create("https://hrgenius.local/problems/validation-failed"));
        detail.setTitle("Request validation failed");
        detail.setProperty("fieldErrors", exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldError(error.getField(), error.getDefaultMessage())).toList());
        return detail;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail notFound(ResourceNotFoundException exception) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        detail.setType(URI.create("https://hrgenius.local/problems/not-found"));
        detail.setTitle("Resource not found");
        return detail;
    }

    @ExceptionHandler({ConflictException.class, DataIntegrityViolationException.class})
    ProblemDetail conflict(RuntimeException exception) {
        String message = exception instanceof ConflictException ? exception.getMessage() : "The request conflicts with an existing record";
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, message);
        detail.setType(URI.create("https://hrgenius.local/problems/conflict"));
        detail.setTitle("Request conflict");
        return detail;
    }

    private record FieldError(String field, String message) { }
}
