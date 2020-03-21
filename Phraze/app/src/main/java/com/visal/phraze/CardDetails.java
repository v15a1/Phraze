package com.visal.phraze;

//https://blog.chirathr.com/android/2018/08/23/android-recycler-view/
public class CardDetails {
    private String phrase;
    private int addedDaysAgo;

    public CardDetails(String phrase, int addedDaysAgo) {
        this.phrase = phrase;
        this.addedDaysAgo = addedDaysAgo;
    }

    public int getAddedDaysAgo() {
        return addedDaysAgo;
    }

    public String getPhrase() {
        return phrase;
    }
}
