package com.example.test.ds;

import java.io.Serializable;

public class Company extends User implements Serializable {
    private String title;
    private String contactPerson;

    public Company(String login, String password, String title, String contactPerson) {
        super(login, password);
        this.title = title;
        this.contactPerson = contactPerson;
    }

    public Company() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    @Override
    public String toString() {
        return super.toString() + " title = " + title +
                ", contactPerson = " + contactPerson;
    }
}