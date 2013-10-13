package com.example.transitlogger;

import java.util.ArrayList;
import java.util.List;

import com.example.transitlogger.model.Distance;
import com.example.transitlogger.model.Trip;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class TripDB {
	private TripDBHelper dbHelper;
	private SQLiteDatabase database;

	private String[] allColumns = { TripDBHelper.COLUMN_ID,
			TripDBHelper.COLUMN_DISTANCE };
	
	public TripDB(Context context) {
		dbHelper = new TripDBHelper(context);
	}
	
	public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
    	dbHelper.close();
    }

    public long addTrip(Trip trip) {
        ContentValues values = new ContentValues();
        values.put(TripDBHelper.COLUMN_DISTANCE, trip.getDistance().getKilometers());
        long insertId = database.insert(TripDBHelper.TABLE_TRIPS, null,
            values);
        trip.setId(insertId);
        return insertId;
    }
    
    public List<Trip> getAllTrips() {
        List<Trip> trips = new ArrayList<Trip>();

        Cursor cursor = database.query(TripDBHelper.TABLE_TRIPS,
            allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
          Trip trip = cursorToTrip(cursor);
          trips.add(trip);
          cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return trips;
    }

    private Trip cursorToTrip(Cursor cursor) {
        Trip trip = new Trip();
        trip.setId(cursor.getLong(0));
        trip.setDistance(new Distance(cursor.getFloat(1)));
        return trip;
      }
}
