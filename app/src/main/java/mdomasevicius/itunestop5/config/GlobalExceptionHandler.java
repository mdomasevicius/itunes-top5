package mdomasevicius.itunestop5.config;

import mdomasevicius.itunestop5.common.GenericBadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Exception handler, to make error messages more pretty and less leaky of internals
 */
@ControllerAdvice
class GlobalExceptionHandler {
    public static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    ErrorResponse badRequest(GenericBadRequestException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    ErrorResponse fallback(Exception e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Internal error");
    }

    static class ErrorResponse {
        public final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

    }

}
