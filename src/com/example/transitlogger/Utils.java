package com.example.transitlogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

public class Utils {
	public static void copyAssets(Context context) {
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
		    Log.w(Utils.class.getName(), "External SD card not mounted");
		}
		
		AssetManager assetManager = context.getAssets();
		String[] files = null;
		String baseDir = "forms";
		try {
			files = assetManager.list(baseDir);
		} catch (IOException e) {
			Log.e("tag", "Failed to get asset file list.", e);
		}
		for (String filename : files) {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = assetManager.open(new File(baseDir, filename).getPath());
				File outFile = new File(
						Environment.getExternalStorageDirectory(), filename);
				if (!outFile.exists()) {

					out = new FileOutputStream(outFile);
					copyFile(in, out);
					in.close();
					in = null;
					out.flush();
					out.close();
					out = null;
				}
			} catch (IOException e) {
				Log.e("tag", "Failed to copy asset file: " + filename, e);
			}
		}
	}

	private static void copyFile(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}
}
