package com.example.myapplication;

public class Cruise {
    private String cruiseLine;
    private String shipName;
    private String code;
    private String region;
    private int length;
    private double price;
    private double gratuity;

    public Cruise() {
    }

    public Cruise(String cruiseLine, String shipName, String code, String region,
                  int length, double price, double gratuity) {
        this.cruiseLine = cruiseLine;
        this.shipName = shipName;
        this.code = code;
        this.region = region;
        this.length = length;
        this.price = price;
        this.gratuity = gratuity;
    }

    public String getCruiseLine() {
        return cruiseLine;
    }

    public void setCruiseLine(String cruiseLine) {
        this.cruiseLine = cruiseLine;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getGratuity() {
        return gratuity;
    }

    public void setGratuity(double gratuity) {
        this.gratuity = gratuity;
    }

    public double getTotalGratuity() {
        return gratuity * length;
    }

    public double getTotalCost() {
        return price + getTotalGratuity();
    }

    public double getPerDayCost() {
        return price / length;
    }
}