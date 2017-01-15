package com.evostest.animallocation;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "sql_animal_location.db";
    private static MyDBHelper myDBHelper;
    private SQLiteDatabase mDatabase;
    private int mOpenCounter;

    public static synchronized MyDBHelper getInstance(Context context, String dbName) {
        if (myDBHelper == null) myDBHelper = new MyDBHelper(context, dbName);
        return myDBHelper;
    }

    public static synchronized MyDBHelper getInstance(Context context) {
        return getInstance(context, DB_NAME);
    }

    private MyDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    private MyDBHelper(Context context, String dbName) {
        super(context, dbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE animals ("
                + "id INTEGER PRIMARY KEY NOT NULL, "
                + "name VARCHAR(50), "
                + "type VARCHAR(50) DEFAULT 'BIRD' NOT NULL, "
                + "latitude double, "
                + "longitude double, "
                + "has_milk BOOL NOT NULL DEFAULT '0', "
                + "can_fly BOOL NOT NULL DEFAULT '1'" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public synchronized SQLiteDatabase openDatabase() {
        mOpenCounter++;
        if (mOpenCounter == 1) {
            // Opening new database
            mDatabase = myDBHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        mOpenCounter--;
        if (mOpenCounter == 0) {
            // Closing database
            mDatabase.close();
        }
    }
}


