package com.bdo.shrey.instrumentmanager.Models;

public class SDeletes {
    private String id, name, location, assigned, d_user;

    public SDeletes() {
    }

    public SDeletes(String id, String name, String location, String assigned, String d_user) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.assigned = assigned;
        this.d_user = d_user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAssigned() {
        return assigned;
    }

    public void setAssigned(String assigned) {
        this.assigned = assigned;
    }

    public String getD_user() {
        return d_user;
    }

    public void setD_user(String d_user) {
        this.d_user = d_user;
    }
}
