package org.pathvisio.biomartconnect.impl;


import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

/**
 * Custom table cell renderer for Genetic Variation table to do color coding on prediction attributes
 * 
 * @author rsaxena
 *
 */

public class Renderer implements TableCellRenderer {
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
		
		
	    JTextField editor = new JTextField();
	    if (value != null)
	      editor.setText(value.toString());
	    if(table.getColumnName(column).equals("PolyPhen prediction")){
	    	
	    	if(value.toString().equalsIgnoreCase("probably damaging")){
	    		editor.setBackground(Color.red);
	    	}
	    	
	    	if(value.toString().equalsIgnoreCase("possibly damaging")){
	    		editor.setBackground(Color.orange);
	    	}
	    	
	    	if(value.toString().equalsIgnoreCase("benign")){
	    		editor.setBackground(Color.green);
	    	}
	    	
	    	if(value.toString().equalsIgnoreCase("unknown")){
	    		editor.setBackground(Color.gray);
	    	}
	    }
	    
	    else if (table.getColumnName(column).equals("SIFT prediction")){
	    	
	    	if(value.toString().equalsIgnoreCase("tolerated")){
	    		editor.setBackground(Color.green);
	    	}
	    	
	    	if(value.toString().equalsIgnoreCase("deleterious")){
	    		editor.setBackground(Color.red);
	    	}
	    	
	    }
	    
	    
	    return editor;
	    
	}
}