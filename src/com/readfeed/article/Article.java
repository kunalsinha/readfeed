package com.readfeed.article;

import java.net.URL;
import java.util.ArrayList;

/**
 * This class contains details about an item of the channel element.
 * @author Kunal Sinha, kunalsinha4u@gmail.com
 *
 */
public class Article {
	private String title;
	private String link;
	private String description;
	private String author;
	private ArrayList<String> categories;
	private String pubDate;
	private URL commentURL;
	private String updatePeriod; //Not used in current version
	private String updateFrequency; //Not used in current version
	
	/**Returns comment URL for the article
	 * @return
	 */
	public URL getCommentURL() {
		return commentURL;
	}

	/**Sets comment URL of the article
	 * @param comments
	 */
	public void setCommentURL(URL comments) {
		this.commentURL = comments;
	}

	/**
	 * @return title of the <article> tag 
	 */
	public String getTitle() {
		return title;
	}
	
	/** 
	 * sets the title of the <article> tag
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * @return URL of the article
	 */
	public String getLink() {
		return link;
	}
	
	/**sets the URL of the article
	 * @param link
	 */
	public void setLink(String link) {
		this.link = link;
	}
	
	/**
	 * @return text value of <description> tag inside <article> element
	 */
	public String getDescription() {
		return description;
	}
	
	/**sets text value of <description> tag inside <article> element
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return author of the article
	 */
	public String getAuthor() {
		return author;
	}
	
	/**sets author of the article
	 * @param author
	 */
	public void setAuthor(String author) {
		this.author = author;
	}
	
	/**
	 * @return text value of <category> tags inside <article>
	 */
	public ArrayList<String> getCategories() {
		return categories;
	}
	
	/**sets <category> tags inside <article>
	 * @param category
	 */
	public void setCategories(ArrayList<String> categories) {
		this.categories = categories;
	}
	
	/**
	 * @return publish date of the article
	 */
	public String getPubDate() {
		return pubDate;
	}
	
	/**sets publish date of the article
	 * @param pubDate
	 */
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	@Override
	public String toString() {
		String articleString = "";
		if(title != null)
			articleString += title;
		if(link != null)
			articleString += "\n" + link;
		if(description != null)
			articleString += "\n" + description;
		if(author != null)
			articleString += "\n" + author;
		if(categories.size() > 0)
			articleString += "\n" + categories.toString();
		if(pubDate != null)
			articleString += "\n" + pubDate;
		if(commentURL != null)
			articleString += "\n" + commentURL.toString();
		return articleString;
	}
	
}
