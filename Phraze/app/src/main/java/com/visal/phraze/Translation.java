package com.visal.phraze;

public class Translation {
    String abbreviation;
    String language;
    String englishPhrase;
    String translation;

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
