package com.jl.crm.services;

import org.springframework.http.MediaType;

/**
 * Created by eric on 7/6/15.
 */
public class ProfilePhoto {

    private Long userId;
    private byte[] photo;
    private MediaType mediaType;

    public ProfilePhoto(Long userId, byte[] photo, MediaType mediaType) {
        this.userId = userId;
        this.photo = photo;
        this.mediaType = mediaType;
    }

    public Long getUserId() {
        return userId;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public MediaType getMediaType() {
        return mediaType;
    }
}
