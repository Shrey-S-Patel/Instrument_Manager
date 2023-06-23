package com.bdo.shrey.instrumentmanager.Models;

public class Instrument {
    private String id, category, location, status, img, notes, assigned;

    public Instrument() {
    }

    public Instrument(String id, String category, String location, String status, String img, String notes, String assigned) {
        this.id = id;
        this.category = category;
        this.location = location;
        this.status = status;
        this.img = img;
        this.notes = notes;
        this.assigned = assigned;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAssigned() {
        return assigned;
    }

    public void setAssigned(String assigned) {
        this.assigned = assigned;
    }
}
