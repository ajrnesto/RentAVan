package com.rentavan.objects;

public class Van {
    String model;
    int price;
    String driver;
    String fuelType;
    String audioSystem;
    int seats;
    int luggage;
    int imageId;

    public Van() {
    }

    public Van(String model, int price, String driver, String fuelType, String audioSystem, int seats, int luggage, int imageId) {
        this.model = model;
        this.price = price;
        this.driver = driver;
        this.fuelType = fuelType;
        this.audioSystem = audioSystem;
        this.seats = seats;
        this.luggage = luggage;
        this.imageId = imageId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getAudioSystem() {
        return audioSystem;
    }

    public void setAudioSystem(String audioSystem) {
        this.audioSystem = audioSystem;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public int getLuggage() {
        return luggage;
    }

    public void setLuggage(int luggage) {
        this.luggage = luggage;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
