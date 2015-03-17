package org.pathvisio.biomartconnect.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.pathvisio.core.debug.Logger;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

/**
 * This class is an adapted version of class BiomartDataService.java written by jakefried.
 * http://svn.bigcat.unimaas.nl/pvplugins/jakefried/trunk/src/org/pathvisio/facets/model/ 
 *
 *@author Rohan Saxena
 * @author jakefried
 */

public class BiomartQueryService {
	private static int TIMEOUT_MS = 3000;
	private static String biomart = "http://www.biomart.org/biomart/martservice/result?query=" ; 
	
	public BiomartQueryService() {}

	public static boolean isInternetReachable() {
        return isInternetReachable("http://www.google.com");
    }
	
    public static boolean isInternetReachable(String str_url) {

    	try {
            URL url = new URL(str_url);
            HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
            java.net.URLConnection conn = url.openConnection();  
            conn.setConnectTimeout(TIMEOUT_MS);  
            conn.setReadTimeout(TIMEOUT_MS);  
                                
            Logger.log.debug("URL: " + str_url + " Response code: " + urlConnect.getResponseCode());     		
    		
            conn.getInputStream();            
            
        } catch (IOException e) {
        	Logger.log.warn("FacetedSearch: " + str_url + " Is Not Reachable");
        	e.printStackTrace();
        	return false;
        }


    	
    	Logger.log.info("FacetedSearch: " + str_url + " Is Reachable");
        return true;
    }
    
    
	/**
	 * helper method for generating proper xml queries for biomart
	 * 
	 * @param set - the dataset to use in the query
	 * @param attrs - the attributes to return in the tsv
	 * @param identifierFilters - the identifiers to get the attributes for
	 * @return a XMLDocument formattted correctly to send to biomart 
	 */
	public static Document createQuery(String set, Collection<String> attrs, Collection<String> identifierFilters, String format ) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			// create doc
			Document query = docBuilder.newDocument();
			DOMImplementation domImpl = query.getImplementation();
			DocumentType doctype = domImpl.createDocumentType("Query", "", "");
			query.appendChild(doctype);
			Element root = query.createElement("Query");
			root.setAttribute("client", "true");
			root.setAttribute("formatter", format);
			root.setAttribute("limit", "-1");
			root.setAttribute("header", "1");
			query.appendChild(root);
			/* specify the dataset to use */
			Element dataset = query.createElement("Dataset");
			dataset.setAttribute("name", set);
			//dataset.setAttribute("config", "gene_ensembl_config");
			root.appendChild(dataset);
			/* filter to only the geneIDs we care about -- must be ensembl */
			Element filter = query.createElement("Filter");
			filter.setAttribute("name", "ensembl_gene_id");
			String identifiers = identifierFilters.toString().replaceAll("[\\[\\] ]", "");
			filter.setAttribute("value", identifiers);
			dataset.appendChild(filter);
			/* add attributes specified in app */
			Element gene_id = query.createElement("Attribute");
			gene_id.setAttribute("name", "ensembl_gene_id");
			dataset.appendChild(gene_id);
			for(String attr : attrs) {
				Element a = query.createElement("Attribute");
				a.setAttribute("name", attr);
				dataset.appendChild(a);
			}
			return query;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null; 
	}

	
	/**
	 * @param xml - the xml document to convert into a string
	 * @return the xml in String Format
	 */
	public static String docToString(Document xml) {
		LSSerializer ls =  ((DOMImplementationLS) xml.getImplementation()).createLSSerializer();
		LSOutput lsOut = ((DOMImplementationLS) xml.getImplementation()).createLSOutput();
		lsOut.setEncoding("UTF-8");
		StringWriter stringWriter = new StringWriter();
		lsOut.setCharacterStream(stringWriter);			
		ls.write(xml, lsOut);
		return stringWriter.toString();
	}
	/**
	 * @returns an input stream capable of being read by CSVDataService
	 * @param xml - the query to send to biomart
	 */
	public static InputStream getDataStream(Document xml) {
		try {
			String encodedXml = docToString(xml);
			encodedXml = URLEncoder.encode(encodedXml, "UTF-8"); // encode to url
			URL url = new URL(biomart + encodedXml);
			
			Logger.log.debug("Biomart query URL: " + url.toString());
			
			HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
			urlc.setDoOutput(true);
			//urlc.setDoInput(true);
			int code = urlc.getResponseCode();
			if(code != 200) {
				Logger.log.warn("HTTP Response code: " + urlc.getResponseCode());
			}
			return urlc.getInputStream();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	
}
