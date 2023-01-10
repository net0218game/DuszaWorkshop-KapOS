package com.kapos.hypedemo.service;

public class Calculator {

    private final Integer x;
    private final Integer y;

    public Calculator(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public int sum() {
        if (x == null || y == null) {
            return 0;
        }
        return x + y;
    }

    public int multiply() {
        return x * y;
    }

    public int div() {
        if (y == 0) {
            throw new IllegalArgumentException("Nullával nem osztunk");
        }
        return x / y;
    }
}
