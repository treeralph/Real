package com.example.real.tool;

public class NumberingMachine {
    private int number = 0;
    public NumberingMachine(){
        number = 0;
    }
    public int getNumber(){ return number; }
    public void add(){
        number++;
    }
}
