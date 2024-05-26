package com.testClasses;

import org.junit.Test;

import com.example.TestClass;

@TestClass 
public class TestClass1 {

    @Test
    public static void method1() {
        System.out.println("Test method1 executed!");
    }

    @Test
    public static void method2(String p1, int p2) {
        System.out.println(String.format("Test method2 (%s, %d) executed!", p1, p2));

    }

    public static void main(String[] args) {
        
    }
}
