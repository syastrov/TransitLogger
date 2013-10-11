package com.example.transitlogger.model;

import java.util.Date;

public class Trip {
	protected Place from, to;
	protected TripPurpose purpose;
	protected Distance distance;
	protected Date fromDate, toDate;
	protected long id;
	
	

	public Place getFrom() {
		return from;
	}
	public void setFrom(Place from) {
		this.from = from;
	}
	public Place getTo() {
		return to;
	}
	public void setTo(Place to) {
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
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
}
