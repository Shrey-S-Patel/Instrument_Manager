package com.bdo.shrey.instrumentmanager.Models;

public class Deletes {
    private String id, category, location, status, img, d_user;

    public Deletes() {
    }

    public Deletes(String id, String category, String location, String status, String img, String d_user) {
        this.id = id;
        this.category = category;
        this.location = location;
        this.status = status;
        this.img = img;
        this.d_user = d_user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getD_user() {
        return d_user;
    }

    public void setD_user(String d_user) {
        this.d_user = d_user;
    }
}
