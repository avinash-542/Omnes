package com.example.omnes;

public class Leaves {
    private String roll, fromUID, fromToken, grStat, didLeft, date ,classroom, toToken, toUID;

    public Leaves () {

    }

    public Leaves(String roll, String fromUID, String fromToken, String grStat, String didLeft, String date, String classroom, String toToken, String toUID) {
        this.roll = roll;
        this.fromUID = fromUID;
        this.fromToken = fromToken;
        this.grStat = grStat;
        this.didLeft = didLeft;
        this.date = date;
        this.classroom = classroom;
        this.toToken = toToken;
        this.toUID = toUID;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getFromUID() {
        return fromUID;
    }

    public void setFromUID(String fromUID) {
        this.fromUID = fromUID;
    }

    public String getFromToken() {
        return fromToken;
    }

    public void setFromToken(String fromToken) {
        this.fromToken = fromToken;
    }

    public String getGrStat() {
        return grStat;
    }

    public void setGrStat(String grStat) {
        this.grStat = grStat;
    }

    public String getDidLeft() {
        return didLeft;
    }

    public void setDidLeft(String didLeft) {
        this.didLeft = didLeft;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getToToken() {
        return toToken;
    }

    public void setToToken(String toToken) {
        this.toToken = toToken;
    }

    public String getToUID() {
        return toUID;
    }

    public void setToUID(String toUID) {
        this.toUID = toUID;
    }
}
