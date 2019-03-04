package com.example.nenad.projekat;

import java.io.Serializable;
import java.util.Date;

public class ZakazanaVoznja implements Serializable{
    private Driver driver;
    private String pocetak;
    private String kraj;
    private int year;
    private int month;
    private int day;

    public ZakazanaVoznja() {
    }

    public ZakazanaVoznja(Driver driver, String pocetak, String kraj, int year, int month, int day) {
        this.driver= driver;
        this.pocetak = pocetak;
        this.kraj = kraj;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public String getPocetak() {
        return pocetak;
    }

    public void setPocetak(String pocetak) {
        this.pocetak = pocetak;
    }

    public String getKraj() {
        return kraj;
    }

    public void setKraj(String kraj) {
        this.kraj = kraj;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
