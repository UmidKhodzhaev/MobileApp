package com.example.notes.model;


public class Task extends NamedEntity {
    private int subject_id;
    private int status_id;
    private int category_id;

    private String date;
    private String deadline;
    private String location;
    private String comment;

    public Task(int id, int subject_id, int status_id, int category_id, String title, String date, String deadline, String location, String comment) {
        super(id, title);
        this.subject_id = subject_id;
        this.status_id = status_id;
        this.category_id = category_id;
        this.date = date;
        this.deadline = deadline;
        this.location = location;
        this.comment = comment;
    }

    public int getSubject_id() {
        return subject_id;
    }

    public int getStatus_id() {
        return status_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public String getDate() {
        return date;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getLocation() {
        return location;
    }

    public String getComment() {
        return comment;
    }

    public void setSubject_id(int subject_id) {
        this.subject_id = subject_id;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
