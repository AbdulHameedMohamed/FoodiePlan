package com.abdulhameed.foodieplan.model.data;

public class Step {
    private int number;
    private String description;

    public Step(int number, String description) {
        this.number = number;
        this.description = description;
    }

    public int getNumber() {
        return number;
    }

    public String getDescription() {
        return description;
    }
}