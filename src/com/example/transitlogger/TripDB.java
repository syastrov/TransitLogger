package com.example.transitlogger;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

import com.example.transitlogger.model.Distance;
import com.example.transitlogger.model.Place;
import com.example.transitlogger.model.Trip;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PointF;
import android.location.Location;

public class TripDB {
	private TripDBHelper dbHelper;
	private SQLiteDatabase database;

	private String[] allTripColumns = { TripDBHelper.COLUMN_ID,
			TripDBHelper.COLUMN_DISTANCE,
			TripDBHelper.COLUMN_START_PLACE_ID, 
			TripDBHelper.COLUMN_END_PLACE_ID};
	

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
        
        if (trip.getStartPlace() == null) {
            values.put(TripDBHelper.COLUMN_START_PLACE_ID, "null");
        } else {
        	values.put(TripDBHelper.COLUMN_START_PLACE_ID, trip.getStartPlace().getId());
        }
        
        if (trip.getEndPlace() == null) {
            values.put(TripDBHelper.COLUMN_END_PLACE_ID, "null");
        } else {
        	values.put(TripDBHelper.COLUMN_END_PLACE_ID, trip.getEndPlace().getId());
        }
        
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

	public Place getNearestPlace(double latitude, double longitude) {
		// Query for all places within a bounding box of the coordinates.
		PointF center = new PointF();
		center.set((float) latitude, (float) longitude);
		final double mult = 1; // mult = 1.1; is more reliable
		final double radius = 1000; // in meters
		PointF p1 = calculateDerivedPosition(center, mult * radius, 0);
		PointF p2 = calculateDerivedPosition(center, mult * radius, 90);
		PointF p3 = calculateDerivedPosition(center, mult * radius, 180);
		PointF p4 = calculateDerivedPosition(center, mult * radius, 270);
		
    	String[] selectionArgs = {String.valueOf(p3.x),
    			String.valueOf(p1.x),
    			String.valueOf(p2.y),
    			String.valueOf(p4.y)};
    	
    	String selection = TripDBHelper.COLUMN_LAT + " > ? AND "
    	        + TripDBHelper.COLUMN_LAT + " < ? AND "
    	        + TripDBHelper.COLUMN_LON + " < ? AND "
    	        + TripDBHelper.COLUMN_LON + " > ?";
    	
        Cursor cursor = database.query(TripDBHelper.TABLE_PLACES,
        		allPlaceColumns, selection, selectionArgs, null, null, null);

        cursor.moveToFirst();
        
        List<Place> places = new ArrayList<Place>();
        while (!cursor.isAfterLast()) {
            Place place = cursorToPlace(cursor);
            places.add(place);
            cursor.moveToNext();
        }
        
        // make sure to close the cursor
        cursor.close();
        
        
        
        // Build a map containing distances mapped to places.
        SortedMap<Float, Place> distanceMap = new TreeMap<Float, Place>();
        
        for (Place place: places) {
        	float[] results = {Float.MAX_VALUE};
			Location.distanceBetween(latitude, longitude, place.getLat(), place.getLon(), results);
			if (results.length > 0) {
				float distance = results[0];
				// Only consider this place if we are within its auto-snap range.
				if (distance < place.getAutoSnapRange().getKilometers()) {
					distanceMap.put(distance, place);
				}
			}
		}
        
        // Return the place with the lowest distance.
        try {
	        Float firstKey = distanceMap.firstKey();	
        	return distanceMap.get(firstKey);     
		} catch (NoSuchElementException e) {
			return null;
		}
	}
	
	// http://stackoverflow.com/a/12997900
	/**
	* Calculates the end-point from a given source at a given range (meters)
	* and bearing (degrees). This methods uses simple geometry equations to
	* calculate the end-point.
	* 
	* @param point
	*            Point of origin
	* @param range
	*            Range in meters
	* @param bearing
	*            Bearing in degrees
	* @return End-point from the source given the desired range and bearing.
	*/
	public static PointF calculateDerivedPosition(PointF point,
	            double range, double bearing) {
        double EarthRadius = 6371000; // m

        double latA = Math.toRadians(point.x);
        double lonA = Math.toRadians(point.y);
        double angularDistance = range / EarthRadius;
        double trueCourse = Math.toRadians(bearing);

        double lat = Math.asin(
                Math.sin(latA) * Math.cos(angularDistance) +
                        Math.cos(latA) * Math.sin(angularDistance)
                        * Math.cos(trueCourse));

        double dlon = Math.atan2(
                Math.sin(trueCourse) * Math.sin(angularDistance)
                        * Math.cos(latA),
                Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat));

        double lon = ((lonA + dlon + Math.PI) % (Math.PI * 2)) - Math.PI;

        lat = Math.toDegrees(lat);
        lon = Math.toDegrees(lon);

        PointF newPoint = new PointF((float) lat, (float) lon);

        return newPoint;
    }
}
