package me.jigulsw.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.jigulsw.api.domain.code.ResponseStatus;

public class ApiResponse<T> {
    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("redirect")
    private String redirect = "";

    @JsonProperty("data")
    private T t;

    public ApiResponse(ResponseStatus status) {
        this.status = status.getDescEng();
    }

    public ApiResponse(ResponseStatus status, String message) {
        this.status = status.getDescEng();
        this.message = message;
    }

    public ApiResponse(ResponseStatus status, String message, T t) {
        this.status = status.getDescEng();
        this.message = message;
        this.t = t;
    }

    public static ApiResponse ok() {
        return new ApiResponse(ResponseStatus.SUCCESS, "ok");
    }

    public static ApiResponse ok(Object object) {
        return new ApiResponse(ResponseStatus.SUCCESS, "ok", object);
    }

    public static ApiResponse error(String message) {
        return new ApiResponse(ResponseStatus.FAILURE, message);
    }

}
