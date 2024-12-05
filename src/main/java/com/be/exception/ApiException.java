package com.be.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Setter
@Getter
public class ApiException extends RuntimeException{
    private HttpStatus httpStatus;
    private String message;
    public ApiException() {
        super();
    }

    public ApiException(String message) {
        super(message);
    }
}
