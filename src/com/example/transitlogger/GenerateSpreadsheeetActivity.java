package com.example.transitlogger;

import java.io.File;
import java.io.IOException;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;

import com.example.transitlogger.excel.SpreadsheetFiller;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class GenerateSpreadsheeetActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generate_spreadsheet);
		
		Utils.copyAssets(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.generate_spreadsheeet, menu);
		return true;
	}
	
	public void onGenerate(View view) {
	    File path = Utils.getDocumentsDirectory();
	    File outputFile = new File(path, "output.xls");

        // Make sure the directory exists.
        path.mkdirs();
	    
        Log.w("transitlogger", outputFile.getName());
        
        File inputFile = new File(Utils.getFormsDirectory(), "korsel.xls");
        
		SpreadsheetFiller filler = new SpreadsheetFiller(inputFile, outputFile);
		
		Toast.makeText(this, "Please wait...", Toast.LENGTH_LONG).show();
		
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
			}
		}
	}

}
