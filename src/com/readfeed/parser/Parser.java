package com.readfeed.parser;

/**Utility class to obtain feeds from the given URL.
 * @author Kunal Sinha, kunalsinha4u@gmail.com
 */

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.util.ByteArrayBuffer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;

import com.readfeed.HomeScreen;
import com.readfeed.article.Article;
import com.readfeed.article.FeedLink;
import com.readfeed.article.Channel;
import com.readfeed.helper.URLParser;


public class Parser {
	
	/* Stores the context passed in the constructor of Parser class */
	private Context context;
	
	/* Stores URL of the site for which feeds have to be read */
	private URL url;
	
	/* Stores details of the channel element of feed */
	private Channel channel = null;
	private Document channelDocument;
	private Article article;
	private ArrayList<Article> articles;
	private ArrayList<FeedLink> feedLinks = null;
	
	/* Stores domain name which will be used as base file name to save feed XMLs */
	private String domain;
	
	/**Retrieves the feed URL from the HTML page and saves the feeds into internal memory and retrieves details about the feed page 
	 * @param url URL from which feeds should be shown
	 * @param context the context to use. Usually your Application or Activity object.
	 * @throws FeedNotFoundException
	 * @throws AtomFeedNotSupportedException
	 */
	public Parser(URL url, Context context) throws FeedNotFoundException, AtomFeedNotSupportedException{
		this.context = context;
		this.url = url;

		try{
			getFeedURL();
		}
		catch (FeedNotFoundException e) {
			e.getMessage();
			e.printStackTrace();
			throw new FeedNotFoundException("Feed unavailable for " + url.toString());
		}
		
		/* Throw a FeedNotFoundException if no feed URL is present in the URL source */
		if(feedLinks.size() <= 0)
			throw new FeedNotFoundException("Feed unavailable for " + url.toString());
		
		domain = URLParser.getDomain(url); 
		saveFeedXML();
		if(HomeScreen.DEBUG)
			displayFeedXML();
		getChannelDetails();
			
	}
	
	/**Extracts feed URLs from the HTML source of URL passed in the constructor of Parser object.
	 * @return true if feed is available, false otherwise.
	 * @throws FeedNotFoundException
	 */
	private void getFeedURL() throws FeedNotFoundException{
		try{
			channelDocument = Jsoup.connect(url.toString()).userAgent("Mozilla/5.0 (X11; Linux i686) " +
					"AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.97 Safari/537.22").followRedirects(true).get();
			
			channelDocument.normalise();
			
			Elements elements = channelDocument.select("link[type]");
			feedLinks = new ArrayList<FeedLink>();
			
			for(Element element : elements){
				if(element.attr("type").equals("application/rss+xml")){
					FeedLink feedLink = new FeedLink();
					feedLink.setTitle(element.attr("title"));
					feedLink.setLink(new URL(element.attr("abs:href")));
					feedLinks.add(feedLink);
				}
			}
		}
		catch (MalformedURLException e) {
			Log.d(HomeScreen.TAG, "MalformedURLException while setting feed link in Parser.obtainFeedURL()");
			e.printStackTrace();
			throw new FeedNotFoundException("Invalid URL");
		}
		catch (NullPointerException e) {
			e.printStackTrace();
			throw new FeedNotFoundException("Feed not available for this URL");
		}
		catch (SocketTimeoutException e) {
			Log.d(HomeScreen.TAG, "SocketTimeOutException in Parser.obtainFeedURL()");
			e.printStackTrace();
			throw new FeedNotFoundException("Feed not available currently for " + url.toString());
		}
		catch (IOException e) {
			Log.d(HomeScreen.TAG, "I/O failed in Parser.obtainFeedURL()");
			e.printStackTrace();
			throw new FeedNotFoundException("Error while parsing feed from " + url.toString());
		}
	}
	
