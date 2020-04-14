package com.visal.phraze;

import java.util.Date;

public class Phrase {
    String phrase;
    String dateAdded;

    public Phrase(String phrase, String dateAdded) {
        this.phrase = phrase;
        this.dateAdded = dateAdded;
    }

    public String getPhrase() {
        return phrase;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    @Override
    public String toString() {
        return "Phrase{" +
                "phrase='" + phrase + '\'' +
                ", dateAdded='" + dateAdded + '\'' +
                '}';
    }
}
