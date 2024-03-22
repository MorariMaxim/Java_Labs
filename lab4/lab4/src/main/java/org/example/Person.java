package org.example;

import java.util.Objects;

public class Person implements Comparable<Person> {
    protected String name;
    protected int age;
    protected Destination destination;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public Person(String name, int age, Destination destination) {
        this.name = name;
        this.age = age;
        this.destination = destination;
    }

    @Override
    public int compareTo(Person otherPerson) {
        
        int nameComparison = this.name.compareTo(otherPerson.name);
        if (nameComparison != 0) {
            return nameComparison;
        }
        return Integer.compare(this.age, otherPerson.age);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Person Person = (Person) o;
        return age == Person.age && Objects.equals(name, Person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    @Override
    public String toString() {
        return "Person [name=" + name + ", age=" + age + ", destination=" + destination + "]";
    }
}
