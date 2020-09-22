package com.example.poachernotify;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper
{
    // Declare logging for SQLite DB
    private static final String LOG = "DatabaseHelper";

    // Declare details for the DB
    private static final String DATABASE_NAME = "trail_tracker.sql";
    private static final int DATABASE_VERSION = 1;

    // Declare table names
    private static final String TABLE_CAMERA = "camera";
    private static final String TABLE_ZONE = "zone";

    // Declare column names for CAMERAS table
    private static final String KEY_CAMERA = "camera_id";
    private static final String LATITUDE_CAMERA = "latitude";
    private static final String LONGITUDE_CAMERA = "longitude";

    // Declare column names for ZONES table
    private static final String KEY_ZONE = "zone_id";
    private static final String ZONE_NAME_ZONE = "zone_name";

    // Table Create Statements

    // CREATE Statement for table 'camera'
    private static final String CREATE_TABLE_CAMERA =  "CREATE TABLE " + TABLE_CAMERA + "(" + KEY_CAMERA + " INTEGER PRIMARY KEY," + LATITUDE_CAMERA + " INTEGER," + LONGITUDE_CAMERA + " INTEGER," + KEY_ZONE + " INTEGER, FOREIGN KEY(" + KEY_ZONE + ") REFERENCES " + TABLE_ZONE + "(" + KEY_ZONE + ")";

    // CREATE Statement for table 'zone'
    private static final String CREATE_TABLE_ZONE = "CREATE TABLE " + TABLE_ZONE + "(" + KEY_ZONE + " INTEGER PRIMARY KEY," + ZONE_NAME_ZONE + " VARCHAR(256)" + ")";

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Create Table "zone" before table "camera" so as to
        // create foreign key reference at "zone_id between them.
        db.execSQL(CREATE_TABLE_ZONE);
        db.execSQL(CREATE_TABLE_CAMERA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
