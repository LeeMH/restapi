package me.jigulsw.api.exception;

import lombok.Data;

@Data
public class ApiException extends RuntimeException {
    private String status;
    private String mesage;

    public ApiException() { super(); }

    public ApiException(String message) {
        super(message);
        this.status = "error";
    }

    public ApiException(String status, String message) {
        super(message);
        this.status = status;
    }

}
