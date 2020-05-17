package com.visal.phraze.model;

import java.util.Date;

//phrase class to store data related to all the phrases
public class Phrase {
    int id;
    String phrase;
    String dateAdded;

    //constructor
    public Phrase(int id, String phrase, String dateAdded) {
        this.id = id;
        this.phrase = phrase;
        this.dateAdded = dateAdded;
    }

    //getters and setters
    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public int getId() {
        return id;
    }

    //to string method
    @Override
    public String toString() {
        return "Phrase{" +
                "phrase='" + phrase + '\'' +
                ", dateAdded='" + dateAdded + '\'' +
                '}';
    }
}
