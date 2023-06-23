package com.bdo.shrey.instrumentmanager.Models;

public class Assign {
    private String i_id, s_id, s_name, time, a_user;

    public Assign() {
    }

    public Assign(String i_id, String s_id, String s_name, String time, String a_user) {
        this.i_id = i_id;
        this.s_id = s_id;
        this.s_name = s_name;
        this.time = time;
        this.a_user = a_user;
    }

    public String getI_id() {
        return i_id;
    }

    public void setI_id(String i_id) {
        this.i_id = i_id;
    }

    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
    }

    public String getS_name() {
        return s_name;
    }

    public void setS_name(String s_name) {
        this.s_name = s_name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getA_user() {
        return a_user;
    }

    public void setA_user(String a_user) {
        this.a_user = a_user;
    }
}
