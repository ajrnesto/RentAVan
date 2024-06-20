package com.rentavan.objects;

public class Booking
{
    String uid;
    String passengerUid;
    String model;
    String tripName;
    String travelPurpose;
    int passengers;
    double pickUpLatitude;
    double pickUpLongitude;
    double destinationLatitude;
    double destinationLongitude;
    long dateStart;
    long dateEnd;
    long time;
    String note;
    int status;

    public Booking() {
    }

    public Booking(String uid, String passengerUid, String model, String tripName, String travelPurpose, int passengers, double pickUpLatitude, double pickUpLongitude, double destinationLatitude, double destinationLongitude, long dateStart, long dateEnd, long time, String note, int status) {
        this.uid = uid;
        this.passengerUid = passengerUid;
        this.model = model;
        this.tripName = tripName;
        this.travelPurpose = travelPurpose;
        this.passengers = passengers;
        this.pickUpLatitude = pickUpLatitude;
        this.pickUpLongitude = pickUpLongitude;
        this.destinationLatitude = destinationLatitude;
        this.destinationLongitude = destinationLongitude;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.time = time;
        this.note = note;
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPassengerUid() {
        return passengerUid;
    }

    public void setPassengerUid(String passengerUid) {
        this.passengerUid = passengerUid;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getTravelPurpose() {
        return travelPurpose;
    }

    public void setTravelPurpose(String travelPurpose) {
        this.travelPurpose = travelPurpose;
    }

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }

    public double getPickUpLatitude() {
        return pickUpLatitude;
    }

    public void setPickUpLatitude(double pickUpLatitude) {
        this.pickUpLatitude = pickUpLatitude;
    }

    public double getPickUpLongitude() {
        return pickUpLongitude;
    }

    public void setPickUpLongitude(double pickUpLongitude) {
        this.pickUpLongitude = pickUpLongitude;
    }

    public double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public double getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    public long getDateStart() {
        return dateStart;
    }

    public void setDateStart(long dateStart) {
        this.dateStart = dateStart;
    }

    public long getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(long dateEnd) {
        this.dateEnd = dateEnd;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
