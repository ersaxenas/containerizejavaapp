package com.lrn.containerize.model;

public class UserDetails {
    private String userId;
    private String userName;
    private String phoneNo;
    private String address;
    private String email;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserDetails withUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public UserDetails withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public UserDetails withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserDetails withAddress(String address) {
        this.address = address;
        return this;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "UserDetails{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
