package fr.syl.controller;

import fr.syl.exception.FormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * class to manage exceptions thrown in the endpoints
 */
@ControllerAdvice

public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {
    private static Logger LOG = LoggerFactory.getLogger(RestResponseExceptionHandler.class);

    @ExceptionHandler(value = { FormatException.class })
    private ResponseEntity<Object> handleFormatException(FormatException ex, WebRequest request) {
        return getHttpError(ex, request, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * catch everything else to not expose anything, like failing SQL requests if it happens.
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(value = { Exception.class })
    private ResponseEntity<Object> handleOtherException(Exception ex, WebRequest request) {
        return getHttpError(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    private ResponseEntity<Object> getHttpError(Exception ex, WebRequest request, HttpStatus status, String message) {
        LOG.error(ex.getMessage(), ex);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return handleExceptionInternal(ex, message, headers, status, request);
    }
}
