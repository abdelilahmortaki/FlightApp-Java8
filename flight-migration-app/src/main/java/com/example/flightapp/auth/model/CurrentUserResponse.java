package com.example.flightapp.auth.model;

import java.util.ArrayList;
import java.util.List;

public class CurrentUserResponse {

    private String username;
    private List<String> roles = new ArrayList<String>();

    public CurrentUserResponse() {
    }

    public CurrentUserResponse(String username, List<String> roles) {
        this.username = username;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
