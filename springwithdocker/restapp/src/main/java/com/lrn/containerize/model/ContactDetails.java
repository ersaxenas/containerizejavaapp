package com.lrn.containerize.model;

import java.sql.Date;

public class ContactDetails {
    private String contactId;
    private String userName;
    private String phoneNo;
    private String address;
    private String email;
    private Date createdDate;
    private Date updatedDate;

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
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

    public ContactDetails withUserId(String userId) {
        this.contactId = userId;
        return this;
    }

    public ContactDetails withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public ContactDetails withEmail(String email) {
        this.email = email;
        return this;
    }

    public ContactDetails withAddress(String address) {
        this.address = address;
        return this;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Override
    public String toString() {
        return "ContactDetails{" +
                "contactId='" + contactId + '\'' +
                ", userName='" + userName + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                '}';
    }
}
