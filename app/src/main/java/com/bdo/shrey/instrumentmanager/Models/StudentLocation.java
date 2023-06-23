package com.bdo.shrey.instrumentmanager.Models;

public class StudentLocation {
    private String location;
    private int count, code_count;

    public StudentLocation() {
    }

    public StudentLocation(String location, int count, int code_count) {
        this.location = location;
        this.count = count;
        this.code_count = code_count;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCode_count() {
        return code_count;
    }

    public void setCode_count(int code_count) {
        this.code_count = code_count;
    }
}
