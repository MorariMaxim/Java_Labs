package org.example;

public class Passenger extends Person  {
    
    public Passenger(String name, int age, Destination destination ) {
        super(name, age, destination);
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "name='" + name + '\'' +
                ", age=" + age + 
                '}';
    }
}
