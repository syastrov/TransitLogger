package com.example.transitlogger;

import java.util.ArrayList;
import java.util.List;

import com.example.transitlogger.model.Distance;
import com.example.transitlogger.model.Place;
import com.example.transitlogger.model.Trip;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class TripDB {
	private TripDBHelper dbHelper;
	private SQLiteDatabase database;

	private String[] allTripColumns = { TripDBHelper.COLUMN_ID,
			TripDBHelper.COLUMN_DISTANCE };
	

	private String[] allPlaceColumns = { TripDBHelper.COLUMN_ID,
			TripDBHelper.COLUMN_NAME,
			TripDBHelper.COLUMN_LAT,
			TripDBHelper.COLUMN_LON};
	
	
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
        		allTripColumns, null, null, null, null, null);

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
    

    
    
    public long addPlace(Place place) {
        ContentValues values = new ContentValues();
        values.put(TripDBHelper.COLUMN_NAME, place.getName());
        values.put(TripDBHelper.COLUMN_LAT, place.getLat());
        values.put(TripDBHelper.COLUMN_LON, place.getLon());
        long insertId = database.insert(TripDBHelper.TABLE_PLACES, null,
            values);
        place.setId(insertId);
        return insertId;
    }
    
    public List<Place> getAllPlaces() {
        List<Place> places = new ArrayList<Place>();

        Cursor cursor = database.query(TripDBHelper.TABLE_PLACES,
        		allPlaceColumns, null, null, null, null, null);

        cursor.moveToFirst();
        
        while (!cursor.isAfterLast()) {
          Place place = cursorToPlace(cursor);
          places.add(place);
          cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return places;
    }
    
    /**
     * Get a place by its name, returning null if it does not exist.
     * @param name
     * @return
     */
    public Place getPlaceByName(String name) {
    	String[] selectionArgs = {name};
        Cursor cursor = database.query(TripDBHelper.TABLE_PLACES,
        		allPlaceColumns, TripDBHelper.COLUMN_NAME + " = ?", selectionArgs, null, null, null);

        cursor.moveToFirst();
        
        Place place = null;
        while (!cursor.isAfterLast()) {
          place = cursorToPlace(cursor);
          cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return place;
    }

    private Place cursorToPlace(Cursor cursor) {
        Place place = new Place();
        place.setId(cursor.getLong(0));
        place.setName(cursor.getString(1));
        place.setLat(cursor.getFloat(2));
        place.setLon(cursor.getFloat(3));
        return place;
    }
}
