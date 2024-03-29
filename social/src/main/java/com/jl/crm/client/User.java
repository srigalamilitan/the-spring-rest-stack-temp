package com.jl.crm.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private String firstName, lastName, username;
    private String profilePhotoMediaType;
    private long id;
    private boolean profilePhotoImported;
    private Date signupDate;

    public User(String firstName, String lastName, String username, String profilePhotoMediaType, long id, boolean profilePhotoImported, Date signupDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.profilePhotoMediaType = profilePhotoMediaType;
        this.id = id;
        this.profilePhotoImported = profilePhotoImported;
        this.signupDate = signupDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public MediaType getProfilePhotoMediaType() {
        return StringUtils.hasText(profilePhotoMediaType) ? MediaType.parseMediaType(profilePhotoMediaType) : null;
    }

    public void setProfilePhotoMediaType(String profilePhotoMediaType) {
        this.profilePhotoMediaType = profilePhotoMediaType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isProfilePhotoImported() {
        return profilePhotoImported;
    }

    public void setProfilePhotoImported(boolean profilePhotoImported) {
        this.profilePhotoImported = profilePhotoImported;
    }

    public Date getSignupDate() {
        return signupDate;
    }

    public void setSignupDate(Date signupDate) {
        this.signupDate = signupDate;
    }

    @Override
    public String toString() {
        return "User{" + "firstName='" + firstName + '\'' + ", username='" + username
            + '\'' + ", lastName='" + lastName + '\'' + '}';
    }

}
