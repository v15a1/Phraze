package com.visal.phraze.model;

public class Language {
    private int index;
    private String name;
    private String abbreviation;

    public Language(int index, String name, String abbreviation) {
        this.index = index;
        this.name = name;
        this.abbreviation = abbreviation;
    }

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
