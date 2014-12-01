import java.io.IOException;
import java.text.ParseException;

import ssw555.project.team5.GEDCOMFileReader;

public class MainProgram {

    public static void main(String args[]) {
        try {
            GEDCOMFileReader gcfr = new GEDCOMFileReader();
            gcfr.readFile("src/data/acceptanceTestFile.ged");

            // US01 - death before birth check
            gcfr.checkDeathBeforeBirth();

            // US03 - death before marriage check
            gcfr.checkDeathBeforeMarriage();

            // US04 - marriage before birth check
            gcfr.checkMarriageBeforeBirth();

            // US05 - child birth before parent check
            gcfr.checkChildBirthBeforeParentBirth();
            
            // US06 - check for parent married to their child
            gcfr.checkMarriedToChild();
            
            // US08 - check for future birth, death, marriage, and divorce dates
            gcfr.checkFutureDate();
            
            // US09 - check individual married to self
            gcfr.checkMarriedToSelf();

            // US10 - check individual is child of self
            gcfr.checkChildOfSelf();
            
            // US11 - check for parents who have more than three children with same birth date
            gcfr.checkMultiKidsBirthDate();
            
            // US12, US17 - check for same sex marriages with children
            gcfr.checkSameSexMarriageWithChildren();
            
            // US13 - check for multiple spouses at the same time
            gcfr.checkConcurrentSpouses();
            
            // US14 - check for married under 16
            gcfr.checkMarriedWhenUnder16();
            
            // US15 - check for age > 100
            gcfr.isAgeGreaterThan100();

            // US16 - check to see if parent is less than 13 years old
            gcfr.isParentAgeIsLessThan13();
            


            // US19 - check individuals divorce date before marriage date
            gcfr.checkIndividualsDivorceDatePriorMarriageDate();
            
            // US20 - check for Male/Female listed as Wife/Husband respectively, 
            //        not necessarily same sex marriage
            gcfr.checkHusbWifeListedAsFemaleMale();

            // US21 - Individual indicated as not dead, yet a death date is provided
            gcfr.checkNotDeadWithDeathDate();
            

            // US22 - check for duplicate child entries
            gcfr.checkDuplicateChildEntries();
            

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
