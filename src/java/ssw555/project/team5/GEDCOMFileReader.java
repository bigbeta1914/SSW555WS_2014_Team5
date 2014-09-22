package ssw555.project.team5;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssw555.project.team5.model.GEDCOMFamily;
import ssw555.project.team5.model.GEDCOMIndividual;

/**
 * 
 * This Java program demonstrate line by line reading using GEDCOMFileReader in
 * Java
 * 
 * @author Michael Lyerly
 * 
 */

public class GEDCOMFileReader {

	private static String[] VALID_TAGS = { "INDI", "NAME", "SEX", "BIRT",
			"DEAT", "FAMC", "FAMS", "FAM", "MARR", "HUSB", "WIFE", "CHIL",
			"DIV", "DATE", "TRLR", "NOTE" };
	
	private List<GEDCOMIndividual> individuals = new ArrayList<GEDCOMIndividual>();
	private List<GEDCOMFamily> families = new ArrayList<GEDCOMFamily>();
	
	private String retrieveArguments(String[] parseLine){
		String arguments = "";
		for(int i=2; i < parseLine.length; i++){
			arguments = arguments + " " + parseLine[i];
		}
		return arguments.trim();
	}
	
	private String retrieveXrefId(String xrefId){
		return xrefId.replace("@", "");
	}

	public void readFile(String file) throws IOException {

		FileInputStream fis = null;
		BufferedReader br = null;

		try {

			fis = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fis));

			System.out.println("Reading GEDCOM File line by line using GEDCOMFileReader");
			System.out.println();

			String line = null;
			GEDCOMIndividual ind = null;
			GEDCOMFamily fam = null;
			boolean isIndOrFam = false;
			
			while ((line = br.readLine()) != null) {
				String[] parseLine = (line.split("\\s+"));
				int level = Integer.valueOf(parseLine[0]);
				String tag = parseLine[1];
				String arguments = (parseLine.length > 2) ? retrieveArguments(parseLine) : null;
				
//				System.out.println("    LEVEL: " + level);
//				System.out.println("      TAG: " + tag);
//				System.out.println("ARGUEMTNS: " + arguments);
				
				if(level == 0) {
					if("INDI".equals(arguments)){
						if(ind != null){
							individuals.add(ind);
						}
						ind = new GEDCOMIndividual();
						ind.setIdentifier(retrieveXrefId(tag));
						isIndOrFam = true;
					}
					else if("FAM".equals(arguments)){
						if(fam != null){
							families.add(fam);
						}
						fam = new GEDCOMFamily();
						fam.setIdentifier(retrieveXrefId(tag));
						isIndOrFam = true;
					} else {
						isIndOrFam = false;
					}
				}
				
				if(isIndOrFam){
					if("NAME".equals(tag)){
						ind.setName(arguments);
					} else if("HUSB".equals(tag)){
						fam.setHusband(retrieveXrefId(arguments));
					} else if("WIFE".equals(tag)){
						fam.setWife(retrieveXrefId(arguments));
					}	
				}				
			}

		} catch (FileNotFoundException ex) {
			Logger.getLogger(GEDCOMFileReader.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(GEDCOMFileReader.class.getName()).log(Level.SEVERE, null, ex);

		} finally {
			try {
				br.close();
				fis.close();
			} catch (IOException ex) {
				Logger.getLogger(GEDCOMFileReader.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	
	public String getIndividual(String xRefId){
		if(individuals != null && !individuals.isEmpty()){
			for(int i=0; i < individuals.size(); i++){
				GEDCOMIndividual indObj = individuals.get(i);
				if(indObj.getIdentifier().equals(xRefId)){
					return indObj.getName();
				}
			}			
		}
		return "Individual not found in GEDCOM File!";
	}

	
	public void printIndividuals(){
		System.out.println("Printing GEDCOM File Individuals:");

		if(individuals == null || individuals.isEmpty()){
			System.out.println("No individuals in GEDCOM File!");
		} else {
			for(int i=0; i < individuals.size(); i++){
				GEDCOMIndividual indObj = individuals.get(i);
				System.out.println("  ID: " + indObj.getIdentifier());
				System.out.println("NAME: " + indObj.getName());
				System.out.println();
			}	
		}
	}

	public void printFamilies(){
		System.out.println("Printing GEDCOM File Families:");
		
		if(families == null || families.isEmpty()){
			System.out.println("No families in GEDCOM File!");
		} else {
			for(int i=0; i < families.size(); i++){
				GEDCOMFamily famObj = families.get(i);
				System.out.println("     ID: " + famObj.getIdentifier());
				System.out.println("HUSBAND: " + getIndividual(famObj.getHusband()));
				System.out.println("   WIFE: " + getIndividual(famObj.getWife()));
				System.out.println();
			}			
		}
	}

}