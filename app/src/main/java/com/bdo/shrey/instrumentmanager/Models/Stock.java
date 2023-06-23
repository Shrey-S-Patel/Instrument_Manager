package com.bdo.shrey.instrumentmanager.Models;

public class Stock {
    private String id, category, location, status, img, checked;

    public Stock() {
    }

    public Stock(String id, String category, String location, String status, String img, String checked) {
        this.id = id;
        this.category = category;
        this.location = location;
        this.status = status;
        this.img = img;
        this.checked = checked;
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

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }
}
