package com.readfeed.article;

import java.net.URL;

/**Stores the title and URL of all the available feeds 
 * @author Kunal Sinha, kunalsinha4u@gmail.com
 */
public class FeedLink {
	private String title;
	private URL link;
	
	/**
	 * @return title of the feed
	 */
	public String getTitle() {
		return title;
	}
	
	/**Sets title of the feed
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * @return URL address of the feed
	 */
	public URL getLink() {
		return link;
	}
	
	/**Sets URL of the feed
	 * @param link
	 */
	public void setLink(URL link) {
		this.link = link;
	}
	
	@Override
	public String toString() {
		String feedLinkString = "";
		if(title != null)
			feedLinkString += "\n" + title;
		if(link != null)
			feedLinkString += "\t" + link;
		return feedLinkString;
	}
}
