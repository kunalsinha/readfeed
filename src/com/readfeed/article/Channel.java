package com.readfeed.article;

import java.net.URL;
import java.util.ArrayList;


/**
 * This class contains details about the channel element of the XML.
 * @author Kunal Sinha, kunalsinha4u@gmail.com
 *
 */

public class Channel {
	private String title;
	private String description;
	private URL link;
	private ArrayList<FeedLink> feedLinks;
	private String language;
	private String generator;
	private String lastBuildDate;
	private String copyright;
	
	/**
	 * @return copyright information of the <channel>
	 */
	public String getCopyright() {
		return copyright;
	}

	/**Sets copyright information of the channel
	 * @param copyright
	 */
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	/**Get language encoding of the feed
	 * @return
	 */
	public String getLanguage() {
		return language;
	}

	/**Set language encoding of the feed
	 * @param language
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**Get generator source of the feed
	 * @return
	 */
	public String getGenerator() {
		return generator;
	}

	/**Set generator source of the feed
	 * @param generator
	 */
	public void setGenerator(String generator) {
		this.generator = generator;
	}

	/**Get last build date of the feed
	 * @return
	 */
	public String getLastBuildDate() {
		return lastBuildDate;
	}

	/**Set last build date of the feed
	 * @param lastBuildDate
	 */
	public void setLastBuildDate(String lastBuildDate) {
		this.lastBuildDate = lastBuildDate;
	}

	/**Creates a new Channel object.
	 */
	public Channel(){
		this.title = null;
		this.link = null;
		description = null;
	}
	
	/**Returns the title of <channel> tag
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * sets the title of the <channel> tag
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return URL of home page
	 */
	public URL getLink() {
		return link;
	}

	/**
	 * @param link sets URL of home page
	 */
	public void setLink(URL link) {
		this.link = link;
	}


	/**Returns the feed URLs present in HTML source page
	 * @return
	 */
	public ArrayList<FeedLink> getFeedLinks() {
		return feedLinks;
	}

	public void setFeedLinks(ArrayList<FeedLink> feedLinks) {
		this.feedLinks = feedLinks;
	}

	/**
	 * @return text value of <description> tag inside <channel> element
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description sets text value of <description> tag inside <channel> element
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		String pageString = "";
		if(title != null)
			pageString += title;
		if(link != null)
			pageString += "\n" + link;
		if(feedLinks.size() > 0)
			pageString += "\n" + feedLinks.toString();
		if(description != null)
			pageString += "\n" + description;
		if(language != null)
			pageString += "\n" + language;
		if(generator != null)
			pageString += "\n" + generator;
		if(lastBuildDate != null)
			pageString += "\n" + lastBuildDate;
		if(copyright != null)
			pageString += "\n" + copyright;
		return pageString;
	}
	
	
}
