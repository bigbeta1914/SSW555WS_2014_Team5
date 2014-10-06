package ssw555.project.team5;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import ssw555.project.team5.model.GEDCOMFamilyRecord;
import ssw555.project.team5.model.GEDCOMIndividualRecord;
import ssw555.project.team5.model.GEDCOMObject;

/**
 * 
 * This Java program demonstrate line by line reading using GEDCOMFileReader in
 * Java
 * 
 * @author SSW-555 Fall 2014 Team 5
 * 
 */

public class GEDCOMFileReader {

//	private static String[] VALID_TOP_LEVEL_TAGS = { "INDI", "FAM", "TRLR",	"NOTE" };
//	private static String[] VALID_FIRST_LEVEL_TAGS = { "NAME", "SEX", "BIRT", "DEAT", "FAMC", "FAMS", "MARR", "HUSB", "WIFE", "CHIL", "DIV" };
//	private static String[] VALID_SECOND_LEVEL_TAGS = { "DATE" };

	private LinkedHashMap<String, GEDCOMIndividualRecord> individuals = new LinkedHashMap<String, GEDCOMIndividualRecord>();
	private LinkedHashMap<String, GEDCOMFamilyRecord> families = new LinkedHashMap<String, GEDCOMFamilyRecord>();

	private GEDCOMObject parseLine(String line) {
		GEDCOMObject gedcomObj = new GEDCOMObject();

		String[] parseLine = (line.split("\\s+"));
		int level = Integer.valueOf(parseLine[0]);
		String tag = parseLine[1];
		String arguments = new String();

		if (parseLine.length > 2) {
			for (int i = 2; i < parseLine.length; i++) {
				arguments = arguments.concat(" ").concat(parseLine[i]);
			}
		}

		gedcomObj.setLevel(level);
		gedcomObj.setTag(tag);
		gedcomObj.setArguments(arguments.trim());

		return gedcomObj;
	}

	private String retrieveXrefId(String xrefId) {
		return xrefId.replace("@", "");
	}
	
	// pre: assumes a date string in format "DD MMM YYYY"
	private Calendar convertStringToDate(String dateString) {
		Calendar returnCalendar = Calendar.getInstance();
		String[] parseLine = (dateString.split("\\s+"));
		
		returnCalendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(parseLine[0]));
		
		switch(parseLine[1]) {
		case "JAN":
			returnCalendar.set(Calendar.MONTH, Calendar.JANUARY);
			break;
		case "FEB":
			returnCalendar.set(Calendar.MONTH, Calendar.FEBRUARY);
			break;
		case "MAR":
			returnCalendar.set(Calendar.MONTH, Calendar.MARCH);
			break;
		case "APR":
			returnCalendar.set(Calendar.MONTH, Calendar.APRIL);
			break;
		case "MAY":
			returnCalendar.set(Calendar.MONTH, Calendar.MAY);
			break;
		case "JUN":
			returnCalendar.set(Calendar.MONTH, Calendar.JUNE);
			break;
		case "JUL":
			returnCalendar.set(Calendar.MONTH, Calendar.JULY);
			break;
		case "AUG":
			returnCalendar.set(Calendar.MONTH, Calendar.AUGUST);
			break;
		case "SEP":
			returnCalendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
			break;
		case "OCT":
			returnCalendar.set(Calendar.MONTH, Calendar.OCTOBER);
			break;
		case "NOV":
			returnCalendar.set(Calendar.MONTH, Calendar.NOVEMBER);
			break;
		case "DEC":
			returnCalendar.set(Calendar.MONTH, Calendar.DECEMBER);
			break;
		}
		
		returnCalendar.set(Calendar.YEAR, Integer.valueOf(parseLine[2]));
		
