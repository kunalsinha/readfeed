package com.readfeed.helper;

import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

import com.readfeed.HomeScreen;

/**Helper class to manipulate URL address
 * @author Kunal Sinha, kunalsinha4u@gmail.com
 */

public class URLParser {
	
	/**Returns a well formed URl from the supplied String
	 * @param arg the address to be converted to a well formed URL
	 * @return well formed URL
	 * @throws MalformedURLException
	 */
	public static URL getWellFormedURL(String arg) throws MalformedURLException{
		if(HomeScreen.DEBUG)
			Log.d(HomeScreen.TAG, "User input URL: " + arg);
		
		/* https protocol is not currently supported */
		if(arg.substring(0, 8).equalsIgnoreCase("https://"))
			throw new MalformedURLException("Secure connection is not supported currently");
		
		if(!(arg.substring(0, 7).equalsIgnoreCase("http://"))){
			if(!(arg.substring(0, 4)).equals("www."))
				arg = "http://www." + arg;
			else
				arg = "http://" + arg;
		}
		else{
			if(!(arg.substring(7, 11)).equalsIgnoreCase("www.")){
				arg = "http://www." + arg.substring(7);
			}
		}
		if(HomeScreen.DEBUG)
			Log.d(HomeScreen.TAG, "Well formed URL: " + arg);
		return new URL(arg);
	}
	
	/**Returns domain name for the given URL
	 * @param url the URL address whose domain name is required
	 * @return domain name of given URL
	 */
	public static String getDomain(URL url){
		String arg = url.toString();
		String domain;
		int indexOfSlash = arg.indexOf('/', 11);
		if(indexOfSlash != -1)
			domain = arg.substring(11, indexOfSlash);
		else
			domain = arg.substring(11);
		return domain;
	}
}
