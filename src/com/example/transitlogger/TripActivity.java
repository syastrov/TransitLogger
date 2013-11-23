package com.example.transitlogger;

import java.util.Date;
import java.util.List;

import com.example.transitlogger.model.Distance;
import com.example.transitlogger.model.Place;
import com.example.transitlogger.model.Trip;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class TripActivity extends Activity {
	private Trip trip;
	
	public static TripDB tripDB;
	
	private TextView distanceText;
	private TextView labelDistance;
	private LocationManager locationManager;
	private AutoCompleteTextView startPlace;

    private Location currentBestLocation;
	
	public LocationManager getLocationManager() {
		return locationManager;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip);
		
		//Intent intent = getIntent();
		// TODO: accept specific trip IDs

		distanceText = (TextView) findViewById(R.id.distanceText);
		labelDistance = (TextView) findViewById(R.id.labelDistance);
		labelDistance.setVisibility(View.GONE);
		distanceText.setVisibility(View.GONE);

	    tripDB = new TripDB(this);
	    tripDB.open();

		// Create a new trip.
		trip = new Trip();
		
		startPlace = (AutoCompleteTextView) findViewById(R.id.autoCompleteStartLocation);
		
		setupLocationProvider();
		
		updatePlacesAutocomplete();
		
		// Show the distance information
		labelDistance.setVisibility(View.VISIBLE);
		distanceText.setVisibility(View.VISIBLE);

		distanceText.setText("Waiting for GPS...");
	}
	
	public void startTrip() {
		trip.setFromDate(new Date());
	}
	
	protected void updatePlacesAutocomplete() {
		List<Place> places = tripDB.getAllPlaces();
		ArrayAdapter<Place> adapter = new ArrayAdapter<Place>(this,
				android.R.layout.simple_list_item_1, places);
		startPlace.setAdapter(adapter);
	}

	public void setupLocationProvider() {
		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location provider.
				// Only update the location if it's better
		    	if (location != null && isBetterLocation(location, currentBestLocation)) {
			    	updateLocation(location);
			    	currentBestLocation = location;
		    	}
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		    
		    private static final int TWO_MINUTES = 1000 * 60 * 2;

		    /** Determines whether one Location reading is better than the current Location fix
		      * @param location  The new Location that you want to evaluate
		      * @param currentBestLocation  The current Location fix, to which you want to compare the new one
		      */
		    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		        if (currentBestLocation == null) {
		            // A new location is always better than no location
		            return true;
		        }

		        // Check whether the new location fix is newer or older
		        long timeDelta = location.getTime() - currentBestLocation.getTime();
		        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		        boolean isNewer = timeDelta > 0;

		        // If it's been more than two minutes since the current location, use the new location
		        // because the user has likely moved
		        if (isSignificantlyNewer) {
		            return true;
		        // If the new location is more than two minutes older, it must be worse
		        } else if (isSignificantlyOlder) {
		            return false;
		        }

		        // Check whether the new location fix is more or less accurate
		        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		        boolean isLessAccurate = accuracyDelta > 0;
		        boolean isMoreAccurate = accuracyDelta < 0;
		        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		        // Check if the old and new location are from the same provider
		        boolean isFromSameProvider = isSameProvider(location.getProvider(),
		                currentBestLocation.getProvider());

		        // Determine location quality using a combination of timeliness and accuracy
		        if (isMoreAccurate) {
		            return true;
		        } else if (isNewer && !isLessAccurate) {
		            return true;
		        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
		            return true;
		        }
		        return false;
		    }

		    /** Checks whether two providers are the same */
		    private boolean isSameProvider(String provider1, String provider2) {
		        if (provider1 == null) {
		          return provider2 == null;
		        }
		        return provider1.equals(provider2);
		    }
		  };

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.trip, menu);
		return true;
	}
	
	class EndTripClickListener implements OnClickListener {
		private TripActivity tripActivity;

		public EndTripClickListener(TripActivity tripActivity) {
			this.tripActivity = tripActivity;
		}
		
		public void onClick(View v) {
			tripActivity.onEndTrip(v);
		}
	}
	
	public void onEndTrip(View view) {
		// Set the end date
		trip.setToDate(new Date());
		
		Place place = tripDB.getPlaceByName(startPlace.getText().toString());
		trip.setStartPlace(place);
		
		showShortMessage("Trip ended! Distance: " + trip.getDistance().getKilometers() + " km" +
				" Starting place: " + place.getName() + " (" + place.getId() + ")");
		
		// Save trip to database
		long id = tripDB.addTrip(trip);

		showLongMessage("Trip added to DB with id: " + id);
		
		// TODO: Allow the distance to be editable now.
		
		// Possibly, let them discard the trip somehow, rather than saving it
	}
	

	public void onAddStartPlace(View view) {
		String name = startPlace.getText().toString();
		
		// Check if we have a GPS location yet.
		if (currentBestLocation == null) {
			showLongMessage("Please wait until GPS location is acquired before adding a new place.");
			return;
		}
		
		// Check that this place name has not already been taken.
		Place place = tripDB.getPlaceByName(name);
		if (place != null) {
			showLongMessage("Place already exists with that name!");
		} else {
			Place newPlace = new Place();
			newPlace.setName(name);
			newPlace.setLat(currentBestLocation.getLatitude());
			newPlace.setLon(currentBestLocation.getLongitude());
			tripDB.addPlace(newPlace);
		}
		
		updatePlacesAutocomplete();
	}
	
	
	public void updateLocation(Location location) {
		if (currentBestLocation == null) {
			currentBestLocation = location;
			
			// Begin the trip once we've got our first location information
			startTrip();
			
			// Auto-select the start place if it is not already chosen
			if (startPlace.getText().length() == 0) {
				Place nearestPlace = findNearestPlace(currentBestLocation);
				if (nearestPlace != null) {
					// Choose it in our place box
					startPlace.setText(nearestPlace.getName());
				}
			}
		}
		
		// Get distance from current to new location in kilometers
		double dist = currentBestLocation.distanceTo(location) / 1000.0;
		
		Log.d(getClass().getName(), String.format("curLocation: %s\nnewLocation: %s", currentBestLocation, location));
		Distance distance = trip.getDistance();
		distance.setKilometers(distance.getKilometers() + dist);
		distanceText.setText(String.format("%.2f km", distance.getKilometers()));
	}
	
	public Place findNearestPlace(Location location) {
		Place place = tripDB.getNearestPlace(currentBestLocation.getLatitude(), currentBestLocation.getLongitude());
		return place;
	}

	public void showMessage(String message, int duration) {
		Context context = getApplicationContext();
		CharSequence text = (CharSequence) message;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
	
	public void showShortMessage(String message) {
		showMessage(message, Toast.LENGTH_SHORT);
	}
	
	public void showLongMessage(String message) {
		showMessage(message, Toast.LENGTH_LONG);
	}
}
