// PredictionEntry.java
package com.example.test;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class PredictionEntry {
    private String city;
    private double area;
    private float floors;
    private int bedrooms;
    private int bathrooms;
    private double price;
    private Date timestamp;

    public PredictionEntry(String city, double area, float floors, int bedrooms, int bathrooms, double price) {
        this.city = city;
        this.area = area;
        this.floors = floors;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.price = price;
        this.timestamp = new Date(); // Set current date and time
    }

    public String getCity() {
        return city;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public double getArea() {
        return area;
    }

    public float getFloors() {
        return floors;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public double getPrice() {
        return price;
    }
}