		return returnCalendar;
	}
	
	public void checkDeathBeforeBirth() {
			// get collection of individuals
			Collection indivCollection = individuals.values();
			
			// iterator for collection
			Iterator indivIterator = indivCollection.iterator();
			
			// iterate through all individuals
			while(indivIterator.hasNext()) {
				GEDCOMIndividualRecord ind = (GEDCOMIndividualRecord) indivIterator.next(); // not sure if this works
				
				//System.out.println("ind.getBirth() = " + ind.getBirth());
				//System.out.println("ind.getDeath() = " + ind.getDeath());
				
				// only check if both birth and death have a date
				if( (ind.getBirth() != null) && (ind.getDeath() != null) )
				{
					// convert birth and death date strings to Calendar objects
					Calendar calandarBirth = convertStringToDate(ind.getBirth());
					Calendar calendarDeath = convertStringToDate(ind.getDeath());
					
					// do the check
					if(calendarDeath.before(calandarBirth))
					{
						System.out.println("ERROR - DeathBeforeBirth: name = " + ind.getName() + " " + ind.getSurName() 
								+", death date = " + ind.getDeath() + " is before birth date = " + ind.getBirth());
					}
				}
			}
	}
	
	public void checkDeathBeforeMarriage() {
		// get collection of families
		Collection famCollection = families.values();
		
		// iterator for collection
		Iterator famIterator = famCollection.iterator();
		
		// iterate through all individuals
		while(famIterator.hasNext()) {
			GEDCOMFamilyRecord fam = (GEDCOMFamilyRecord) famIterator.next(); // not sure if this works
			
			// only check if both birth and death have a date
			if( fam.getMarried() != null )
			{
				// convert marriage date to Calendar object
				Calendar calendarMarried = convertStringToDate(fam.getMarried());
				
				GEDCOMIndividualRecord ind;
				
				// get husband
				ind = individuals.get(fam.getHusband());
				
				for( int i = 0; i < 2; i++ )
				{
					if( ind.getDeath() != null )
					{
						// convert birth to Calendar object
						Calendar calendarDeath = convertStringToDate(ind.getDeath());
						// do the check
						if(calendarDeath.before(calendarMarried))
						{
							System.out.println("ERROR - DeathBeforeMarriage: name = " + ind.getName() + " " + ind.getSurName() 
									+", death date = " + ind.getDeath() + " is before marriage date = " + fam.getMarried());
						}
						
						ind = individuals.get(fam.getWife());
					}
				}
			}
		}
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
			GEDCOMIndividualRecord ind = null;
			GEDCOMFamilyRecord fam = null;
			GEDCOMObject previousGEDCOMObj = null;
			
			while ((line = br.readLine()) != null) {
				GEDCOMObject currentGEDCOMObj = parseLine(line);

//				System.out.println("    LEVEL: " + currentGEDCOMObj.getLevel());
//				System.out.println("      TAG: " + currentGEDCOMObj.getTag());
//				System.out.println("ARGUEMTNS: " + currentGEDCOMObj.getArguments());

				switch (currentGEDCOMObj.getLevel()) {

				case 0:
					switch (currentGEDCOMObj.getArguments()) {
					
					case "INDI":
						if (ind != null) {
							individuals.put(ind.getUniqueId(), ind);
							ind = null;
						}
						ind = new GEDCOMIndividualRecord();
						ind.setUniqueId(retrieveXrefId(currentGEDCOMObj.getTag()));
						break;

					case "FAM":
						if (ind != null) {
							individuals.put(ind.getUniqueId(), ind);
							ind = null;
						}
						if (fam != null) {
							families.put(fam.getUniqueId(), fam);
							fam = null;						
						}
						fam = new GEDCOMFamilyRecord();
						fam.setUniqueId(retrieveXrefId(currentGEDCOMObj.getTag()));
						break;

					default:
						if (fam != null) {
							families.put(fam.getUniqueId(), fam);
							fam = null;						
						}
						break;
					}
					break;

				case 1:
					switch (currentGEDCOMObj.getTag()) {
					
					case "NAME":
						ind.setName(currentGEDCOMObj.getArguments());
						break;

					case "SEX":
						ind.setSex(currentGEDCOMObj.getArguments());
						break;

					case "FAMS":
						ind.setFamilySpouseOf(retrieveXrefId(currentGEDCOMObj.getArguments()));
						break;

					case "FAMC":
						ind.setFamilyChildOf(retrieveXrefId(currentGEDCOMObj.getArguments()));
						break;

					case "HUSB":
						fam.setHusband(retrieveXrefId(currentGEDCOMObj.getArguments()));
						break;

					case "WIFE":
						fam.setWife(retrieveXrefId(currentGEDCOMObj.getArguments()));
						break;
						
					case "CHIL":
						fam.setChildren(retrieveXrefId(currentGEDCOMObj.getArguments()));
						break;
						
					case "_CURRENT":
						fam.setCurrent(currentGEDCOMObj.getArguments());
						break;

					}
					break;

				case 2:
					switch (currentGEDCOMObj.getTag()) {
					
					case "GIVN":
						ind.setGivenName(currentGEDCOMObj.getArguments());
						break;

					case "SURN":
						ind.setSurName(currentGEDCOMObj.getArguments());
						break;

					case "_MARNM":
						ind.setMarriedName(currentGEDCOMObj.getArguments());
						break;

					case "DATE":
						switch (previousGEDCOMObj.getTag()) {
						
						case "BIRT":
							ind.setBirth(currentGEDCOMObj.getArguments());
							break;

						case "DEAT":
							ind.setDeath(currentGEDCOMObj.getArguments());
							break;
							
						case "MARR":
							fam.setMarried(currentGEDCOMObj.getArguments());
							break;

						case "DIV":
							fam.setDivorce(currentGEDCOMObj.getArguments());
							break;
						}
						break;

					}
					break;

				default:
					break;
				}

				previousGEDCOMObj = currentGEDCOMObj;

			}

		} catch (FileNotFoundException ex) {
			Logger.getLogger(GEDCOMFileReader.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(GEDCOMFileReader.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				System.out.println("INDIVIDUALS: " + this.individuals.size());
				System.out.println("FAMILES: " + this.families.size());
				br.close();
				fis.close();
			} catch (IOException ex) {
				Logger.getLogger(GEDCOMFileReader.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

}