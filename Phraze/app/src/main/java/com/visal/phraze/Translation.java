package com.visal.phraze;

public class Translation {
    String language;
    String englishPhrase;
    String translation;

    public Translation(String language, String englishPhrase, String translation) {
        this.language = language;
        this.englishPhrase = englishPhrase;
        this.translation = translation;
    }

    @Override
    public String toString() {
        return "Translation{" +
                "language='" + language + '\'' +
                ", englishPhrase='" + englishPhrase + '\'' +
                ", translation='" + translation + '\'' +
                '}';
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
}
