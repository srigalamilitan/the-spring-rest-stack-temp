package com.jl.crm.client;

import org.springframework.http.MediaType;

/**
 * Created by eric on 7/6/15.
 */
public class ProfilePhoto {

    private final MediaType mediaType;
    private final byte[] bytes;

    public ProfilePhoto(MediaType mediaType, byte[] bytes) {
        this.mediaType = mediaType;
        this.bytes = bytes;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public byte[] getBytes() {
        return bytes;
    }

}

