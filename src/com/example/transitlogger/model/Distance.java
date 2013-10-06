package com.example.transitlogger.model;

public class Distance {
	public static final double KM_PER_MI = 1.60934;
	protected double distanceKm;

	public Distance(double distanceKm) {
		this.distanceKm = distanceKm;
	}
	
	public double getDistanceKm() {
		return distanceKm;
	}

	public void setDistanceKm(double distanceKm) {
		this.distanceKm = distanceKm;
	}
	
	public double getDistanceMiles() {
		return distanceKm / KM_PER_MI;
	}

	public void setDistanceMiles(double distanceMiles) {
		this.distanceKm = KM_PER_MI * distanceMiles;
	}
}
