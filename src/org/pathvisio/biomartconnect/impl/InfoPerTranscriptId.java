package org.pathvisio.biomartconnect.impl;


import java.util.ArrayList;
import java.util.List;

/**
 * Custom data structure to store coding information for each transcript id.
 * 
 * @author rsaxena
 *
 */

public class InfoPerTranscriptId {

	private String transcriptId;
	private String sequence;
	private List<String> exon;
	
	public InfoPerTranscriptId(){
		this.transcriptId = null;
		this.sequence = null;
		this.exon = new ArrayList<String>();
	}
	
	public String getTranscriptId(){
		return transcriptId;
	}
	
	public String getSequence(){
		return sequence;
	}
	
	public List<String> getExon(){
		return exon;
	}
	
	public void setTranscriptId(String transcriptId){
		this.transcriptId =transcriptId;
	}
	
	public void setSequence(String sequence){
		this.sequence=sequence;
	}
	
	public void setExon(String exon){
		this.exon.add(exon);
	}
	
	
	
}
