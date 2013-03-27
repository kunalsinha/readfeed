package com.readfeed.parser;

/**This exception is thrown when feed from the URL passed to the constructor of Parser class cannot be found 
 * @author Kunal Sinha, kunalsinha4u@gmail.com
 */
public class FeedNotFoundException extends Exception{

	final static long serialVersionUID = 630830980924L;

	public FeedNotFoundException(){	}

	public FeedNotFoundException(String msg){
		super(msg);
	}
}
