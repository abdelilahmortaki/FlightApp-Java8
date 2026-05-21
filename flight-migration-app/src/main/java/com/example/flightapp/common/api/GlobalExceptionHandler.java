package com.example.flightapp.common.api;

import com.example.flightapp.common.domain.BusinessRuleException;
import java.time.LocalDateTime;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiErrorResponse> businessRule(BusinessRuleException exception) {
        ApiErrorResponse response = new ApiErrorResponse(
            "BUSINESS_RULE_ERROR",
            exception.getMessage(),
            LocalDateTime.now()
        );
        return new ResponseEntity<ApiErrorResponse>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> validation(MethodArgumentNotValidException exception) {
        ApiErrorResponse response = new ApiErrorResponse(
            "VALIDATION_ERROR",
            "Request validation failed",
            LocalDateTime.now()
        );
        return new ResponseEntity<ApiErrorResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> dataIntegrity(DataIntegrityViolationException exception) {
        ApiErrorResponse response = new ApiErrorResponse(
            "BUSINESS_RULE_ERROR",
            "Duplicate or invalid persisted data",
            LocalDateTime.now()
        );
        return new ResponseEntity<ApiErrorResponse>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> illegalArgument(IllegalArgumentException exception) {
        ApiErrorResponse response = new ApiErrorResponse(
            "VALIDATION_ERROR",
            exception.getMessage(),
            LocalDateTime.now()
        );
        return new ResponseEntity<ApiErrorResponse>(response, HttpStatus.BAD_REQUEST);
    }
}
