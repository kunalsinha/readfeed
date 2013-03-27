package com.readfeed.parser;

/**This exception is thrown when atom feeds are encountered while parsing feed XML
 * @author Kunal Sinha, kunalsinha4u@gmail.com
 */
public class AtomFeedNotSupportedException extends Exception{
	final static long serialVersionUID = 68727045654877L;
	
	public AtomFeedNotSupportedException(){	}
	
	public AtomFeedNotSupportedException(String msg){
		super(msg);
	}
}
