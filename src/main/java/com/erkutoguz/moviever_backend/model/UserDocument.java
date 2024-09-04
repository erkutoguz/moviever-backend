package com.erkutoguz.moviever_backend.model;

import java.util.List;

public class UserDocument {

    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private List<String> roles;
    private Long id;
    private boolean enabled;

    public UserDocument() {
    }

    public UserDocument(String email, String firstName, String lastName,
                        String username, List<String> roles, Long id, boolean enabled) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.roles = roles;
        this.id = id;
        this.enabled = enabled;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public Long getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
