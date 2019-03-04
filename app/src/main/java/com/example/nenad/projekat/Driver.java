package com.example.nenad.projekat;

import java.io.Serializable;

public class Driver implements Serializable {
    private String email;
    private String id;
    private String user_image;
    private String name;
    private String phone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private int pozitivneOcene;
    private int negativneOcene;

    public int getPozitivneOcene() {
        return pozitivneOcene;
    }

    public void setPozitivneOcene(int pozitivneOcene) {
        this.pozitivneOcene = pozitivneOcene;
    }

    public int getNegativneOcene() {
        return negativneOcene;
    }

    public void setNegativneOcene(int negativneOcene) {
        this.negativneOcene = negativneOcene;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }



    public void setEmail(String email)
    {
        this.email=email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail()
    {

        return this.email;
    }
    public void dodajPozitivnuOcenu()
    {
        pozitivneOcene++;
    }
    public void dodajNegativnuOcenu()
    {
        negativneOcene--;
    }
}
