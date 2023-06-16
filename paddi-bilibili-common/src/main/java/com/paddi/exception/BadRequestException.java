package com.paddi.exception;

import com.paddi.constants.HttpStatus;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 16:53:10
 */
public class BadRequestException extends RuntimeException{
    private static final long serialVersionUID = -414757700436827698L;

    private int code;
    public BadRequestException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BadRequestException(String message) {
        super(message);
        this.code = HttpStatus.BAD_REQUEST;
    }

    public int getCode() {
        return code;
    }
}
