package mdomasevicius.itunestop5.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GenericBadRequestException extends RuntimeException {
    public GenericBadRequestException(String message) {
        super(message);
    }

    public GenericBadRequestException(Throwable cause) {
        super(cause);
    }
}
