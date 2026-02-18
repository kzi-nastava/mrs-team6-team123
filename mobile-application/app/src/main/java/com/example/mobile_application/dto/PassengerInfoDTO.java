
package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class PassengerInfoDTO implements Serializable {
    @SerializedName("id")
    private Long id;
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("profileImage")
    private String profileImage;

    public PassengerInfoDTO() {}

    public Long getId() { return id; }
    public void setId(Long v) { this.id = v; }
    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String v) { this.profileImage = v; }
}