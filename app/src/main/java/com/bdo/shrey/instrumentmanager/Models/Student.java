package com.bdo.shrey.instrumentmanager.Models;

public class Student {
    private String id, name, location, assigned;

    public Student() {
    }

    public Student(String id, String name, String location, String assigned) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.assigned = assigned;
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
}
