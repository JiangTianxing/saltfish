package org.redrock.wechatcore.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class WechatException extends Exception{

    private HttpStatus httpStatus;

    private String msg;

    public WechatException(HttpStatus httpStatus, String msg) {
        this.httpStatus = httpStatus;
        this.msg = msg;
    }
}
