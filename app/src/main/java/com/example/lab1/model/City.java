package com.example.lab1.model;

public class City {
    String name,country,id;
    int population;

    public City() {
    }

    public City(String name, String country, String id, int population) {
        this.name = name;
        this.country = country;
        this.id = id;
        this.population = population;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }
}
