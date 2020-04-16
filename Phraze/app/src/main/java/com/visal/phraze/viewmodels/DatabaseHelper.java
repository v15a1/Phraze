package com.visal.phraze.viewmodels;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.visal.phraze.model.Language;
import com.visal.phraze.model.Phrase;
import com.visal.phraze.model.Translation;

import java.util.ArrayList;

//http://www.codebind.com/android-tutorials-and-examples/android-sqlite-tutorial-example/
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();
    //constant values of the database and table
    private static final String DATABASE_NAME = "z.db";
    private static final String PHRASE_TABLE = "phrase_table";
    private static final String SUBSCRIBED_LANGUAGES_TABLE = "subscribed_languages_table";
    private static final String ALL_TRANSLATIONS_TABLE = "all_translations_table";
    private static final String PHRASE_COLUMN_1 = "PHRASE_ID";
    private static final String PHRASE_COLUMN_2 = "PHRASE";
    private static final String PHRASE_COLUMN_3 = "DATE_ADDED";
    private static final String SUBS_COLUMN_1 = "SUBS_ID";
    private static final String SUBS_COLUMN_2 = "SUBS_INDEX";
    private static final String SUBS_COLUMN_3 = "LANG_NAME";
    private static final String SUBS_COLUMN_4 = "LANG_ABBREVIATION";
    private static final String ALL_TRANSLATIONS_COLUMN_1 = "LANG";
    private static final String ALL_TRANSLATIONS_COLUMN_2 = "LANG_ABBREVIATION";
    private static final String ALL_TRANSLATIONS_COLUMN_3 = "ENGLISH_TRANSLATION";
    private static final String ALL_TRANSLATIONS_COLUMN_4 = "TRANSLATION";
    private SQLiteDatabase database;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table IF NOT EXISTS " + PHRASE_TABLE + " (PHRASE_ID INTEGER PRIMARY KEY AUTOINCREMENT, PHRASE TEXT, DATE_ADDED DATETIME)");
        db.execSQL("create table IF NOT EXISTS " + SUBSCRIBED_LANGUAGES_TABLE + " (LANGUAGE_ID INTEGER PRIMARY KEY AUTOINCREMENT, SUBS_INDEX INTEGER, LANG_NAME TEXT, LANG_ABBREVIATION TEXT)");
        db.execSQL("create table IF NOT EXISTS " + ALL_TRANSLATIONS_TABLE + " (TRANSLATION_ID INTEGER PRIMARY KEY AUTOINCREMENT, LANG_ABBREVIATION TEXT, LANG TEXT, ENGLISH_TRANSLATION TEXT, TRANSLATION TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("create table IF NOT EXISTS " + ALL_TRANSLATIONS_TABLE + " (TRANSLATION_ID PRIMARY KEY AUTOINCREMENT, LANG_ABBREVIATION TEXT, ENGLISH_TRANSLATION TEXT, TRANSLATION TEXT)");
        onCreate(db);
    }

    //method to insert phrases
    public boolean insertPhrase(String phrase) {
        database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PHRASE_COLUMN_2, phrase);
        contentValues.put(PHRASE_COLUMN_3, DateTime.getDateTime());
        long result = database.insert(PHRASE_TABLE, null, contentValues); //inserting values into the db
        return result != -1;    //returning a boolean to check if the data has been successfully stored
    }

    //method to insert subscribed languages
    public boolean insertLanguageSubscriptions(int value, String language, String languageAbbreviation) {
        database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SUBS_COLUMN_2, value);
        contentValues.put(SUBS_COLUMN_3, language);
        contentValues.put(SUBS_COLUMN_4, languageAbbreviation);
        long result = database.insert(SUBSCRIBED_LANGUAGES_TABLE, null, contentValues); //inserting values into the table
        return result != -1;
    }

    //method to add translations
    public boolean insertTranslations(String languageAbbr, String language, String englishPhrase, String translatedPhrase){
        database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ALL_TRANSLATIONS_COLUMN_1, languageAbbr);
        contentValues.put(ALL_TRANSLATIONS_COLUMN_2, language);
        contentValues.put(ALL_TRANSLATIONS_COLUMN_3, englishPhrase);
        contentValues.put(ALL_TRANSLATIONS_COLUMN_4, translatedPhrase);
        long result = database.insert(ALL_TRANSLATIONS_TABLE, null, contentValues);
        return  result != -1;
    }

    //method to retrieve all values from the table
    public ArrayList<Phrase> getAllPhraseData() {
        ArrayList<Phrase> phrases = new ArrayList<>();
        database = this.getReadableDatabase();
        Cursor cursor =  database.rawQuery("select * from " + PHRASE_TABLE, null);
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String phrase = cursor.getString(1);
            String dateAdded = cursor.getString(2);
            phrases.add(new Phrase(id,phrase, dateAdded));
        }
        return phrases;
    }

    //method to retrieve all the translations
    public ArrayList<Translation> getAlltranslations() {
        ArrayList<Translation> translations = new ArrayList<>();
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + ALL_TRANSLATIONS_TABLE, null);
        while (cursor.moveToNext()) {
            String language = cursor.getString(1);
            String abbreviation = cursor.getString(2);
            String phrase = cursor.getString(3);
            String translation = cursor.getString(4);
            translations.add(new Translation(abbreviation,language, phrase, translation));
        }
        return translations;
    }

    //method to truncate the language subscription table
    public void deleteLanguageSubscriptionTableData() {
        database.delete(SUBSCRIBED_LANGUAGES_TABLE, null, null);
    }

    //method to delete a phrase of a matched ID
    public boolean deletePhrase(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PHRASE_TABLE, "PHRASE_ID=" + id, null ) > 0;
    }

    //method to truncate the language Translations table
    public void deleteTranslations(){
        database.delete(ALL_TRANSLATIONS_TABLE, null, null);
    }

    //method to update a phrase
    public boolean updateData(int phraseId, String phrase) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PHRASE_COLUMN_2, phrase);
        long updated = db.update(PHRASE_TABLE, contentValues, "PHRASE_ID=" + phraseId, null);
        return updated != -1;
    }

    //method to retrieve most recently added entries and return an array list
    public ArrayList getLastAddedPhrases() {
        ArrayList<String> recentlyAddedPhrases = new ArrayList<>();
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select " + PHRASE_COLUMN_2 + " from " + PHRASE_TABLE + " ORDER BY " + PHRASE_COLUMN_1 + " DESC LIMIT 3", null);
        while (cursor.moveToNext()) {
            recentlyAddedPhrases.add(cursor.getString(cursor.getColumnIndex(PHRASE_COLUMN_2)));
        }
        return recentlyAddedPhrases;
    }

    //method to retrieve all the phrases for
    public ArrayList getAllPhrases() {
        ArrayList<String> allPhrases = new ArrayList<>();
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select " + PHRASE_COLUMN_2 + " from " + PHRASE_TABLE, null);
        while (cursor.moveToNext()) {
            allPhrases.add(cursor.getString(cursor.getColumnIndex(PHRASE_COLUMN_2)).toUpperCase());
        }
        return allPhrases;
    }

    //method to retrieve all the languages
    public ArrayList<Language> getAllSubscriptions() {
        ArrayList<Language> languages = new ArrayList<>();
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + SUBSCRIBED_LANGUAGES_TABLE, null);
        while (cursor.moveToNext()) {
            int index = cursor.getInt(1);
            String name = cursor.getString(2);
            String abbreviation = cursor.getString(3);
            languages.add(new Language(index, name, abbreviation));
        }
        return languages;
    }

}
