package com.be.exception;


import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(value={ApiException.class})
    public ResponseEntity<Object> handlerUserServiceException(ApiException ex, WebRequest request){
        return  new ResponseEntity<>(ex.getMessage(),new HttpHeaders(), ex.getHttpStatus());
    }

}
