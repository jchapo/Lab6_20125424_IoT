package com.example.lab6_20125424_iot.item;

import java.io.Serializable;

public class ListElementIngreso implements Serializable {
    private String id;
    private String title;
    private double amount;
    private String description;
    private String date;

    public ListElementIngreso(String id, String title, double amount, String description, String date) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    // Constructor sin argumentos requerido por Firebase
    public ListElementIngreso() {}


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
