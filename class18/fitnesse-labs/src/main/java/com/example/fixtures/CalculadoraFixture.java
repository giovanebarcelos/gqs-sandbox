package com.example.fixtures;

public class CalculadoraFixture {
    private int a;
    private int b;

    public void setA(int a) { this.a = a; }
    public void setB(int b) { this.b = b; }

    public int soma()         { return a + b; }
    public int subtrai()      { return a - b; }
    public int multiplica()   { return a * b; }
    public double divide()    { return (double) a / b; }
}
