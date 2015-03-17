package org.pathvisio.biomartconnect.impl;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * Window containing genetic variation data table. Also contains a settings button to 
 * set attributes to be shown in the table. It also color codes various prediction attributes. 
 * 
 * @author rsaxena
 *
 */

public class TableDialog extends JDialog {

	JScrollPane jt;
	GeneticVariationProvider bcp;
	String [] attr;
	JPanel jp;
		
	public TableDialog(GeneticVariationProvider bcp, JScrollPane jt, String [] attr){

		this.bcp = bcp;
		this.jt = jt;
		this.attr = attr;
		initUI();
	}

	/**
	 * Initializes this window and populates it with various elements 
	 */	
	
	public final void initUI(){

		JLabel title = new JLabel("Variation Data Table");

		JPanel master = new JPanel();
		master.setLayout(new BorderLayout());
		master.add(title,BorderLayout.NORTH);
		
		JScrollPane scrollpane = new JScrollPane(jt,  JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		master.add(scrollpane,BorderLayout.CENTER);
        
		final TableSettingsDialog tsd = new TableSettingsDialog(bcp,attr,master,scrollpane);
        
		JButton settings = new JButton("Settings");
        settings.setAlignmentX(Component.CENTER_ALIGNMENT);
		settings.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){
			tsd.setVisible(true);
		}	
    });
        
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.PAGE_AXIS));
        southPanel.add(settings);
        master.add(southPanel,BorderLayout.SOUTH);
        
        add(master);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setSize(900,660);        
	}
}