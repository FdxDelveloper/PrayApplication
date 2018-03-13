package com.kritcharat.prayapp;

import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/*
 * Default Notification handler class for receiving ContentHandler
 * events raised by the SAX Parser
 * 
 * */
public class XParser extends DefaultHandler {

	ArrayList<String> idlist = new ArrayList<String>();
	ArrayList<String> namelist = new ArrayList<String>();
	ArrayList<String> desclist = new ArrayList<String>();
	ArrayList<String> audiolist = new ArrayList<String>();
	ArrayList<String> textlist = new ArrayList<String>();
	ArrayList<String> iconlist = new ArrayList<String>();

	// temp variable to store the data chunk read while parsing
	private String tempStore = null;

	public XParser() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * Clears the tempStore variable on every start of the element notification
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		super.startElement(uri, localName, qName, attributes);

		if (localName.equalsIgnoreCase("id")) {
			tempStore = "";
		} else if (localName.equalsIgnoreCase("name")) {
			tempStore = "";
		} else if (localName.equalsIgnoreCase("description")) {
			tempStore = "";
		} else if (localName.equalsIgnoreCase("audio")) {
			tempStore = "";
		} else if (localName.equalsIgnoreCase("text")) {
			tempStore = "";
		} else if (localName.equalsIgnoreCase("icon")) {
			tempStore = "";
		} else {
			tempStore = "";
		}
	}

	/*
	 * updates the value of the tempStore variable into corresponding list on
	 * receiving end of the element notification
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);

		if (localName.equalsIgnoreCase("id")) {
			idlist.add(tempStore);
		} else if (localName.equalsIgnoreCase("name")) {
			namelist.add(tempStore);
		} else if (localName.equalsIgnoreCase("description")) {
			desclist.add(tempStore);
		} else if (localName.equalsIgnoreCase("audio")) {
			audiolist.add(tempStore);
		} else if (localName.equalsIgnoreCase("text")) {
			textlist.add(tempStore);
		} else if (localName.equalsIgnoreCase("icon")) {
			iconlist.add(tempStore);
		}

		tempStore = "";
	}

	/*
	 * adds the incoming data chunk of character data to the temp data variable
	 * - tempStore
	 */
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		tempStore += new String(ch, start, length);
	}

}
