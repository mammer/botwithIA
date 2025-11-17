package com.mammer.botwithiatest.domaine.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SwingPoint {
    private int index;      // candle index in array
    private double price;   // high or low value
    private boolean isHigh; // true = swing high, false = swing low
}
