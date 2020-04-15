package com.visal.phraze.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.visal.phraze.Language;
import com.visal.phraze.Phrase;
import com.visal.phraze.Translation;

import java.util.ArrayList;

//http://www.codebind.com/android-tutorials-and-examples/android-sqlite-tutorial-example/
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();
    //constant values of the database and table
    private static final String DATABASE_NAME = "z.db";
    private static final String PHRASE_TABLE = "phrase_table";
    private static final String SUBSCRIBED_LANGUAGES = "subscribed_languages_table";
    private static final String SELECTED_TRANSLATIONS = "all_translations_table";
    private static final String COLUMN_1 = "PHRASE_ID";
    private static final String COLUMN_2 = "PHRASE";
    private static final String COLUMN_3 = "DATE_ADDED";
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
        db.execSQL("create table IF NOT EXISTS " + SUBSCRIBED_LANGUAGES + " (LANGUAGE_ID INTEGER PRIMARY KEY AUTOINCREMENT, SUBS_INDEX INTEGER, LANG_NAME TEXT, LANG_ABBREVIATION TEXT)");
        db.execSQL("create table IF NOT EXISTS " + SELECTED_TRANSLATIONS + " (TRANSLATION_ID INTEGER PRIMARY KEY AUTOINCREMENT, LANG_ABBREVIATION TEXT, LANG TEXT, ENGLISH_TRANSLATION TEXT, TRANSLATION TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("create table IF NOT EXISTS " + SELECTED_TRANSLATIONS + " (TRANSLATION_ID PRIMARY KEY AUTOINCREMENT, LANG_ABBREVIATION TEXT, ENGLISH_TRANSLATION TEXT, TRANSLATION TEXT)");
        onCreate(db);
    }

    //method to insert data
    public boolean insertPhrase(String phrase) {
        database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_2, phrase);
        contentValues.put(COLUMN_3, DateTime.getDateTime());
        long result = database.insert(PHRASE_TABLE, null, contentValues); //inserting values into the db
        return result != -1;    //returning a boolean to check if the data has been successfully stored
    }

    public boolean insertLanguageSubscriptions(int value, String language, String languageAbbreviation) {
        database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SUBS_COLUMN_2, value);
        contentValues.put(SUBS_COLUMN_3, language);
        contentValues.put(SUBS_COLUMN_4, languageAbbreviation);
        long result = database.insert(SUBSCRIBED_LANGUAGES, null, contentValues); //inserting values into the table
        return result != -1;
    }


    public boolean insertTranslations(String languageAbbr, String language, String englishPhrase, String translatedPhrase){
        database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ALL_TRANSLATIONS_COLUMN_1, languageAbbr);
        contentValues.put(ALL_TRANSLATIONS_COLUMN_2, language);
        contentValues.put(ALL_TRANSLATIONS_COLUMN_3, englishPhrase);
        contentValues.put(ALL_TRANSLATIONS_COLUMN_4, translatedPhrase);
        long result = database.insert(SELECTED_TRANSLATIONS, null, contentValues);
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

    public ArrayList<Translation> getAlltranslations() {
        ArrayList<Translation> translations = new ArrayList<>();
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + SELECTED_TRANSLATIONS, null);
        while (cursor.moveToNext()) {
            String language = cursor.getString(2);
            String phrase = cursor.getString(3);
            String translation = cursor.getString(4);
            translations.add(new Translation(language, phrase, translation));
        }
        return translations;
    }

    public void deleteLanguageSubscriptionTableData() {
        database.delete(SUBSCRIBED_LANGUAGES, null, null);
    }

    public boolean deletePhrase(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PHRASE_TABLE, "PHRASE_ID=" + id, null ) > 0;
    }

    public void deleteTranslations(){
        database.delete(SELECTED_TRANSLATIONS, null, null);
    }

    //method to update a phrase
    public boolean updateData(int phraseId, String phrase) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_2, phrase);
        long updated = db.update(PHRASE_TABLE, contentValues, "PHRASE_ID=" + phraseId, null);
        return updated != -1;
    }

    //method to retrieve finally added entries and return an array list
    public ArrayList getLastAddedPhrases() {
        ArrayList<String> recentlyAddedPhrases = new ArrayList<>();
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select " + COLUMN_2 + " from " + PHRASE_TABLE + " ORDER BY " + COLUMN_1 + " DESC LIMIT 3", null);
        while (cursor.moveToNext()) {
            recentlyAddedPhrases.add(cursor.getString(cursor.getColumnIndex(COLUMN_2)));
        }
        return recentlyAddedPhrases;
    }

    public ArrayList getAllPhrases() {
        ArrayList<String> allPhrases = new ArrayList<>();
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select " + COLUMN_2 + " from " + PHRASE_TABLE, null);
        while (cursor.moveToNext()) {
            allPhrases.add(cursor.getString(cursor.getColumnIndex(COLUMN_2)).toUpperCase());
        }
        return allPhrases;
    }


    public ArrayList<Language> getAllSubscriptions() {
        ArrayList<Language> languages = new ArrayList<>();
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + SUBSCRIBED_LANGUAGES, null);
        while (cursor.moveToNext()) {
            int index = cursor.getInt(1);
            String name = cursor.getString(2);
            String abbreviation = cursor.getString(3);
            languages.add(new Language(index, name, abbreviation));
        }
        return languages;
    }

}
