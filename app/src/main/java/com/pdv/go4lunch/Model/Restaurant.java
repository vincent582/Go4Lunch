package com.pdv.go4lunch.Model;

public class Restaurant {

    private String id;
    private String name;
    private int rate;
    private Boolean isSomeoneEatingHere;

    public Restaurant (String name,String id,Boolean isSomeoneEatingHere){
        this.id = id;
        this.name = name;
        this.isSomeoneEatingHere = isSomeoneEatingHere;
    }

    public Restaurant(){}

    public Restaurant (String name,String id){
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public Boolean getSomeoneEatingHere() {
        return isSomeoneEatingHere;
    }

    public void setSomeoneEatingHere(Boolean someoneEatingHere) {
        isSomeoneEatingHere = someoneEatingHere;
    }
}
