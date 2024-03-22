package org.example;

public class Driver extends Person {
    

    public Driver(String name, int age, Destination destination ) {
        super(name, age, destination);
    }

    @Override
    public String toString() {
        return "Driver{" +
                "name='" + name + '\'' +
                ", age=" + age + 
                '}';
    }
}
