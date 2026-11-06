package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class ItemList {
    private static ItemList instance;
    private final ArrayList<Cruise> cruises;

    private ItemList() {
        cruises = new ArrayList<>();
    }

    public static synchronized ItemList getInstance() {
        if (instance == null) {
            instance = new ItemList();
        }
        return instance;
    }

    public List<Cruise> getCruises() {
        return cruises;
    }

    public void replaceAll(List<Cruise> newCruises) {
        cruises.clear();
        cruises.addAll(newCruises);
    }

    public void add(Cruise cruise) {
        cruises.add(cruise);
    }

    public Cruise findByCode(String code) {
        for (Cruise c : cruises) {
            if (c.getCode().equalsIgnoreCase(code)) {
                return c;
            }
        }
        return null;
    }

    public boolean removeByCode(String code) {
        Cruise c = findByCode(code);
        if (c == null) {
            return false;
        }
        return cruises.remove(c);
    }

    public int size() {
        return cruises.size();
    }

    public boolean isEmpty() {
        return cruises.isEmpty();
    }
}