package edu.ustc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminDTO {
    @JsonProperty("name")
    private String username;
    private String avatar;

    public AdminDTO() {
    }

    public AdminDTO(String username, String avator) {
        this.username = username;
        this.avatar = avator;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}
