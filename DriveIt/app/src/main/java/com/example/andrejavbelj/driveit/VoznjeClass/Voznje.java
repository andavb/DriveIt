package com.example.andrejavbelj.driveit.VoznjeClass;


import java.security.Timestamp;

public class Voznje {
    private int id;
    private String ime;
    private int kolicina;
    private int cena;
    private long datum;


    public Voznje(int id, String ime, int kolicina, int cena, long cas) {
        this.id = id;
        this.ime = ime;
        this.kolicina = kolicina;
        this.cena = cena;
        this.datum = cas;
    }

    public Voznje(String ime, int kolicina, int cena) {
        this.ime = ime;
        this.kolicina = kolicina;
        this.cena = cena;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public int getKolicina() {
        return kolicina;
    }

    public void setKolicina(int kolicina) {
        this.kolicina = kolicina;
    }

    public int getCena() {
        return cena;
    }

    public void setCena(int cena) {
        this.cena = cena;
    }

    public long getCas() {
        return datum;
    }

    public void setCas(long cas) {
        this.datum = cas;
    }

}
