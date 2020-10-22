package com.example.myapplication;

public class GetHelper {
    String name;
    String gender;
    String phone_number;
    String image_url;

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public GetHelper(String name, String gender, String phone_number, String image_url) {
        this.name = name;
        this.gender=gender;
        this.phone_number=phone_number;
        this.image_url=image_url;
    }
    public GetHelper() {
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setName(String name) {
        this.name = name;
    }
}
