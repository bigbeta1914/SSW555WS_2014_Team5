import java.io.IOException;
import java.text.ParseException;

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

			// marriage before birth check
			gcfr.checkMarriageBeforeBirth();

			// child birth before parent check
			gcfr.checkChildBirthBeforeParentBirth();

			// check for age > 100
			gcfr.isAgeGreaterThan100();

			// check to see if parent is less than 13 years old
			gcfr.isParentAgeIsLessThan13();

			// check for future birth, death, marriage, and divorce dates
			gcfr.checkFutureDate();
			
			// check for same sex marriages with children
			gcfr.checkSameSexMarriageWithChildren();
                        
            // check for parents who have more than three children with same birth date
            gcfr.checkMultiKidsBirthDate();
            
            // check for parent married to thier child
            gcfr.checkMarriedToChild();
            

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
