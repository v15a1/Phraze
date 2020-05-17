package com.visal.phraze.model;

//Translated class to store data of a translation
public class Translation {
    private String abbreviation;
    private String language;
    private String englishPhrase;
    private String translation;

    //constructor
    public Translation(String abbreviation, String language, String englishPhrase, String translation) {
        this.abbreviation = abbreviation;
        this.language = language;
        this.englishPhrase = englishPhrase;
        this.translation = translation;
    }

    @Override
    public String toString() {
        return "Translation{" +
                "abbreviation='" + abbreviation + '\'' +
                ", language='" + language + '\'' +
                ", englishPhrase='" + englishPhrase + '\'' +
                ", translation='" + translation + '\'' +
                "}\n";
    }

    //getters
    public String getLanguage() {
        return language;
    }

    public String getEnglishPhrase() {
        return englishPhrase;
    }

    public String getTranslation() {
        return translation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}
