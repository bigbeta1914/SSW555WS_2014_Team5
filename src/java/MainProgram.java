import java.io.IOException;

import ssw555.project.team5.GEDCOMFileReader;

public class MainProgram {
	public static void main(String args[]) {
		try {
			GEDCOMFileReader gcfr = new GEDCOMFileReader();
			gcfr.readFile("src/data/acceptanceTestFile.ged");
			
			// death before birth check
			gcfr.checkDeathBeforeBirth();
			
			// death before marriage check
			gcfr.checkDeathBeforeMarriage();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
