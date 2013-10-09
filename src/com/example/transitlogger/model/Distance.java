package com.example.transitlogger.model;

public class Distance {
	public static final double KM_PER_MI = 1.60934;
	protected double distanceKm;

	public Distance(double distanceKm) {
		this.distanceKm = distanceKm;
	}
	
	public double getKilometers() {
		return distanceKm;
	}

	public void setKilometers(double distanceKm) {
		this.distanceKm = distanceKm;
	}
	
	public double getMiles() {
		return distanceKm / KM_PER_MI;
	}

	public void setMiles(double distanceMiles) {
		this.distanceKm = KM_PER_MI * distanceMiles;
	}
}
