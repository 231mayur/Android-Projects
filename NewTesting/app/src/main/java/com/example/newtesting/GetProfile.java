package com.example.newtesting;

public class GetProfile {
    private String name;
    private String mobile;
    GetProfile()
    {

    }
    GetProfile(String name,String mobile)
    {
        this.name=name;
        this.mobile=mobile;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }



}
