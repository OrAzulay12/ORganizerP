package com.or.organizerp.model;

import java.util.ArrayList;

public class GroupEvent {
      protected String id;
      protected  String name;
      protected  String type;
      protected  String date, time, details;

      protected   int maxNumber;
      User admin;
      ArrayList<User> users;

      ArrayList<User> standBy;

    ArrayList<User> approve;

    public GroupEvent(String id, String name, String type, String date, String time, String details, int maxNumber, User admin, ArrayList<User> users, ArrayList<User> standBy, ArrayList<User> approve) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.date = date;
        this.time = time;
        this.details = details;
        this.maxNumber = maxNumber;
        this.admin = admin;
        this.users = users;
        this.standBy = standBy;
        this.approve = approve;
    }

    public GroupEvent() {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(int maxNumber) {
        this.maxNumber = maxNumber;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public ArrayList<User> getStandBy() {
        return standBy;
    }

    public void setStandBy(ArrayList<User> standBy) {
        this.standBy = standBy;
    }

    public ArrayList<User> getApprove() {
        return approve;
    }

    public void setApprove(ArrayList<User> approve) {
        this.approve = approve;
    }

    @Override
    public String toString() {
        return "GroupEvent{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", details='" + details + '\'' +
                ", maxNumber=" + maxNumber +
                ", admin=" + admin +
                ", users=" + users +
                ", standBy=" + standBy +
                ", approve=" + approve +
                '}';
    }
}
