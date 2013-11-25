package com.example.transitlogger.tests;

import java.io.IOException;

import com.example.transitlogger.excel.SpreadsheetFiller;

import junit.framework.TestCase;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;

public class SpreadsheetFillerTest extends TestCase {

	private SpreadsheetFiller filler;

	protected void setUp() throws Exception {
		filler = new SpreadsheetFiller("korsel.xls",
				"output.xls");
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
