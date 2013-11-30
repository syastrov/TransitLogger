package com.example.transitlogger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;

import com.example.transitlogger.excel.SpreadsheetFiller;
import com.example.transitlogger.model.Trip;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class GenerateSpreadsheeetActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generate_spreadsheet);
		
		if (!Utils.areAssetsCopied()) {
			if (Utils.copyAssets(this)) {
				Toast.makeText(this, "Unable to copy forms to SD storage. Please be sure it is mounted.", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.generate_spreadsheeet, menu);
		return true;
	}
	
	public void onGenerate(View view) {
	    File path = Utils.getDocumentsDirectory();
	    
	    if (!path.exists()) {
			Toast.makeText(this, "Output directory " + path.toString() + " does not exist.", Toast.LENGTH_LONG).show();	
	    	return;
	    }
	    
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String dateString = format.format(new Date());
		
	    File outputFile = new File(path, "kørsel-" + dateString + ".xls");

        // Make sure the directory exists.
        path.mkdirs();
	    
        Log.w("transitlogger", outputFile.getName());
        
        File inputFile = new File(Utils.getFormsDirectory(), "korsel.xls");
        
	    if (!inputFile.exists()) {
			Toast.makeText(this, "Form template " + inputFile.toString() + " does not exist.", Toast.LENGTH_LONG).show();	
	    	return;
	    }
	    
		Toast.makeText(this, "Please wait...", Toast.LENGTH_LONG).show();
        
		SpreadsheetFiller filler = new SpreadsheetFiller(inputFile, outputFile);
		
	    TripDB tripDB = new TripDB(this);
	    tripDB.open();

    	filler.setTrips(tripDB.getAllTrips());
		
		try {
			filler.readWrite();
		} catch (BiffException e) {
			Toast.makeText(this, "BIFF Error!", Toast.LENGTH_LONG).show();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			Toast.makeText(this, "Error writing!", Toast.LENGTH_LONG).show();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(this, "IO Error!", Toast.LENGTH_LONG).show();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (outputFile.exists()) {
				Toast.makeText(this, "Generated spreadsheet " + outputFile + " successfully.", Toast.LENGTH_LONG).show();			
				
				Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(outputFile), "application/vnd.ms-excel");
				
				try {
					startActivity(intent); 
				} catch (ActivityNotFoundException e) {
					Toast.makeText(this, "Cannot open spreadsheet. Please install a spreadsheet app that can open Excel .XLS files.", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

}
