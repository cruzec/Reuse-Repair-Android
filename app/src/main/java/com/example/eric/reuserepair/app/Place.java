package com.example.eric.reuserepair.app;

/**
 * Created by Eric on 3/14/2016.
 */

public class Place {
    private String name;
    private double distance;

    public Place(String name, double distance) {
        this.name = name;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public double getDistance() {
        return distance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
