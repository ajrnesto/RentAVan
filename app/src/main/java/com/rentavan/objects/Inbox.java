package com.rentavan.objects;

public class Inbox {
    String uid;
    String message;
    String authorUid;
    long timestamp;
    String passengerUid;

    public Inbox() {
    }

    public Inbox(String uid, String message, String authorUid, long timestamp, String passengerUid) {
        this.uid = uid;
        this.message = message;
        this.authorUid = authorUid;
        this.timestamp = timestamp;
        this.passengerUid = passengerUid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthorUid() {
        return authorUid;
    }

    public void setAuthorUid(String authorUid) {
        this.authorUid = authorUid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPassengerUid() {
        return passengerUid;
    }

    public void setPassengerUid(String passengerUid) {
        this.passengerUid = passengerUid;
    }
}
