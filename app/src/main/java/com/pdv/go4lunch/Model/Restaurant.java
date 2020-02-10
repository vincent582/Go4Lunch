package com.pdv.go4lunch.Model;

public class Restaurant {

    private String id;
    private String name;
    private String vicinity;
    private int likes;
    private int nbrPeopleEatingHere;

    public Restaurant(){}

    public Restaurant (String name,String id,String vicinity){
        this.name = name;
        this.id = id;
        this.vicinity = vicinity;
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

    public int getNbrPeopleEatingHere() {
        return nbrPeopleEatingHere;
    }

    public void setNbrPeopleEatingHere(int someoneEatingHere) { nbrPeopleEatingHere = someoneEatingHere; }

    public int getLikes() { return likes; }

    public void setLikes(int likes) { this.likes = likes; }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }
}
