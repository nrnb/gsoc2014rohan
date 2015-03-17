package org.pathvisio.biomartconnect.impl;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


/**
 * This class provides functions to get all the available attributes corresponding to the organism of the 
 * selected gene product and the provider used from the biomart server.
 * 
 * @author rsaxena
 *
 */

public class AttributesImporter {
	
	String organism;
	String identifier;
	
	public AttributesImporter(String organism,String identifier){
		this.organism = organism;
		this.identifier = identifier;
	}
	
	/**
	 * Retrieves all the attributes from the biomart server for the selcted gene product.
	 * It manipulates URL to get attributes corresponding to the data provider selected.
	 * 
	 * @return - Map data structure containing all the attibutes and their corresponding 
	 * 			biomart code
	 */
	
	public Map<String,String> getAttributes(){
		
		//URL on which attributes will be available
		String url = "http://www.biomart.org/biomart/martservice?type=attributes&dataset=" + organism;
		URL attribute_file = null;
		BufferedReader in = null;
		Map<String,String> attr_map = new HashMap<String,String>();
		
		try {
			
		//Requesting the URL to get attributes 
		attribute_file = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		if(attribute_file != null){
		try {
			in = new BufferedReader(new InputStreamReader(attribute_file.openStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Parsing input stream line by line and extracting all the attributes
        String inputLine;
        try {
			while ((inputLine = in.readLine()) != null && inputLine.length() > 0){
			    String [] temp = inputLine.split("\t");
			    if(temp.length > 5 && temp[5].equals(organism + identifier)){
			    	attr_map.put(temp[1], temp[0]);
			    }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		}
		return attr_map;
	}
}