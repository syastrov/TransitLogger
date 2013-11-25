package com.example.transitlogger.tests;

import com.example.transitlogger.TripDB;
import com.example.transitlogger.model.Point2D;

import junit.framework.TestCase;

public class NearestPlacesTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testCalc() {
		double latitude = 37.42;
		double longitude = -122.08;
		Point2D center = new Point2D((float) latitude, (float) longitude);
		final double mult = 1.0; // mult = 1.1; is more reliable
		final double radius = 500; // in meters
		Point2D p1 = TripDB.calculateDerivedPosition(center, mult * radius, 0);
		Point2D p2 = TripDB.calculateDerivedPosition(center, mult * radius, 90);
		Point2D p3 = TripDB.calculateDerivedPosition(center, mult * radius, 180);
		Point2D p4 = TripDB.calculateDerivedPosition(center, mult * radius, 270);
		
		System.out.println(center);
		System.out.println("");
		
		System.out.println(p1);
		System.out.println(p2);
		System.out.println(p3);
		System.out.println(p4);
		
		assertEquals(true, center.x > p3.x);
		assertEquals(true, center.x < p1.x);

		assertEquals(true, center.y < p2.y);
		assertEquals(true, center.y > p4.y);
	}
}
