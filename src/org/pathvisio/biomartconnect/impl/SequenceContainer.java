package org.pathvisio.biomartconnect.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A custom made data structure for storing coding information of a gene product.
 * 
 * @author rsaxena
 *
 */

public class SequenceContainer {

	//A list of all the transcript ids of this gene product
	List<InfoPerTranscriptId> transcriptIdList;
	
	public SequenceContainer(){	
		transcriptIdList = new ArrayList<InfoPerTranscriptId>();
	}
	
	/**
	 * Adds new transcript sequence for on gene id
	 * @return - object containing new sequence 
	 */
	public InfoPerTranscriptId addSequence(){
		
		InfoPerTranscriptId temp = new InfoPerTranscriptId();
		temp.setSequence(null);
		temp.setTranscriptId(null);
		transcriptIdList.add(temp);
		return temp;
	}
	
	/**
	 * Searches if this transcript id already exists
	 * 
	 * @param id - transcript id to be searched
	 * @return - object containing transcript id if it exits otherwise null
	 */
	public InfoPerTranscriptId find(String id) {
		for(InfoPerTranscriptId temp: transcriptIdList){
			if(temp.getTranscriptId().equals(id)){
				return temp;
			}
		}
		return null;
	}
	
	/**
	 * Parses fasta format to extract all the coding sequences
	 * 
	 * @param is - InputStream containing coding sequence in fasta format
	 * @param id - ensembl gene id
	 * @param isExon - true if parsing for Exon otherwise false
	 */
	
	public void fastaParser(InputStream is, String id, Boolean isExon){

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;
		String [] temp_array = null;
		
		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				
				if(line.startsWith(">")){
					
					if(temp_array != null){
						
						for(int i=0;i<temp_array.length && temp_array[i] != null;i++){
							InfoPerTranscriptId it = find(temp_array[i]);
							if(it != null){
								if(isExon){
									it.setExon(sb.toString());
								}
								else {
									it.setSequence(sb.toString());
								}
							}
						}
						
						sb = new StringBuilder();
					}
					
					String temp = line.substring(1);
					temp_array = temp.split("[|]");

					Set<String> temp_set = new HashSet<String>();
					
					for(int i=0;i<temp_array.length;i++){
						temp_set.addAll(Arrays.asList(temp_array[i].split("[;]")));
					}
					
					temp_array = temp_set.toArray(temp_array);
					
					for(int i=0;i<temp_array.length && temp_array[i] != null;i++){
						if(!temp_array[i].equals(id)){
							if(find(temp_array[i]) == null){
							InfoPerTranscriptId temp_seq = addSequence();
							temp_seq.setTranscriptId(temp_array[i]);
							}
						}
						
						
					}
				}
				
				else {
					sb.append(line);
				}
			}
			
			for(int i=0;i<temp_array.length && temp_array[i] != null;i++){
				InfoPerTranscriptId it = find(temp_array[i]);
				if(it != null){
					if(isExon){
				it.setExon(sb.toString());
					}
					else
					{
						it.setSequence(sb.toString());
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

/*	public void print() {
		for(InfoPerTranscriptId temp: transcriptIdList){			
			System.err.println("starts here" + temp.getTranscriptId());
			
			if(temp.getSequence() != null){
				System.err.println("sequence" + temp.getSequence().substring(0, 10));
			}
			else{
				System.err.println("Null");
			}
			if(temp.getExon() != null){
				List<String> temp_list = temp.getExon();
				for(String temp_string: temp_list){	
					System.err.println("exon" + temp_string.substring(0, 10));	
				}
			}
			else{
				System.err.println("Null");
			}
			
		}
	}
	*/
}
