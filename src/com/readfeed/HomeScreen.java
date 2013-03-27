package com.readfeed;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.readfeed.article.Article;
import com.readfeed.article.FeedLink;
import com.readfeed.article.Channel;
import com.readfeed.helper.URLParser;
import com.readfeed.parser.AtomFeedNotSupportedException;
import com.readfeed.parser.FeedNotFoundException;
import com.readfeed.parser.Parser;

public class HomeScreen extends Activity {
	public static boolean DEBUG;
	public final static String TAG = "READFEED";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);
		
//		Setting application wide debugging to true
//		DEBUG = true;
		DEBUG = false;
		/*try{
			URLParser.getDomain(URLParser.getWellFormedURL("http:/myntra.com/login")); //fail case
			URLParser.getDomain(URLParser.getWellFormedURL("http://http://santabanta.com/wallpapers/movies"));//fail case
			URLParser.getDomain(URLParser.getWellFormedURL("www.www.gmail.com"));//fail case
		}
		catch (MalformedURLException e) {
			Log.d(HomeScreen.TAG, e.getMessage());
			e.printStackTrace();
		} */
			// TODO: handle exception
		new Thread(new Runnable() {

			@Override
			public void run() {
				try{
					Parser p = new Parser(URLParser.getWellFormedURL("http://www.engadget.com/"), HomeScreen.this);
					Channel page = p.getChannel();
					Log.d(HomeScreen.TAG, page.toString() + "\n");
								
					ArrayList<FeedLink> feedLinks = page.getFeedLinks();
					for(int i=0; i<feedLinks.size(); i++)
						Log.d(HomeScreen.TAG, feedLinks.get(i).getTitle());
					ArrayList<Article> articles = p.getArticles(0);
					for(Article a : articles)
						Log.d(HomeScreen.TAG, "Article Details: \n" + a.toString() + "\n");
				}
				catch (FeedNotFoundException e) {
					Log.d(HomeScreen.TAG, e.getMessage());
					e.printStackTrace();
				}
				catch (AtomFeedNotSupportedException e) {
					Log.d(HomeScreen.TAG, e.getMessage());
					e.printStackTrace();
				}
				catch (MalformedURLException e) {
					Log.d(HomeScreen.TAG, "Malformed URL");
					e.printStackTrace();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_home_screen, menu);
		return true;
	}

}