package com.example.hcart.Model;

public class UserModel {

    String name;
    String email;
    String uid;
    String adhaar;
    String gender;
    String instagram;
    String position;
    String dp_link;
    String number;

    public UserModel() {
    }

    public UserModel(String name, String email, String uid, String adhaar, String gender, String instagram, String position, String dp_link, String number) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.adhaar = adhaar;
        this.gender = gender;
        this.instagram = instagram;
        this.position = position;
        this.dp_link = dp_link;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public String getAdhaar() {
        return adhaar;
    }

    public String getGender() {
        return gender;
    }

    public String getInstagram() {
        return instagram;
    }

    public String getPosition() {
        return position;
    }

    public String getDp_link() {
        return dp_link;
    }

    public String getNumber() {
        return number;
    }
}