	/**Extracts the channel details and stores it in a Channel object.
	 * @throws FeedNotFoundException
	 * @throws AtomFeedNotSupportedException
	 */
	private void getChannelDetails() throws FeedNotFoundException, AtomFeedNotSupportedException{
		XmlPullParserFactory pullParserFactory = null;
		XmlPullParser pullParser = null;
		try{
			pullParserFactory = XmlPullParserFactory.newInstance();
			pullParser = pullParserFactory.newPullParser();
			
			/* If only one feed link is present, set parser input to "<domain_name>.xml", else, set parser 
			 * input to "<domain_name>.<first_article_title>.xml" */
			if(feedLinks.size() == 1){
				pullParser.setInput(new InputStreamReader(context.openFileInput(domain + ".xml")));
			}
			else{
				pullParser.setInput(new InputStreamReader(context.openFileInput(domain + "." + feedLinks.get(0).getTitle() + ".xml")));
			}
			
			int eventType = pullParser.getEventType();
			String tag = null;
			String text = null;
			
			/* Tags like <title>, <link>, <description> etc. may be present more than once in <channel>. So we use boolean values
			 * like titleSet, linkSet, descriptionSet etc. to keep track if the concerned properties have been set for an article 
			 * or not. */ 
			boolean titleSet = false;
			boolean descriptionSet = false;
			boolean languageSet = false;
			boolean generatorSet = false;
			boolean lastBuildDateSet = false;
			boolean copyrightSet = false;
			
			/* The variable 'tag' is checked for null in START_TAG and END_TAG cases to prevent NullPointerException on 
			 * using it. */
			
			while(eventType != XmlPullParser.END_DOCUMENT){
				tag = pullParser.getName();
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if(tag == null)
						break;
					
					/* If <channel> tag is encountered in the feed XML, create a new Channel object */
					else if(tag.equals("channel")) 
						channel = new Channel();
					
					/* If <feed> tag is found in the feed XML, throw a AtomFeedNotSupportedException */
					else if(tag.equals("feed"))
						throw new AtomFeedNotSupportedException("ReadFeed does not suppport atom feeds currently. Stay tuned");
					
					/* If <html> tag is found in the feed XML, throw a FeedNotFoundException */
					else if(tag.equals("html"))
						throw new FeedNotFoundException("Feed format is not proper");
					
					break;
				
				case XmlPullParser.TEXT:
					text = pullParser.getText();
					
				case XmlPullParser.END_TAG:
					if(tag == null)
						break;
					else if(tag.equals("title") && !titleSet){
						channel.setTitle(text);
						titleSet = true;
					}
					else if(tag.equals("description") && !descriptionSet){
						channel.setDescription(text);
						descriptionSet = true;
					}
					else if(tag.equals("language") && !languageSet){
						channel.setLanguage(text);
						languageSet = true;
					}
					else if(tag.equals("generator") && !generatorSet){
						channel.setGenerator(text);
						generatorSet = true;
					}
					else if(tag.equals("lastBuildDate") && !lastBuildDateSet){
						channel.setLastBuildDate(text);
						lastBuildDateSet = true;
					}
					else if(tag.equals("copyright") && !copyrightSet){
						channel.setCopyright(text);
						copyrightSet = true;
					}

				default:
					break;
				}
				
				/* Break out of event loop when first <item> is encountered because no more <channel> properties will 
				 * be present */
				if(tag != null && tag.equals("item"))
					break;
					
				eventType = pullParser.next();
			}
			
			/* If <channel> was not encountered in the feed XML, throw a FeedNotFoundException */
			if(channel == null) 
				throw new FeedNotFoundException("Feed format is not supported");
			
			channel.setLink(url);
			channel.setFeedLinks(feedLinks);
		}
		catch (XmlPullParserException e) {
			if(feedLinks.size() == 1)
				Log.d(HomeScreen.TAG, "XMLPullParserException while parsing " + domain + ".xml" +  " in Parser.getChannelDetails()");
			else
				Log.d(HomeScreen.TAG, "XMLPullParserException while parsing " + domain + "." + feedLinks.get(0).getTitle() + ".xml" +  " in Parser.getChannelDetails()");
			e.printStackTrace();
		}
		catch (FileNotFoundException e) {
			if(feedLinks.size() == 1)
				Log.d(HomeScreen.TAG, "FileNotFoundException while opening " + domain + ".xml" +  " in Parser.getChannelDetails()");
			else
				Log.d(HomeScreen.TAG, "FileNotFoundException while opening " + domain + "." + feedLinks.get(0).getTitle() + ".xml" +  " in Parser.getChannelDetails()");
			e.printStackTrace();
		}
		catch (IOException e) {
			if(feedLinks.size() == 1)
				Log.d(HomeScreen.TAG, "IOException while parsing " + domain + ".xml" +  " in Parser.getChannelDetails()");
			else
				Log.d(HomeScreen.TAG, "IOException while parsing " + domain + "." + feedLinks.get(0).getTitle() + ".xml" +  " in Parser.getChannelDetails()");
			e.printStackTrace();
		}
	}
	
	/**Returns a Channel object with details of the channel element
	 * @return Channel
	 */
	public Channel getChannel(){
		return channel;
	}
	
	/** Returns the articles in the feed
	 * @param topicNumber feed number for which articles are to be obtained. Pass 0 if only one feed link is present.
	 * @return an ArrayList of Article class
	 */
	public ArrayList<Article> getArticles(int topicNumber) throws IOException{
		try{
			if(topicNumber >= feedLinks.size())
				throw new IOException("Invalid topic number. Corresponding article does not exist.");
			
			XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
			pullParserFactory.setNamespaceAware(true);
			XmlPullParser pullParser = pullParserFactory.newPullParser();
			
			/*If the size of feedLinks, the ArrayList storing feed URLs is one, open file "<domain_name>.xml", else 
			 * open appropriate XML file corresponding to the topic number "<domain_name>.<article_name>.xml" */
			if(feedLinks.size() == 1)
				pullParser.setInput(context.openFileInput(domain + ".xml"), "UTF-8");
			else{
				pullParser.setInput(context.openFileInput(domain + "." + feedLinks.get(topicNumber).getTitle() + ".xml"), "UTF-8");
			}
			
			parseForArticles(pullParser);
		}
		catch (XmlPullParserException e) {
			if(feedLinks.size() == 1)
				Log.d(HomeScreen.TAG, "XMLPullParserException while parsing " + domain + ".xml" +  " in Parser.getArticles()");
			else
				Log.d(HomeScreen.TAG, "XMLPullParserException while parsing " + domain + "." + feedLinks.get(topicNumber).getTitle() + ".xml" +  " in Parser.getArticles()");
			e.printStackTrace();
		}
		catch (FileNotFoundException e) {
			if(feedLinks.size() == 1)
				Log.d(HomeScreen.TAG, "FileNotFoundException while opening " + domain + ".xml" + " in Parser.getArticles()");
			else
				Log.d(HomeScreen.TAG, "FileNotFoundException while opening " + domain + "." + feedLinks.get(topicNumber).getTitle() + ".xml" + " in Parser.getArticles()");
			e.printStackTrace();
		}
		catch (IOException e) {
			if(feedLinks.size() == 1)
				Log.d(HomeScreen.TAG, "IOException while parsing " + domain + ".xml" +  " in Parser.getArticles()");
			else
				Log.d(HomeScreen.TAG, "IOException while parsing " + domain + "." + feedLinks.get(topicNumber).getTitle() + ".xml" +  " in Parser.getArticles()");
			e.printStackTrace();
		}
		return articles;
	}
	
	/**The actual parsing of the XML file is done by this method
	 * @param pullParser an instance of XMLPullParser whose input has been set
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private void parseForArticles(XmlPullParser pullParser) throws XmlPullParserException, IOException{
		
		articles = new ArrayList<Article>();
		String text = null;
		
		/* Tags like <title>, <link>, <description> etc. may be present in both <channel> and <item>. So we use readingItems to determine
		 * if the parser is currently inside <item> or not. It is set to true when parser encounters a <item> element and set to false when
		 * a </item> is encountered. An <item> tag may also contain more than one tags like <title> or <link>, so we use boolean values
		 * like titleSet, linkSet, descriptionSet etc. to keep track if the concerned properties have been set for an article or not. */
		boolean readingItems = false;
		boolean titleSet = false;
		boolean linkSet = false;
		boolean descriptionSet = false;
		boolean creatorSet = false;
		boolean pubDateSet = false;
		boolean commentURLSet = false;
		
		ArrayList<String> categories = new ArrayList<String>();
		int eventType = pullParser.getEventType();
		
		/* The variable 'tag' is checked for null in START_TAG and END_TAG cases to prevent NullPointerException 
		 * on using it later. */
		while(eventType != XmlPullParser.END_DOCUMENT){
			String tag = pullParser.getName();
			switch (eventType) {
			
			case XmlPullParser.START_TAG:
				if(tag == null)
					break;
				else if(tag.equals("item")){
					article = new Article();
					categories = new ArrayList<String>();
					readingItems = true; 
				}
				break;
				
			case XmlPullParser.TEXT:
				text = pullParser.getText();
				
			case XmlPullParser.END_TAG:
				if(tag == null)
					break;
				else if(tag.equals("item")){
					article.setCategories(categories); 
					articles.add(article);
					titleSet = false;
					linkSet = false;
					descriptionSet = false;
					creatorSet = false;
					pubDateSet = false;
					commentURLSet = false;
					readingItems = false;
				}
				else if(readingItems){
					if(tag.equals("title") && !titleSet){
						article.setTitle(text);
						titleSet = true;
					}
					else if(tag.equals("link") && !linkSet){
						article.setLink(text);
						linkSet = true;
					}
					else if(tag.equals("description") && !descriptionSet){
						article.setDescription(text);
						descriptionSet = true;
					}
					else if(tag.equals("creator") && !creatorSet){
						article.setAuthor(text);
						creatorSet = true;
					}
					else if(tag.equals("category")){
						categories.add(text);
					}
					else if(tag.equals("pubDate") && !pubDateSet){
						article.setPubDate(text);
						pubDateSet = true;
					}
					else if(tag.equals("comments") && !commentURLSet){
						try{
							article.setCommentURL(new URL(text));
						}
						catch (MalformedURLException e) {
							Log.d(HomeScreen.TAG, "Comments URL is not valid");
							e.printStackTrace();
						}
						commentURLSet = true;
					}
				}
				break;
				
			default:
				break;
			}
			eventType = pullParser.next();
		}
	}
	
	/**Downloads the feed XMLs from all the feed links present in the URL source and stores it in internal storage
	 */
	private void saveFeedXML(){
		int topicNumber = 0;
		try{
			for(topicNumber=0; topicNumber<feedLinks.size(); topicNumber++){
				InputStream is = feedLinks.get(topicNumber).getLink().openStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				ByteArrayBuffer baf = new ByteArrayBuffer(5000);
				int current = 0;
				while((current = bis.read()) != -1)
					baf.append((byte)current);
				bis.close();
				is.close();
				
				FileOutputStream fos = null;
				
				/* If only one feed link is present, save XML in "<domain_name>.xml",
				 * else, save XML in "<domain_name>.<article_title>.xml"
				 */
				if(feedLinks.size() == 1)
					fos = context.openFileOutput(domain + ".xml", Context.MODE_PRIVATE);
				else
					fos = context.openFileOutput(domain + "." + feedLinks.get(topicNumber).getTitle() + ".xml", Context.MODE_PRIVATE);
				
				fos.write(baf.toByteArray());
				fos.close();
			}
		}
		catch (IOException e) {
			if(feedLinks.size() == 1)
				Log.d(HomeScreen.TAG, "IOException while parsing " + domain + ".xml" +  " in Parser.saveFeedXML()");
			else
				Log.d(HomeScreen.TAG, "IOException while parsing " + domain + "." + feedLinks.get(topicNumber).getTitle() + ".xml" +  " in Parser.saveFeedXML()");
			e.printStackTrace();
		}
	}
	
	/**Utility method to display contents of the downloaded feed XMLs
	 */
	private void displayFeedXML(){
		int topicNumber = 0;
		try{
			for(topicNumber=0; topicNumber<feedLinks.size(); topicNumber++){
				FileInputStream fis = null;
				if(feedLinks.size() == 1)
					fis = context.openFileInput(domain + ".xml");
				else
					fis = context.openFileInput(domain + "." + feedLinks.get(topicNumber).getTitle() + ".xml");
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				String line;
				while((line = br.readLine()) != null)
					Log.d(HomeScreen.TAG, line);
				br.close();
				fis.close();
			}
		}
		catch (IOException e) {
			if(feedLinks.size() == 1)
				Log.d(HomeScreen.TAG, "IOException while parsing " + domain + ".xml" + " in Parser.displayFeedXML()");
			else
				Log.d(HomeScreen.TAG, "IOException while parsing " + domain + "." + feedLinks.get(topicNumber).getTitle() + ".xml" + " in Parser.displayFeedXML()");
			e.printStackTrace();
		}
	}
	
}
