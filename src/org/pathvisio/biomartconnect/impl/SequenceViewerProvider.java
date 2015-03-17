package org.pathvisio.biomartconnect.impl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;



import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.MutableComboBoxModel;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import javax.swing.text.JTextComponent;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

import org.bridgedb.Xref;
import org.pathvisio.core.model.DataNodeType;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.inforegistry.IInfoProvider;
import org.w3c.dom.Document;

/**
 * This provider queries ensembl biomart to get coding sequence of 
 * the selected gene product and display it in the info panel. It also retrieves 
 * all the exons for the gene product. User is given an option to mark all the exons
 * 
 * @author rsaxena
 *
 */

public class SequenceViewerProvider implements IInfoProvider {
	
	private PvDesktop desktop;
	
	public SequenceViewerProvider(PvDesktop desktop){
		this.desktop = desktop;
	}
	
	/**
	 * Implementing the required function of IInfoProvider interface.
	 * Gives the name to be shown in the inforegistry plugin.
	 */
	@Override
	public String getName() {
		return("Biomart Sequence Viewer");
	}
	
	
	/**
	 * Implementing the required function of IInfoProvider interface.
	 * Tells inforegistry that it works only for gene products.
	 */
	@Override
	public Set<DataNodeType> getDatanodeTypes() {
		Set<DataNodeType> s = new HashSet<DataNodeType>();
		s.add(DataNodeType.GENEPRODUCT);
		return s;
	}
	
	
	/**
	 * Implementing the required function of IInfoProvider interface.
	 * Queries ensembl biomart to find coding sequence of the selected data node.
	 * 
	 * @param xref - to provide id and data source of the selected data node
	 * @return JComponent containing the coding sequence to be displayed 
	 * in the info panel. 
	 */
	@Override
	public JComponent getInformation(Xref xref) {

		//Makes sure organism for the selected gene product is know		
		if(desktop.getSwingEngine().getCurrentOrganism() == null)
			return(new JLabel ("Organism not set for active pathway."));
		
		//Queries Utils.mapId to find any corresponding ensembl gene id
		Xref mapped = Utils.mapId(xref, desktop);
		if(mapped.getId().equals("")){
			return(new JLabel("<html>This identifier cannot be mapped to Ensembl.<br/>Check if the correct identifier mapping database is loaded.</html>"));
		}
		
		//Checks is the internet connection working before proceeding forward		
		if(BiomartQueryService.isInternetReachable()) {

			//Holds attributes for the organism of the gene product selected			
			Map<String,String> attr_map;
			String organism = Utils.mapOrganism(desktop.getSwingEngine().getCurrentOrganism().toString());

			
			
			if(organism != null) {
				
				
				Collection<String> attrs = new HashSet<String>();
				attrs.add("ensembl_gene_id");
				attrs.add("ensembl_transcript_id");
				
				//responsible for getting coding sequence
				attrs.add("coding");
				
				Collection<String> identifierFilters = new HashSet<String>();
				identifierFilters.add(mapped.getId().toString());

				//Querying Biomart
				Document result = BiomartQueryService.createQuery(organism, attrs, identifierFilters,"FASTA");
				
				//Creating input stream of the results obtained from the biomart
				InputStream is = BiomartQueryService.getDataStream(result);

				//Creating a new object of custom data structure(SequenceContainer)
				final SequenceContainer sc = new SequenceContainer();
				
				//parsing the coding sequence returned in fasta format
				sc.fastaParser(is,mapped.getId().toString(),false);

				/**
				 * Now preparing to query for exons
				 */
				
				attrs.remove("coding");
				
				//responsible for getting exons
				attrs.add("gene_exon");
				
				
				result = BiomartQueryService.createQuery(organism, attrs, identifierFilters,"FASTA");				
				is = BiomartQueryService.getDataStream(result);
				sc.fastaParser(is,mapped.getId().toString(),true);
				
				//JCombobox to show all the transcript ids
				final JComboBox transcriptIdList = new JComboBox();
				MutableComboBoxModel model = (MutableComboBoxModel)transcriptIdList.getModel();
				
				JPanel jp = new JPanel();

				final JTextArea jta = new JTextArea();
				jta.setLineWrap(true);

				JScrollPane jsp = new JScrollPane(jta,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				jta.setEditable(false);
				
				for(InfoPerTranscriptId obj: sc.transcriptIdList){
				model.addElement(obj.getTranscriptId());
				}
				
				transcriptIdList.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						JComboBox temp_combo = (JComboBox)e.getSource();
		                String currentQuantity = (String)temp_combo.getSelectedItem();
		                jta.setText(sc.find(currentQuantity).getSequence());
		                }
					});
				
				//Button to mark all the exons
				JToggleButton mark_exon = new JToggleButton("Mark Exons");
				
				
				mark_exon.addActionListener(new ActionListener(){
					
					String replacedStr = null;
					
					public void actionPerformed(ActionEvent e){
					
						if( ((JToggleButton)e.getSource()).isSelected()){
						
							replacedStr = sc.find(transcriptIdList.getSelectedItem().toString()).getSequence();		
							jta.setText(replacedStr);
							
							Highlighter highlighter = jta.getHighlighter();
							HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.PINK);
							
							for(String temp_exon: sc.find(transcriptIdList.getSelectedItem().toString()).getExon()){
								if(sc.find(transcriptIdList.getSelectedItem().toString()).getSequence().contains(temp_exon)){										
									int p0 = replacedStr.indexOf(temp_exon);										
									int p1 = p0 + temp_exon.length();      
									try {
										//Highlighting the exon
										highlighter.addHighlight(p0, p1, painter );
										} 
									catch (BadLocationException e1) {
											e1.printStackTrace();
										}
									}}}}});				
				jp.setLayout(new BorderLayout());
				jp.add(transcriptIdList, BorderLayout.NORTH);
				jp.add(jsp,BorderLayout.CENTER);
				jp.add(mark_exon,BorderLayout.SOUTH);
				
				
				
				
				return jp;
		
		
	}
		}
		return null;
	}
	


}
