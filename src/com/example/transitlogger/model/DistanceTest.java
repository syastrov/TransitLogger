package com.example.transitlogger.model;

import junit.framework.TestCase;

public class DistanceTest extends TestCase {

	private Distance d;
	
	public void setUp() {
		d = new Distance(1.0); // 1 km
	}
	
	public void testDistance() {
		assertEquals(d.getDistanceKm(), 1.0, .001);
		assertEquals(d.getDistanceMiles(), 1.0/Distance.KM_PER_MI, .001);
		d.setDistanceKm(2.0);
		assertEquals(d.getDistanceKm(), 2.0, .001);
	}

}
