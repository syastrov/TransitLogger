package com.example.transitlogger.tests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.transitlogger.excel.SpreadsheetFiller;
import com.example.transitlogger.model.Distance;
import com.example.transitlogger.model.Place;
import com.example.transitlogger.model.Trip;

import junit.framework.TestCase;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;

public class SpreadsheetFillerTest extends TestCase {

	private SpreadsheetFiller filler;

	protected void setUp() throws Exception {
		List<Trip> trips = new ArrayList<Trip>();
		Trip trip = new Trip();
		Place p = new Place();
		p.setName("Blah blah blah blah asd");
		trip.setStartPlace(p);
		trip.setEndPlace(p);
		trip.setDistance(new Distance(10.0f));
		trips.add(trip);
		trips.add(trip);
		
		filler = new SpreadsheetFiller(new File("korsel.xls"),
				new File("output.xls"));
		filler.setTrips(trips);
	}
	
	public void testReadWrite() {
		try {
			filler.readWrite();
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

}
