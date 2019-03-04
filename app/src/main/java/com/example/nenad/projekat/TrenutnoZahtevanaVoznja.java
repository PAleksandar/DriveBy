package com.example.nenad.projekat;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class TrenutnoZahtevanaVoznja implements Serializable{
    private Customer customer;
    private double startLatitude;
    private double startLongitude;
    private double destinationLatitude;

    public double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
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

    private double destinationLongitude;

    public TrenutnoZahtevanaVoznja(Customer customer, double startLatitude, double startLongitude, double destinationLatitude, double destinationLongitude, int distance, int duration) {
        this.customer = customer;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.destinationLatitude = destinationLatitude;
        this.destinationLongitude = destinationLongitude;
        this.distance = distance;
        this.duration = duration;
    }
    public  TrenutnoZahtevanaVoznja()
    {

    }
    private int distance;
    private int duration;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }


    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LatLng getStart()
    {
        return new LatLng(startLatitude,startLongitude);
    }
    public LatLng getDestination()
    {
        return  new LatLng(destinationLatitude,destinationLongitude);
    }
}
