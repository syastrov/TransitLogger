package com.example.transitlogger.model;

import java.util.Date;

public class Trip {
	protected Place from = new Place();
	protected Place to = new Place();
	protected TripPurpose purpose = new TripPurpose("Business");
	protected Distance distance = new Distance();
	protected Date fromDate, toDate;
	protected long id;
	
	public Trip() {
		fromDate = new Date();
		toDate = new Date();
	}

	public Place getStartPlace() {
		return from;
	}
	public void setStartPlace(Place from) {
		this.from = from;
	}
	public Place getEndPlace() {
		return to;
	}
	public void setEndPlace(Place to) {
		this.to = to;
	}
	public TripPurpose getPurpose() {
		return purpose;
	}
	public void setPurpose(TripPurpose purpose) {
		this.purpose = purpose;
	}
	public Distance getDistance() {
		return distance;
	}
	public void setDistance(Distance distance) {
		this.distance = distance;
	}
	public Date getStartDate() {
		return fromDate;
	}
	public void setStartDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getEndDate() {
		return toDate;
	}
	public void setEndDate(Date toDate) {
		this.toDate = toDate;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
}
