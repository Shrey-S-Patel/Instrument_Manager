package com.bdo.shrey.instrumentmanager.Models;

public class Student {
    private String id, name, location, assigned, status, current;

    public Student() {
    }

    public Student(String id, String name, String location, String assigned, String status, String current) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.assigned = assigned;
        this.status = status;
        this.current = current;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }
}
