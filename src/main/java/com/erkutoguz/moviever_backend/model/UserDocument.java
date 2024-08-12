package com.erkutoguz.moviever_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.Set;

@Document(indexName = "users")
@Setting(settingPath = "static/es-user-settings.json")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "edge_ngram_analyzer")
    private String username;

    @Field(type = FieldType.Text, analyzer = "edge_ngram_analyzer")
    private String email;

    @Field(type = FieldType.Text, analyzer = "edge_ngram_analyzer")
    private String firstName;

    @Field(type = FieldType.Text, analyzer = "edge_ngram_analyzer")
    private String lastName;

    private boolean enabled;

    private Set<Role> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
