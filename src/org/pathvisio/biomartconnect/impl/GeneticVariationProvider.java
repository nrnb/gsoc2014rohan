package org.pathvisio.biomartconnect.impl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;
import org.bridgedb.Xref;
import org.pathvisio.core.model.DataNodeType;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.inforegistry.IInfoProvider;
import org.pathvisio.inforegistry.InfoRegistry;
import org.w3c.dom.Document;

/**
 * Provider to show genetic variation data for selected gene product. It shows some basic data in tabular form
 * directly in the info panel. More extensive information is provided under a new window which becomes visible 
 * on clicking a button on info panel. 
 * 
 * 
 * @author rsaxena
 *
 */

public class GeneticVariationProvider extends JPanel implements IInfoProvider{

	private PvDesktop desktop;
	private JComponent resultPanel;
	private ArrayList<String []> temp = null;
	private TableModel dataModel = null;
	public TableRowSorter<TableModel> sorter;
	public GeneticVariationProvider(InfoRegistry registry, PvDesktop desktop) {
		
		registry.registerInfoProvider(this);
		this.desktop = desktop;
		resultPanel = new JPanel();
	}
	
	/**
	 * Implementing the required function of IInfoProvider interface.
	 * Gives the name to be shown in the inforegistry plugin.
	 */
	public String getName(){
		String name = "Biomart Genetic Variation Provider";
		return(name);	
	}
	
	
	/**
	 * Implementing the required function of IInfoProvider interface.
	 * Tells inforegistry that it works only for gene products.
	 */	
	public Set<DataNodeType> getDatanodeTypes(){
		
		Set<DataNodeType> s = new HashSet<DataNodeType>();
		s.add(DataNodeType.GENEPRODUCT );
		return s;	
	}
	
	
	/**
	 * Implementing the required function of IInfoProvider interface.
	 * Queries ensembl biomart to find genetic variation information regarding the selected data node.
	 * 
	 * @param xref - to provide id and data source of the selected data node
	 * @return JComponent containing the features in the tabular form to be displayed 
	 * in the info panel. 
	 */	
	public JComponent getInformation(Xref xref){
		
		if(desktop.getSwingEngine().getCurrentOrganism() == null)
			return(new JLabel ("Organism not set for active pathway."));
	
		Xref mapped = idMapper(xref);
		
		if(mapped.getId().equals("")){
			return(new JLabel("<html>This identifier cannot be mapped to Ensembl.<br/>Check if the correct identifier mapping database is loaded.</html>"));
		}
			if(BiomartQueryService.isInternetReachable())
			{
				/*
				 * There were ambiguities in getting attributes dynamically so right now attributes are
				 * chosen statically but in future they will be made dynamic. 
				 */
				String [] attr = {"Associated Gene Name","Ensembl Gene ID","Ensembl Gene ID","PolyPhen prediction","PolyPhen score","SIFT prediction","SIFT score","Protein location (aa)","Reference ID","Minor allele frequency","Chromosome Location (bp)","Transcript location (bp)","Variant Alleles","Ensembl Transcript ID"};

				
				String organsim = Utils.mapOrganism(desktop.getSwingEngine().getCurrentOrganism().toString());
				if(organsim != null){

					Collection<String> attrs = new HashSet<String>();
					attrs.add("ensembl_gene_id");
					attrs.add("ensembl_transcript_id");
					attrs.add("external_id");
					attrs.add("external_gene_id");
					attrs.add("allele");
					attrs.add("minor_allele_freq");
					attrs.add("transcript_location");
					attrs.add("chromosome_location");
					attrs.add("sift_prediction_2076");
					attrs.add("sift_score_2076");
					attrs.add("polyphen_prediction_2076");
					attrs.add("polyphen_score_2076");
					attrs.add("peptide_location");
					
					Collection<String> identifierFilters = new HashSet<String>();
					identifierFilters.add(mapped.getId().toString());
					
					//Creating query for ensembl biomart
					Document result = BiomartQueryService.createQuery(organsim, attrs, identifierFilters,"TSV");
					
					//Querying enesmbl biomart
					InputStream is = BiomartQueryService.getDataStream(result);
					
					
					temp = matrixFromInputStream(is);
					String[][] temp_arr = new String[2][3];
					temp_arr[0][0]="Ensembl Gene ID";
					temp_arr[0][1]="Associated Gene Name";
					temp_arr[0][2]="No of SNPs";
					temp_arr[1][0]=null;
					temp_arr[1][1]=null;
					temp_arr[1][2]=null;
					
					for(int i=0;i<temp.get(0).length;i++) {
						if(temp.get(0)[i].equals("Ensembl Gene ID") ){
							temp_arr[1][0] = temp.get(1)[i];
						} else if (temp.get(0)[i].equals("Associated Gene Name") ){
							temp_arr[1][1] = temp.get(1)[i];
						}	
					}
					
					temp_arr[1][2] = String.valueOf((temp.size()-1));					
					
					if(temp.size() == 1){
						return new JLabel ("No information returned.");
					} else{
						resultPanel.removeAll();
						resultPanel.setLayout(new BoxLayout(resultPanel,BoxLayout.Y_AXIS));
						resultPanel.add((Utils.arrayToTable(temp_arr)));
						JButton show_table = new JButton("Show Table");
						show_table.setAlignmentX(Component.CENTER_ALIGNMENT);
						resultPanel.add(show_table);
						final TableDialog td = new TableDialog(this,arrayToTable(temp),attr);
						show_table.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e){
								td.setVisible(true);
							}
							
					    });
						return resultPanel;			
					}
				} else{
					return(new JLabel ("This organism is not supported by Ensembl."));
				}			
			} else {				
				JLabel jl = new JLabel ("Error: Cannot connect to the internet.");
				jl.setHorizontalAlignment(JLabel.RIGHT);
				return jl;			
			}
		}
	
	/**
	 * maps given id to the corresponding ensembl id
	 * 
	 * @param xref - id of the gene product to be mapped
	 * @return - mapped ensembl id
	 */
		private Xref idMapper(Xref xref){
			IDMapperStack mapper;
			
			if(xref.getDataSource().toString().equals("Ensembl")){
				return xref;			
			}
			else{
				mapper = desktop.getSwingEngine().getGdbManager().getCurrentGdb();
				Set<Xref> result;
				try {
					result = mapper.mapID(xref, DataSource.getBySystemCode("En"));
				} catch (IDMapperException e) {
					e.printStackTrace();
					return (new Xref("",null));
				}
				if(result.isEmpty())
					return (new Xref("",null));
				else
					return (result.iterator().next());
			}
		}
		
		/**
		 * helper function to make a list of values obtained by splitting each line of InputStream with a 
		 * tab delimiter
		 * 
		 * @param is - input stream
		 * @return - matrix
		 */
		
		private ArrayList<String []> matrixFromInputStream(InputStream is){
			ArrayList<String []> al = new ArrayList<String []>(); 
			int count = 0;
			BufferedReader br = null;
			String line;
			try {
				br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					al.add(line.split("\t"));
					count++;		
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
			return al;
		}
		
		/**
		 * A wrapper function to wrap two function calls
		 * @param m - A list of String array
		 * @return - JScrollPane containing table formed from the list
		 */
		
		private JScrollPane arrayToTable(final ArrayList<String []> m ) {
			
			return tempTableToTable(arrayToTempTable(m));  
		}

		
		/**
		 *  Updates information shown in info panel but updating elements attached to the JPanel.
		 *  It also removes attributes that are disabled by the user.
		 *  
		 * @param jp - JPanel that needs to be edited
		 * @param attr - List of attributes that need to be removed
		 */
		public void sendResult(JPanel jp,List<String> attr) {
			
			BorderLayout layout = (BorderLayout) jp.getLayout();
			jp.remove(layout.getLayoutComponent(BorderLayout.CENTER));
			JTable temp_table = arrayToTempTable(temp);
			temp_table.setDefaultRenderer(Object.class, new Renderer());
			for(int i=0;i<attr.size();i++){
				temp_table.removeColumn(temp_table.getColumn(attr.get(i)));
				
			}			
			jp.add(tempTableToTable(temp_table),BorderLayout.CENTER);			
			jp.revalidate();
			jp.repaint();
		}
		
		
		/**
		 * Makes a table from a list of string arrays
		 * 
 		 * @param m - list of string array
		 * @return - table formed from the list of string array
		 */
		private JTable arrayToTempTable(final ArrayList<String []> m ){
	
			dataModel = new AbstractTableModel() {
				 String[] columnNames= m.get(0);
		          
		          public int getRowCount() { return (m.size()-1);}
		          public Object getValueAt(int row, int col) { 
		        	  return m.get(row+1)[col]; 
		          }
		          public String getColumnName(int column) {	        	 
					return columnNames[column];
		        	}
		          public int getColumnCount(){
		              return columnNames.length;
		          }
		          
		      };
		      
		      //Setting sorting row functionality on each column of the table
		      sorter = new TableRowSorter<TableModel>(dataModel);
		      JTable table = new JTable(dataModel);
		      
		      //Setting custom renderer to color the prediction fields
		      table.setDefaultRenderer(Object.class, new Renderer());
		      table.setRowSorter(sorter); 	
		      
		      table.setAutoCreateRowSorter(true);
		      sorter.setRowFilter(RowFilter.regexFilter("T/C"));
		      
			return table;
			
		}
		
		/**
		 * Puts a table inside a scroll pane
		 */
		private JScrollPane tempTableToTable(JTable table){
		      JScrollPane scrollpane = new JScrollPane(table,  JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);		      
			return scrollpane;
		}
		
}