package com.example.poster.retrofitpbapi.models;

/**
 * Created by POSTER on 31.05.2017.
 */

public class PrivatOtdelModel {
    private String name;
    private String state;
    private int id;
    private String country;
    private String city;
    private int index;
    private String phone;
    private  String email;
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "PrivatOtdelModel{" +
                "name='" + name + '\'' +
                ", state='" + state + '\'' +
                ", id=" + id +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", index=" + index +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
