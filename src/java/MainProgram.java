import java.io.IOException;

import ssw555.project.team5.GEDCOMFileReader;

public class MainProgram {
	public static void main(String args[]) {
		try {
			GEDCOMFileReader gcfr = new GEDCOMFileReader();
			gcfr.readFile("C:/Users/Owner/Downloads/ssw555P02.ged");
			gcfr.printIndividuals();
			gcfr.printFamilies();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
