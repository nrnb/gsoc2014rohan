package org.pathvisio.biomartconnect.impl;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Window to be shown when setting button is clicked on BiomartBasic provider.
 * It contains all the attributes available of the selected gene product.
 * @author rsaxena
 *
 */

public class SettingsDialog extends JDialog {
	
	Map<String,String> attr_map;
	JCheckBox[] jc;
	BasicBiomartProvider bcp;
	
	public SettingsDialog(BasicBiomartProvider bcp, Map<String,String> attr_map){

		this.bcp = bcp;
		this.attr_map = attr_map;
		initUI();
	}
	
	public final void initUI(){

		JLabel title = new JLabel("Choose attributes:");
		setLayout(new BorderLayout());
		
		JPanel jp = new JPanel();
		jp.setLayout(new GridLayout((attr_map.size()/4)+1,4));
		
		jc = new JCheckBox[attr_map.size()];
		
		int temp_counter = 0;
		
		Iterator<String> it = attr_map.keySet().iterator();
		while(it.hasNext()){
			String temp = it.next();
			jc[temp_counter] = new JCheckBox(temp);
			if(attr_map.get(temp).equals("ensembl_gene_id") || attr_map.get(temp).equals("external_gene_id") || attr_map.get(temp).equals("description") || attr_map.get(temp).equals("chromosome_name") || attr_map.get(temp).equals("start_position"))
				jc[temp_counter].setSelected(true);
			jp.add(jc[temp_counter++]);
		}
		

		JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e){
				bcp.updateResultPanel();
			}
        });
        
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

		add(title,BorderLayout.NORTH);
        setTitle("BiomartConnect");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setSize(450,330);
	}
	
	/**
	 * Creates a list of selected options in this window
	 * 
	 * @return - List of selected options
	 */
	
	public ArrayList<String> selectedOptions() {
		
		ArrayList<String> temp = new ArrayList<String>();
		int i;
		for(i=0;i<jc.length;i++){
			if(jc[i].isSelected()){
				temp.add(jc[i].getText());
			}
		}
		return temp;
	}
}
