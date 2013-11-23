package com.example.transitlogger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TripDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_TRIPS = "trips";

    public static final String COLUMN_ID = "_id";
	public static final String COLUMN_DISTANCE = "distance";
	public static final String COLUMN_START_PLACE_ID = "start_place_id";
	public static final String COLUMN_END_PLACE_ID = "end_place_id";
    private static final String TRIPS_TABLE_CREATE =
                "CREATE TABLE " + TABLE_TRIPS + " ("
        		+ COLUMN_ID + " integer primary key autoincrement"
        		+ "," + COLUMN_DISTANCE + " float not null"
        		+ "," + COLUMN_START_PLACE_ID + " integer"
        		+ "," + COLUMN_END_PLACE_ID + " integer"
        		+ ")";
    
    
    public static final String TABLE_PLACES = "places";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_LAT = "lat";
	public static final String COLUMN_LON = "lon";
    private static final String PLACES_TABLE_CREATE =
                "CREATE TABLE " + TABLE_PLACES + " ("
        		+ COLUMN_ID + " integer primary key autoincrement"
        		+ "," + COLUMN_NAME + " tinytext not null"
        		+ "," + COLUMN_LAT + " float not null"
        		+ "," + COLUMN_LON + " float not null"
        		+ ")";
    
    
	private static final String DATABASE_NAME = "TransitLogger";

    TripDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
        db.execSQL(TRIPS_TABLE_CREATE);
        db.execSQL(PLACES_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(TripDBHelper.class.getName(), "Upgrading database from version "
	            + oldVersion + " to " + newVersion
	            + ", which will destroy all old data");
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIPS);
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES);
	        onCreate(db);
	}

}
