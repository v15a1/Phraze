package com.visal.phraze.model;

//language class to hold data related to Languages retrieved or subscribed to
public class Language {
    private int index;
    private String name;
    private String abbreviation;

    //constructor
    public Language(int index, String name, String abbreviation) {
        this.index = index;
        this.name = name;
        this.abbreviation = abbreviation;
    }

    //getter and setters
    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}
