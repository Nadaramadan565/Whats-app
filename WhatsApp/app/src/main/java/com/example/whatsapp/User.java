package com.example.whatsapp;

public class User {
    String name,statues,image;
    String id;
    User(){


    }

    public User(String name, String statues, String image , String id) {
        this.name = name;
        this.statues = statues;
        this.image = image;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatues() {
        return statues;
    }

    public void setStatues(String statues) {
        this.statues = statues;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
