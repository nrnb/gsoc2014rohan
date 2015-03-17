package org.pathvisio.biomartconnect.impl;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;




import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Dialog window that pops up when setting button on variation table is clicked.
 * It cotains all the features that can be shown in the table.
 * 
 * @author rsaxena
 *
 */

public class TableSettingsDialog extends JDialog {
	String [] attr;
	JCheckBox[] jc;
	GeneticVariationProvider bcp;
	JPanel resultPanel;
	JScrollPane jsp;
	
	public TableSettingsDialog(GeneticVariationProvider bcp, String[] attr, JPanel resultPanel, JScrollPane jsp){

		this.bcp = bcp;
		this.attr = attr;
		this.resultPanel = resultPanel;
		this.jsp = jsp;

		
		initUI();
	}
	
	/**
	 * Initializes this window and populates it with various elements 
	 */
	
	public final void initUI(){

		JLabel title = new JLabel("Choose attributes:");
		setLayout(new BorderLayout());
		
		JPanel jp = new JPanel();
		
		//Dividing all the elements in 4 columns
		jp.setLayout(new GridLayout((attr.length/4)+1,4));
		
		jc = new JCheckBox[attr.length];
		int temp_counter = 0;
		int count = 0;
		while(count < attr.length){
			String temp = attr[count];
			jc[temp_counter] = new JCheckBox(temp);
			jc[temp_counter].setSelected(true);
			jp.add(jc[temp_counter++]);
			count++;
		}

		JButton applyButton = new JButton("Apply");
        
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new GridBagLayout());
		GridBagConstraints con = new GridBagConstraints();
		
		con.fill = GridBagConstraints.HORIZONTAL;
		con.gridx = 1;
		con.gridy = 0;
		con.gridwidth = 3;
        southPanel.add(applyButton,con);
		
		add(southPanel,BorderLayout.SOUTH);

		JScrollPane scrollpane = new JScrollPane(jp,  JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scrollpane,BorderLayout.CENTER);
		
        applyButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e){
				bcp.sendResult(resultPanel,notSelectedOptions());
			}
        });

		add(title,BorderLayout.NORTH);
        setTitle("BiomartConnect");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setSize(450,330);
        
	

	}

	/**
	 * Creates a list of non selected attributes
	 * 
	 * @return - list of non selected attributes
	 */
	
	public List<String> notSelectedOptions() {
		
		List<String> temp = new ArrayList<String>();
		

		int i;
		for(i=0;i<jc.length;i++){
			if(!jc[i].isSelected()){
				temp.add(jc[i].getText());
			}
		}

		return temp;
	}

}
