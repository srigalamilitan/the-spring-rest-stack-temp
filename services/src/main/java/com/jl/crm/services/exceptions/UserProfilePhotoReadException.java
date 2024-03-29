package com.jl.crm.services.exceptions;

import com.jl.crm.services.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserProfilePhotoReadException extends UserException {

    private static final long serialVersionUID = 1L;

    public UserProfilePhotoReadException(User user, Throwable cause) {
        super(user, cause);
    }

    public UserProfilePhotoReadException(long userId, Throwable cause) {
        super(userId, cause);
    }

    public UserProfilePhotoReadException(User user) {
        super(user);
    }

    public UserProfilePhotoReadException(long userId) {
        super(userId);
    }

}
