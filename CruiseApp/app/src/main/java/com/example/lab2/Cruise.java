package com.example.lab2;

public class Cruise {

    private String cruiseLine;
    private String shipName;
    private String code;
    private String region;
    private int length;
    private double price;

    public Cruise(String cruiseLine, String shipName, String code,
                  String region, int length, double price) {
        this.cruiseLine = cruiseLine;
        this.shipName = shipName;
        this.code = code;
        this.region = region;
        this.length = length;
        this.price = price;
    }

    public String getCruiseLine() { return cruiseLine; }
    public String getShipName()   { return shipName; }
    public String getCode()       { return code; }
    public String getRegion()     { return region; }
    public int getLength()        { return length; }
    public double getPrice()      { return price; }
}