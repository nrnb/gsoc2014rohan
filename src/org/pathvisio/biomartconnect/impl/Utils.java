package org.pathvisio.biomartconnect.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;
import org.bridgedb.Xref;
import org.pathvisio.desktop.PvDesktop;

/**
 * Contains common functions and mappings for all the modules. 
 * 
 * @author mkutmon
 * @author Rohan Saxena
 */

public class Utils {
	
	/**
	 * maps between BridgeDb species names
	 * and Ensembl BioMart species names
	 */
	public static String mapOrganism(String organism) {
		Map<String, String> orgMap = new HashMap<String, String>();
		orgMap.put("BosTaurus", "btaurus_gene_ensembl");
		orgMap.put("CaenorhabditisElegans", "celegans_gene_ensembl");
		orgMap.put("CanisFamiliaris", "cfamiliaris_gene_ensembl");
		orgMap.put("DanioRerio", "drerio_gene_ensembl");
		orgMap.put("DasypusNovemcinctus", "dnovemcinctus_gene_ensembl");
		orgMap.put("DrosophilaMelanogaster", "dmelanogaster_gene_ensembl");
		orgMap.put("EchinposTelfairi", "etelfairi_gene_ensembl");
		orgMap.put("EquusCaballus", "ecaballus_gene_ensembl");
		orgMap.put("GallusGallus", "ggallus_gene_ensembl");	
		orgMap.put("HomoSapiens", "hsapiens_gene_ensembl");
		orgMap.put("LoxodontaAfricana", "lafricana_gene_ensembl");
		orgMap.put("MacacaMulatta", "mmulatta_gene_ensembl");
		orgMap.put("MusMusculus", "mmusculus_gene_ensembl");
		orgMap.put("MonodelphisDomestica", "mdomestica_gene_ensembl");
		orgMap.put("OrnithorhynchusAnatinus", "oanatinus_gene_ensembl");
		orgMap.put("OryziasLatipes", "olatipes_gene_ensembl");
		orgMap.put("OryctolagusCuniculus", "ocuniculus_gene_ensembl");
		orgMap.put("PanTroglodytes", "ptroglodytes_gene_ensembl");
		orgMap.put("SusScrofa", "sscrofa_gene_ensembl");
		orgMap.put("RattusNorvegicus", "rnorvegicus_gene_ensembl");
		orgMap.put("SaccharomycesCerevisiae", "scerevisiae_gene_ensembl");
		orgMap.put("SorexAraneus", "saraneus_gene_ensembl");
		orgMap.put("TetraodonNigroviridis", "tnigroviridis_gene_ensembl");		
		orgMap.put("XenopusTropicalis", "xtropicalis_gene_ensembl");
		
		if(orgMap.containsKey(organism)) {
			return orgMap.get(organism);
		}
		return null;
	}
	
	/**
	 * Converts array to a dataModel and builds a JTable using this dataModel
	 * 
	 * @param m - array to be converted
	 * @return - JScrollPane containing table created
	 */
	
	public static JScrollPane arrayToTable(final String[][] m) {

		TableModel dataModel = new AbstractTableModel() {
			String[] columnNames = { "Attribute", "Value" };

			public int getRowCount() {
				return m[0].length;
			}

			public Object getValueAt(int row, int col) {
				return m[col][row];
			}

			public String getColumnName(int column) {
				return columnNames[column];
			}

			public int getColumnCount() {
				return columnNames.length;
			}
		};

		JTable table = new JTable(dataModel);
		JScrollPane scrollpane = new JScrollPane(table);
		return scrollpane;
	}
	

	/**
	 * maps given id to the corresponding ensembl id
	 */
	
	public static Xref mapId(Xref xref, PvDesktop desktop) {

		IDMapperStack mapper = desktop.getSwingEngine().getGdbManager().getCurrentGdb();
		
		if(xref.getDataSource().getSystemCode().equals("En")){
			return xref;			
		} else {
			try {
				Set<Xref> result = mapper.mapID(xref, DataSource.getBySystemCode("En"));
				if(result.isEmpty())
					return (new Xref("",null));
				else
					return (result.iterator().next());
			} catch (IDMapperException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return (new Xref("",null));
			}
		}
	}
	
	/**
	 * Reads input stream and builds the contents as a string
	 * 
	 * @param is - InputStream to be converted to the string
	 * @return - string formed
	 */
	
	public static String getStringFromInputStream(InputStream is) {
		 
		int count = 0;
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;
		
		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
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
		sb.deleteCharAt(sb.length()-1);
		if(count == 1){
			return("Invalid");
		} else {
			return sb.toString();
		}
	}

	
}
