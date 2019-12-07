package com.example.test.ds;

import java.io.Serializable;

public class Person extends User implements Serializable {
    private String name;
    private String lastName;

    public Person(String login, String password, String name, String lastname) {
        super(login, password);
        this.name = name;
        this.lastName = lastname;
    }

    public Person(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastName;
    }

    public void setLastname(String lastname) {
        this.lastName = lastname;
    }

    @Override
    public String toString() {
        return super.toString() + " name = " + name +
                ", lastName = " + lastName;
    }
}
