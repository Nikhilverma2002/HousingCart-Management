package com.example.hcart.Model;

public class DetailsModel {

    String name;
    String email;
    String uid;
    String date;
    String in_time;
    String out_time;
    String in_str;
    String out_str;


    public DetailsModel() {
    }

    public DetailsModel(String name, String in_time, String out_time, String in_str, String out_str) {
        this.name = name;
        this.in_time = in_time;
        this.out_time = out_time;
        this.in_str = in_str;
        this.out_str = out_str;
    }

    public String getIn_str() {
        return in_str;
    }

    public void setIn_str(String in_str) {
        this.in_str = in_str;
    }

    public String getOut_str() {
        return out_str;
    }

    public void setOut_str(String out_str) {
        this.out_str = out_str;
    }

    public String getIn_time() {
        return in_time;
    }

    public String getOut_time() {
        return out_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setIn_time(String in_time) {
        this.in_time = in_time;
    }

    public void setOut_time(String out_time) {
        this.out_time = out_time;
    }
}
