package me.jigulsw.api.controller;

import me.jigulsw.api.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class BaseController {
    protected static final String APPLICATION_JSON = "application/json; charset=UTF-8";
    protected static final String MULTIPART = "multipart/form-data;";

    public HttpServletRequest getServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }

        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    public long getUserId() {
        HttpServletRequest servletRequest = getServletRequest();
        if (servletRequest == null) return -1;
        Object userId = servletRequest.getAttribute("userId");

        return userId != null ? (long) userId : -1;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ApiResponse processValidationError(MethodArgumentNotValidException ex) {
        FieldError fieldError = (FieldError) ex.getBindingResult().getFieldErrors().get(0);
        return ApiResponse.error(fieldError.getDefaultMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({AuthenticationException.class})
    public ApiResponse handleAuthException(Exception e) {
        log.error("ApiException ::: {}", e.getMessage());
        e.printStackTrace();
        return ApiResponse.error("권한이 없거나, 인증에 실패 했습니다.");
    }

    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    @ExceptionHandler({ApiException.class})
    public ApiResponse handleApiException(Exception e) {
        log.error("ApiException ::: {}", e.getMessage());
        e.printStackTrace();
        return ApiResponse.error(e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public ApiResponse handleException(Exception e) {
        log.error("Exception ::: {}", e.getMessage());
        e.printStackTrace();
        return ApiResponse.error(e.getMessage());
    }

}
