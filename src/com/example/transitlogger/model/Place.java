package com.example.transitlogger.model;

public class Place {
	protected double lat, lon;
	protected String name;
	protected Distance autoSnapRange = new Distance(0.500); // in kilometers
	protected long id;
	
	public Place () {
		;
	}
	
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Distance getAutoSnapRange() {
		return autoSnapRange;
	}
	public void setAutoSnapRange(Distance autoSnapRange) {
		this.autoSnapRange = autoSnapRange;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String toString() {
		return getName();
	}
}
