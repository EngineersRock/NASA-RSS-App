package com.headfirstlabs.nasadailyimage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.sax.Element;
import android.sax.ElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;
import android.util.Xml.Encoding;

class NasaParser {
    private String mTitle;
    private String mDescription;
    private String mDate;
    private String mImageUrl;
    private Bitmap image;

    public void parse(InputStream is) throws IOException, SAXException {
        RootElement rss = new RootElement("rss");
        Element channel = rss.requireChild("channel");
        Element item = channel.requireChild("item");
        item.setElementListener(new ElementListener() {
            public void end() {
                onItem(mTitle, mDescription, mDate, mImageUrl);
            }
            public void start(Attributes attributes) {
                mTitle = mDescription = mDate = mImageUrl = null;
            }
        });
        item.getChild("title").setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                mTitle = body;
            }
        });
        item.getChild("description").setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                mDescription = body;
            }
        });
        item.getChild("pubDate").setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                mDate = body;
            }
        });
        item.getChild("enclosure").setStartElementListener(new StartElementListener() {
            public void start(Attributes attributes) {
                mImageUrl = attributes.getValue("", "url");
            }
        });
        Xml.parse(is, Encoding.UTF_8, rss.getContentHandler());
        
    }
    
    

    public void onItem(String title, String description, String date, String imageUrl) {
        // This is where you handle the item in the RSS channel, etc. etc.  
        // (Left as an exercise for the reader)
    	URL url;
        System.out.println("title=" + title);
        System.out.println("description=" + description);
        System.out.println("date=" + date);
        // This needs to be downloaded for instance
        try{
            url = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();   
            conn.setDoInput(true);   
            conn.connect();     
            InputStream is = conn.getInputStream();
            image = BitmapFactory.decodeStream(is); 
        }
        catch (IOException e)
        {       
            e.printStackTrace();  
        }
            
            System.out.println("imageUrl=" + imageUrl);
            
        }
    
    public Bitmap getImage() { return image; }
    public String getTitle() { return mTitle; }
    public String getDescription() { return mDescription; }
    public String getDate() { return mDate; }
    public String getImageUrl() { return mImageUrl; }
}
