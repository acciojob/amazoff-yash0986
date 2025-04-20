package com.driver;

public class Order {

    private String id;
    private int deliveryTime;

    public Order() {} // No-arg constructor

    public Order(String id, String deliveryTime) {
        this.id = id;
        this.deliveryTime = convertToMinutes(deliveryTime);
    }

    private int convertToMinutes(String time) {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        return hour * 60 + minute;
    }

    public String getId() {
        return id;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }
}
