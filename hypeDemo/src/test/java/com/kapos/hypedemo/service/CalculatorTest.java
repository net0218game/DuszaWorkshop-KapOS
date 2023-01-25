package com.kapos.hypedemo.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculatorTest {

    @Test
    public void sumAddsTheNumbersWhenXAndYDefined() {
        Calculator calculator = new Calculator(5, 10);
        int result = calculator.sum();
        assertEquals(15, result);
    }

    @Test
    public void multiplyMultipliesWhenXAndYDefined() {
        Calculator calculator = new Calculator(5, 10);
        int result = calculator.multiply();
        assertEquals(50, result);
    }

    @Test
    public void divDividesWhenXAndYDefined() {
        Calculator calculator = new Calculator(5, 10);
        int result = calculator.div();
        assertEquals(0, result);
    }

    @Test
    public void sumIsZeroWhenXAndYNotDefined() {
        Calculator calculator = new Calculator(null, null);
        int result = calculator.sum();
        assertEquals(0, result);
    }

    @Test
    public void divThrowsExceptionWhenYIs0() {
        Calculator calculator = new Calculator(5, 0);
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, calculator::div);
        assertEquals("Nullával nem osztunk", illegalArgumentException.getMessage());
    }
}