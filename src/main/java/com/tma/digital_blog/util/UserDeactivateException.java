package com.tma.digital_blog.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)

public class UserDeactivateException extends RuntimeException {
    public UserDeactivateException() {
        super();
    }

    public UserDeactivateException(final String message) {
        super(message);
    }
}
