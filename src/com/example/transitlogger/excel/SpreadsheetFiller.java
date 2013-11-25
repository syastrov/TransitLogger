package com.example.transitlogger.excel;

/*********************************************************************
 *
 *      Copyright (C) 2002 Andrew Khan
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 ***************************************************************************/

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.example.transitlogger.model.Trip;

import jxl.common.Logger;

import jxl.Cell;
import jxl.CellReferenceHelper;
import jxl.CellType;
import jxl.Range;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.read.biff.BiffException;
import jxl.write.Blank;
import jxl.write.DateFormat;
import jxl.write.DateFormats;
import jxl.write.DateTime;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.WritableCell;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableHyperlink;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class SpreadsheetFiller {
	/**
	 * The logger
	 */
	private static Logger logger = Logger.getLogger(SpreadsheetFiller.class);

	/**
	 * The spreadsheet to read in
	 */
	private File inputWorkbook;
	/**
	 * The spreadsheet to output
	 */
	private File outputWorkbook;

	private List<Trip> trips;
	
	
	int rowStart = 10;
	int rowEnd = 31;

	int colDate = 0;
	int colAddress = 1;
	int colPurpose = 4;
	int colDistance = 6;

	/**
	 * Constructor
	 * 
	 * @param output
	 * @param input
	 */
	public SpreadsheetFiller(File input, File output) {
		inputWorkbook = input;
		outputWorkbook = output;
//		logger.setSuppressWarnings(Boolean.getBoolean("jxl.nowarnings"));
		logger.info("Input file:  " + input);
		logger.info("Output file:  " + output);
	}

	/**
	 * Reads in the inputFile and creates a writable copy of it called
	 * outputFile
	 * 
	 * @exception IOException
	 * @exception BiffException
	 */
	public void readWrite() throws IOException, BiffException, WriteException {
		logger.info("Reading...");

		WorkbookSettings ws = new WorkbookSettings();
		ws.setEncoding("Cp1252");
		
		Workbook w1 = Workbook.getWorkbook(inputWorkbook, ws);

		logger.info("Copying..." + ws.getEncoding() + 
				ws.getLocale());
		WritableWorkbook w2 = Workbook.createWorkbook(outputWorkbook, w1);

		modify(w2);

		w2.write();
		w2.close();
		logger.info("Done");
	}

	/**
	 * If the inputFile was the test spreadsheet, then it modifies certain
	 * fields of the writable copy
	 * 
	 * @param w
	 */
	private void modify(WritableWorkbook w) throws WriteException {
		logger.info("Modifying...");

		WritableSheet sheet = w.getSheet(0);

		fillTripRows(sheet, rowStart, rowEnd);
	}

	private double formatDistance(double d) {
		  DecimalFormat format = new DecimalFormat("#.#");
		  return Double.valueOf(format.format(d));
	}
	
	private void fillTripRow(WritableSheet sheet, int row, Trip trip) throws WriteException, RowsExceededException {
		Label l;
		Number n;
		
		String addressString = trip.getStartPlace().getName() + " til " + trip.getEndPlace().getName();
		l = new Label(colAddress, row, addressString);

	    WritableCellFormat newFormat = new WritableCellFormat(l.getCellFormat());
	    newFormat.setShrinkToFit(false);
	    l.setCellFormat(newFormat);
	    
	    sheet.addCell(l);
		
		Double distance = trip.getDistance().getKilometers();
		distance = formatDistance(distance);
		Math.floor(distance);
		n = new Number(colDistance, row, distance);
		sheet.addCell(n);
		
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		String dateString = format.format(trip.getStartDate());
		l = new Label(colDate, row, dateString);
		sheet.addCell(l);
			
//			DateFormat df = new DateFormat("dd MMM yyyy");
//			WritableCellFormat cf = new WritableCellFormat(df);
//			DateTime dt = new DateTime(col, row, (Date) val, cf);
//			sheet.addCell(dt);

	}
	
	private void fillTripRows(WritableSheet sheet, int rowStart, int rowEnd) throws WriteException, RowsExceededException {
		int numRows = rowEnd - rowStart;
		for (int i = 0; i < trips.size(); i++) {
			if (i > numRows) {
				break;
			}

			int row = i + rowStart;
			fillTripRow(sheet, row, trips.get(i));
		}
	}

	public void setTrips(List<Trip> trips) {
		this.trips = trips;
	}
}
