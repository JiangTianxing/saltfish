package org.redrock.saltfish.common.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class RequestException extends Exception{

    private HttpStatus httpStatus;

    private String msg;

    public RequestException(HttpStatus httpStatus, String msg) {
        this.httpStatus = httpStatus;
        this.msg = msg;
    }
}