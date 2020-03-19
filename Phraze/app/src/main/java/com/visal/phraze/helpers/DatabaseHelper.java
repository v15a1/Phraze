package com.visal.phraze.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.nio.Buffer;

//http://www.codebind.com/android-tutorials-and-examples/android-sqlite-tutorial-example/
public class DatabaseHelper extends SQLiteOpenHelper {

    //constant values of the database and table
    private static final String DATABASE_NAME = "Phraze.db";
    private static final String TABLE_NAME = "phrase_table";
    private static final String COLUMN_1 = "PHRASE_ID";
    private static final String COLUMN_2 = "PHRASE";
    private SQLiteDatabase database;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (PHRASE_ID INTEGER PRIMARY KEY AUTOINCREMENT, PHRASE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //method to insert data
    public boolean insertPhrase(String phrase) {
        database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_2, phrase);
        long result = database.insert(TABLE_NAME, null, contentValues); //inserting values into the db
        return result != -1;    //returning a boolean to check if the data has been successfully stored
    }

    //method to retrieve all values from the table
    public Cursor getAllPhrases() {
        database = this.getReadableDatabase();
        return database.rawQuery("select * from " + TABLE_NAME, null);
    }

    //method to update a phrase
    public boolean updateData(int phraseId,String phrase) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_1,phraseId);
        contentValues.put(COLUMN_2,phrase);
        long updated = db.update(TABLE_NAME, contentValues, "PHRASE_ID=" + phraseId, null);
        return updated != -1;
    }

    //method to retrieve finally added entries
    public Cursor getLastAddedPhrase(){
        database = this.getReadableDatabase();
        return database.rawQuery("select * from " + TABLE_NAME + " ORDER BY " + COLUMN_1 + " DESC LIMIT 3", null);

    }
}