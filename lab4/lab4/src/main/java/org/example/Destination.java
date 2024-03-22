package org.example;

public class Destination {
    private String location;

    public Destination(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "Destination [location=" + location + "]";
    }


}

