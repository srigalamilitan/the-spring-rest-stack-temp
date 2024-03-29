package com.jl.crm.services.exceptions;

import com.jl.crm.services.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
public class UserProfilePhotoWriteException extends  UserException {

    private static final long serialVersionUID = 1L;

    public UserProfilePhotoWriteException(User user, Throwable cause) {
        super(user, cause);
    }

    public UserProfilePhotoWriteException(long userId, Throwable cause) {
        super(userId, cause);
    }

    public UserProfilePhotoWriteException(User user) {
        super(user);
    }

    public UserProfilePhotoWriteException(long userId) {
        super(userId);
    }

}
