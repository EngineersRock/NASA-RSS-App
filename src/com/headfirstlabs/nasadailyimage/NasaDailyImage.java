package com.headfirstlabs.nasadailyimage;
// This is a git test yo!
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.sax.Element;
import android.sax.ElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;
import android.util.Xml.Encoding;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class NasaDailyImage extends Activity {

	Handler uiHandler;
	Bitmap	nasaImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); 
        setContentView(R.layout.activity_nasa_daily_image);
        uiHandler = new Handler();
        refreshFromFeed();
    }

    
    
    private void resetDisplay (String title, String date, Bitmap image, String description) {

        TextView titleView = (TextView) findViewById (R.id.imageTitle);
        titleView.setText(title);

        TextView dateView = (TextView) findViewById(R.id.imageDate);
        dateView.setText(date);

        ImageView imageView = (ImageView) findViewById (R.id.imageDisplay);
        imageView.setImageBitmap(image);

        TextView descriptionView = (TextView) findViewById (R.id.imageDescription);
        descriptionView.setText(description);
    }
    
    private void refreshFromFeed() {
    	
    	//opens loading screen
    	final ProgressDialog dialog = ProgressDialog.show(this, "Loading", "Loading the image of the Day");
    	
    	// creates and runs non-ui processes on a separate thread
    	Thread th = new Thread() {
    		public void run() {
    		

    	    	// Initializes Internet connection
    	    	final NasaParser handler = new NasaParser();
    	        String url = "http://www.nasa.gov/rss/image_of_the_day.rss";
    	        InputStream inputStream = null;
    	     // Opens Internet connection 
    			try {
    				inputStream = new URL(url).openStream();
    			} catch (MalformedURLException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			} catch (IOException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			}
    	        try {
    				handler.parse(inputStream);
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (SAXException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    	        
    	     Runnable r = new Runnable() {
    	    	 @Override public void run() {
    	    		 handler.onItem(handler.getTitle(), handler.getDescription(), handler.getDate(), handler.getImageUrl());
    	    	     resetDisplay (handler.getTitle(), handler.getDate(), handler.getImage(), handler.getDescription());
    	    	     nasaImage = handler.getImage();
    	    	     //closes loading screen
    	    	     dialog.dismiss();
    	    	 }
    	     };
    	     uiHandler.post(r);    
    		}
    	};
    	th.start(); 	
    }
    
    public void onRefresh(View view) {
    	refreshFromFeed();
    }
   //Note: The book set this up as a separate function pg 154-163
    public void onSetWallpaper(View view) {
		Thread th = new Thread() {
			public void run() {
				WallpaperManager wallpaperManager =
						WallpaperManager.getInstance(NasaDailyImage.this);
				try {
					wallpaperManager.setBitmap(nasaImage);
					uiHandler.post(
							new Runnable() {
								public void run() {
									CharSequence text = "Wallpaper set";
									Toast.makeText(NasaDailyImage.this,
											text,
											Toast.LENGTH_SHORT).show();
								}});
				}
				catch (Exception e) {
					e.printStackTrace();
					uiHandler.post(
							new Runnable() {
								public void run() {
									CharSequence text = "Error Setting Wallpaper";
									Toast.makeText(NasaDailyImage.this,
											text,
											Toast.LENGTH_SHORT).show();
								}});
				}
			}
		};
		th.start();
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nasa_daily_image, menu);
        return true;
    }
    
}
