import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * This Java program demonstrate line by line reading using GEDCOMFileReader in Java
 *
 * @author Michael Lyerly
 *
 */

public class GEDCOMFileReader{  

private static String[] VALID_TAGS = {"INDI", "NAME", "SEX", "BIRT", "DEAT", "FAMC", "FAMS", "FAM", "MARR", "HUSB", "WIFE", "CHIL", "DIV", "DATE", "TRLR", "NOTE"};

public void readFile(String file) throws IOException {
 
	FileInputStream fis = null;
        BufferedReader br = null;
     
	try {
     
		fis = new FileInputStream(file);
		br = new BufferedReader(new InputStreamReader(fis));
         
		System.out.println("Reading GEDCOM File line by line using GEDCOMFileReader");      

		String line = null;
		
		while ((line = br.readLine()) != null) {
			System.out.println(" LINE: " + line);
			System.out.println("LEVEL: " + parseLevelNumber (line));
			System.out.println("  TAG: " + (isValidTag(parseTag (line)) ? parseTag (line) : "Invalid Tag " + parseTag (line)));
			System.out.println();
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

public  String parseLevelNumber (String line) {
	return Character.toString(line.charAt(0));
}

public  String parseTag (String line) {
	String subStrg = line.substring(line.indexOf(" "));
	String tag = subStrg.substring(subStrg.indexOf(" ")+1);
	
	return (tag.indexOf(" ") == -1) ? tag : tag.substring(0, tag.indexOf(" "));
//	return isValidTag(tag) ? tag : "Invalid Tag: " + tag;
	 
}

public  boolean isValidTag (String tag) {
	for (String validTag: VALID_TAGS) {
		if(validTag.equalsIgnoreCase(tag)){
			return true;
		}

	}
	return false;
}

public static void main(String args[]) {
	try {
		new GEDCOMFileReader().readFile("C:/Users/Owner/Downloads/ssw555P02.ged");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

}