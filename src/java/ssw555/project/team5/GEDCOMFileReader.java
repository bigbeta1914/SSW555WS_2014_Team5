package ssw555.project.team5;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    private boolean isNullOrBlank(String stg) {
        return (null == stg || stg.trim().length() <= 0);
    }

    // pre: assumes a date string in format "DD MMM YYYY"
    private Calendar convertStringToDate(String dateString) {
        Calendar returnCalendar = Calendar.getInstance();
        String[] parseLine = (dateString.split("\\s+"));

        returnCalendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(parseLine[0]));

        switch (parseLine[1]) {
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
        while (indivIterator.hasNext()) {
            GEDCOMIndividualRecord ind = (GEDCOMIndividualRecord) indivIterator.next(); // not sure if this works

            //System.out.println("ind.getBirth() = " + ind.getBirth());
            //System.out.println("ind.getDeath() = " + ind.getDeath());
            // only check if both birth and death have a date
            if ((ind.getBirth() != null) && (ind.getDeath() != null)) {
                // convert birth and death date strings to Calendar objects
                Calendar calandarBirth = convertStringToDate(ind.getBirth());
                Calendar calendarDeath = convertStringToDate(ind.getDeath());

                // do the check
                if (calendarDeath.before(calandarBirth)) {
                    System.out.println("ERROR - DeathBeforeBirth: id = " + ind.getUniqueId()
                            + ", death date = " + ind.getDeath() + " is before birth date = " + ind.getBirth());
                }
            }
        }
    }

    public void checkDeathBeforeMarriage() {
        // get collection of families
        Collection<GEDCOMFamilyRecord> famCollection = families.values();

        // iterator for collection
        Iterator<GEDCOMFamilyRecord> famIterator = famCollection.iterator();

        // iterate through all families
        while (famIterator.hasNext()) {
            GEDCOMFamilyRecord fam = (GEDCOMFamilyRecord) famIterator.next();

            // only check if marriage has a date
            if (fam.getMarried() != null) {
                // convert marriage date to Calendar object
                Calendar calendarMarried = convertStringToDate(fam.getMarried());

                GEDCOMIndividualRecord ind;

                // get husband
                ind = individuals.get(fam.getHusband());

                for (int i = 0; i < 2; i++) {
                    // only check if death has a date
                    if (ind.getDeath() != null) {
                        // convert birth to Calendar object
                        Calendar calendarDeath = convertStringToDate(ind.getDeath());
                        // do the check
                        if (calendarDeath.before(calendarMarried)) {
                            System.out.println("ERROR - DeathBeforeMarriage: ID = " + ind.getUniqueId()
                                    + ", death date = " + ind.getDeath() + " is before marriage date = " + fam.getMarried());
                        }

                        ind = individuals.get(fam.getWife());
                    }
                }
            }
        }
    }

    public void checkMarriageBeforeBirth() {

        // get collection of families
        Collection<GEDCOMFamilyRecord> famCollection = families.values();

        // iterator for collection
        Iterator<GEDCOMFamilyRecord> famIterator = famCollection.iterator();

        // iterate through all families
        while (famIterator.hasNext()) {
            GEDCOMFamilyRecord fam = (GEDCOMFamilyRecord) famIterator.next();

            // only check if marriage has a date
            if (fam.getMarried() != null) {
                // convert marriage date to Calendar object
                Calendar calendarMarried = convertStringToDate(fam.getMarried());

                GEDCOMIndividualRecord ind;

                // get husband
                ind = individuals.get(fam.getHusband());

                for (int i = 0; i < 2; i++) {
                    // only check if birth has a date (may be omitted)
                    if (ind.getBirth() != null) {
                        // convert birth to Calendar object
                        Calendar calendarBirth = convertStringToDate(ind.getBirth());
                        // do the check
                        if (calendarMarried.before(calendarBirth)) {
                            System.out.println("ERROR - MarriageBeforeBirth: ID = " + ind.getUniqueId()
                                    + ", marriage date = " + fam.getMarried() + " is before birth date = " + ind.getBirth());
                        }

                        ind = individuals.get(fam.getWife());
                    }
                }
            }
        }

    }

    public void checkFutureDate() throws ParseException {

        for (String key : individuals.keySet()) {
            GEDCOMIndividualRecord ind = (GEDCOMIndividualRecord) individuals.get(key);

            Date birth = null;
            if (isNullOrBlank(ind.getBirth())) {
            } else {
                birth = new SimpleDateFormat("dd MMM yyyy").parse(ind.getBirth());

                if (birth.after(new Date())) {
                    System.out.println("ERROR - individual with ID = " + ind.getUniqueId() + " has birth date after current date.");
                }

            }

            Date death = null;
            if (isNullOrBlank(ind.getDeath())) {

            } else {
                death = new SimpleDateFormat("dd MMM yyyy").parse(ind.getDeath());

                if (death.after(new Date())) {
                    System.out.println("ERROR - individual with ID = " + ind.getUniqueId() + " has death date after current date.");
                }
            }
        }

        for (String key : families.keySet()) {
            GEDCOMFamilyRecord fam = (GEDCOMFamilyRecord) families.get(key);

            Date married = null;
            if (isNullOrBlank(fam.getMarried())) {
            } else {
                married = new SimpleDateFormat("dd MMM yyyy").parse(fam.getMarried());

                if (married.after(new Date())) {
                    System.out.println("ERROR - " + fam.getHusband() + " & " + fam.getWife() + " married date is after current date.");
                }

            }

            Date divorce = null;
            if (isNullOrBlank(fam.getDivorce())) {
            } else {
                divorce = new SimpleDateFormat("dd MMM yyyy").parse(fam.getDivorce());

                if (divorce.after(new Date())) {
                    System.out.println("ERROR - " + fam.getHusband() + " & " + fam.getWife() + " divorce date is after current date.");
                }
            }
        }
    }

    public void checkChildBirthBeforeParentBirth() {
        // get collection of families
        Collection<GEDCOMFamilyRecord> famCollection = families.values();

        // iterator for collection
        Iterator<GEDCOMFamilyRecord> famIterator = famCollection.iterator();

        // iterate through all families
        while (famIterator.hasNext()) {
            GEDCOMFamilyRecord fam = (GEDCOMFamilyRecord) famIterator.next();

            // get children array
            ArrayList<String> childrenArrayList = fam.getChildren();

            // only check if family has children
            if (childrenArrayList != null) {
                // convert to child array, because I'm not sure how to work with an array list
                String[] childrenArray = new String[childrenArrayList.size()];
                childrenArray = childrenArrayList.toArray(childrenArray);

                // get husband
                GEDCOMIndividualRecord parent = individuals.get(fam.getHusband());

                // only continue if parent birthdate is provided (may be omitted)
                if (parent.getBirth() != null) {
                    // convert parent birth to Calendar object
                    Calendar calendarParentBirth = convertStringToDate(parent.getBirth());

                    // cycle through each child and compare birthdate to husband's birthdate
                    for (String childId : childrenArray) {
                        GEDCOMIndividualRecord child = individuals.get(childId);

                        // only check if child birthdate is provided (may be omitted)
                        if (child.getBirth() != null) {
                            // convert child birth to Calendar object
                            Calendar calendarChildBirth = convertStringToDate(child.getBirth());
                            // do the check
                            if (calendarChildBirth.before(calendarParentBirth)) {
                                System.out.println("ERROR - ChildBirthBeforeParentBirth: child (" + child.getUniqueId() + ") birth date = " + child.getBirth() + " is before parent (" + parent.getUniqueId()
                                        + ") birth date = " + parent.getBirth());
                            }
                        } // if( child.getBirth() != null)
                    } // for( int i = 0; i < childrenArray.size(); i++ )
                } // if( parent.getBirth() != null)

                // get wife
                parent = individuals.get(fam.getWife());

                // only continue if parent birthdate is provided (may be omitted)
                if (parent.getBirth() != null) {
                    // convert parent birth to Calendar object
                    Calendar calendarParentBirth = convertStringToDate(parent.getBirth());

                    // cycle through each child and compare birthdate to husband's birthdate
                    for (String childId : childrenArray) {
                        GEDCOMIndividualRecord child = individuals.get(childId);

                        // only check if child birthdate is provided (may be omitted)
                        if (child.getBirth() != null) {
                            // convert child birth to Calendar object
                            Calendar calendarChildBirth = convertStringToDate(child.getBirth());
                            // do the check
                            if (calendarChildBirth.before(calendarParentBirth)) {
                                System.out.println("ERROR - ChildBirthBeforeParentBirth: child (" + child.getUniqueId() + ") birth date = " + child.getBirth() + " is before parent (" + parent.getUniqueId()
                                        + ") birth date = " + parent.getBirth());
                            }
                        } // if( child.getBirth() != null)
                    } // for( int i = 0; i < childrenArray.size(); i++ )
                } // if( parent.getBirth() != null)
            } // if( childrenArray != null )
        } // while(famIterator.hasNext())
    }

    public void isAgeGreaterThan100() {
        int age;
        // get collection of families
        Collection<GEDCOMFamilyRecord> famCollection = families.values();

        // iterator for collection
        Iterator<GEDCOMFamilyRecord> famIterator = famCollection.iterator();

        // iterate through all families
        while (famIterator.hasNext()) {
            GEDCOMFamilyRecord fam = (GEDCOMFamilyRecord) famIterator.next();

            // get husband
            GEDCOMIndividualRecord father = individuals.get(fam.getHusband());

            // only continue if father's birthdate is provided (may be omitted)
            if (father.getBirth() != null) {
                age = getAge(father.getBirth());
                if (age > 100) {
                    System.out.println("ANOMALY - isAgeGreaterThan100: individual with ID = " + father.getUniqueId() + " claims to be over 100 years old!");
                }
            }

            // get wife
            GEDCOMIndividualRecord mother = individuals.get(fam.getWife());

            // only continue if mother's birthdate is provided (may be omitted)
            if (mother.getBirth() != null) {
                age = getAge(mother.getBirth());
                if (age > 100) {
                    System.out.println("ANOMALY - isAgeGreaterThan100: individual with ID = " + mother.getUniqueId() + " claims to be over 100 years old!");
                }
            }

            // get children
            ArrayList<String> childrenArrayList = fam.getChildren();

            // only check if family has children
            if (childrenArrayList != null) {
                // convert to child array, because I'm not sure how to work with an array list
                String[] childrenArray = new String[childrenArrayList.size()];
                childrenArray = childrenArrayList.toArray(childrenArray);

                for (String childId : childrenArray) {
                    GEDCOMIndividualRecord child = individuals.get(childId);

                    // only check if child birthdate is provided (may be omitted)
                    if (child.getBirth() != null) {
                        age = getAge(child.getBirth());
                        if (age > 100) {
                            System.out.println("ANOMALY - isAgeGreaterThan100: individual with ID = " + child.getUniqueId() + " claims to be over 100 years old!");
                        }
                    }
                }
            }
        }
    }

    public int getAge(String birthday) {
        LocalDate now = LocalDate.now();
        String[] parseLine = (birthday.split("\\s+"));
        int birthDay = Integer.valueOf(parseLine[0]);
        int birthMonth = convertMonthToNumber(birthday);
        int birthYear = Integer.valueOf(parseLine[2]);
        LocalDate birthdate = LocalDate.of(birthYear, birthMonth, birthDay); //year, month, day
        Period p = Period.between(birthdate, now);
        return p.getYears();
    }

    public int convertMonthToNumber(String birthday) {
        String[] parseLine = (birthday.split("\\s+"));
        switch (parseLine[1]) {
            case "JAN":
                return 1;
            case "FEB":
                return 2;
            case "MAR":
                return 3;
            case "APR":
                return 4;
            case "MAY":
                return 5;
            case "JUN":
                return 6;
            case "JUL":
                return 7;
            case "AUG":
                return 8;
            case "SEP":
                return 9;
            case "OCT":
                return 10;
            case "NOV":
                return 11;
            case "DEC":
                return 12;
        }
        return 0;
    }

    public void isParentAgeIsLessThan13() {
        double age;
        // get collection of families
        Collection<GEDCOMFamilyRecord> famCollection = families.values();

        // iterator for collection
        Iterator<GEDCOMFamilyRecord> famIterator = famCollection.iterator();

        // iterate through all families
        while (famIterator.hasNext()) {
            GEDCOMFamilyRecord fam = (GEDCOMFamilyRecord) famIterator.next();

            // get children array
            ArrayList<String> childrenArrayList = fam.getChildren();

            // only check if family has children
            if (childrenArrayList != null) {

                // get husband
                GEDCOMIndividualRecord father = individuals.get(fam.getHusband());

                // get wife
                GEDCOMIndividualRecord mother = individuals.get(fam.getWife());

                // only continue if father's birthdate is provided (may be omitted)
                if (father.getBirth() != null) {
                    age = getAge(father.getBirth());
                    if (age < 13) {
                        System.out.println("ANOMALY - parentUnder13: parent (individual) with ID = " + father.getUniqueId() + " claims to be under 13");
                    }
                }

                // only continue if mother's birthdate is provided (may be omitted)
                if (mother.getBirth() != null) {
                    age = getAge(mother.getBirth());
                    if (age < 13) {
                        System.out.println("ANOMALY - parentUnder13: parent (individual) with ID = " + mother.getUniqueId() + " claims to be under 13");
                    }
                }
            }
        }
    }

    public void checkSameSexMarriageWithChildren() {
        for (String key : families.keySet()) {
            GEDCOMFamilyRecord fam = (GEDCOMFamilyRecord) families.get(key);

            if (isNullOrBlank(fam.getHusband())) {

            } else {
                GEDCOMIndividualRecord husband = (GEDCOMIndividualRecord) individuals.get(fam.getHusband());

                if (isNullOrBlank(fam.getWife())) {

                } else {
                    GEDCOMIndividualRecord wife = (GEDCOMIndividualRecord) individuals.get(fam.getWife());

                    if (husband.getSex().equals(wife.getSex())) {
                        System.out.println("ANOMALY - " + fam.getUniqueId() + " family has same sex marrige with husband: " + husband.getUniqueId() + " and wife: " + wife.getUniqueId());

                        if (fam.getChildren().size() > 0) {
                            System.out.println("ANOMALY - " + fam.getUniqueId() + " family has same sex marrige with children!");
                        }
                    }
                }
            }
        }
    }

    public void checkMarriedToSelf() {
        for (String key : families.keySet()) {
            GEDCOMFamilyRecord fam = (GEDCOMFamilyRecord) families.get(key);

            if (isNullOrBlank(fam.getHusband())) {

            } else {
                GEDCOMIndividualRecord husband = (GEDCOMIndividualRecord) individuals.get(fam.getHusband());

                if (isNullOrBlank(fam.getWife())) {

                } else {
                    GEDCOMIndividualRecord wife = (GEDCOMIndividualRecord) individuals.get(fam.getWife());

                    if (husband.getUniqueId().equals(wife.getUniqueId())) {
                        System.out.println("ERROR - " + husband.getUniqueId() + " listed as husband and wife to family " + fam.getUniqueId());
                    }
                }
            }
        }
    }

    public void checkChildOfSelf() {
        for (String key : families.keySet()) {
            GEDCOMFamilyRecord fam = (GEDCOMFamilyRecord) families.get(key);

            if (fam.getChildren().size() > 0) {
                ArrayList<String> children = fam.getChildren();

                for (int i = 0; i < children.size(); i++) {

                    if (!isNullOrBlank(fam.getHusband()) || !isNullOrBlank(fam.getWife())) {
                        if (children.get(i).equalsIgnoreCase(fam.getHusband()) || children.get(i).equalsIgnoreCase(fam.getWife())) {
                            System.out.println("ERROR - " + children.get(i) + " listed as parent and child to family " + fam.getUniqueId());
                        }
                    }
                }

            }
        }
    }

    public void checkMultiKidsBirthDate() {
        int count = 0;
        String age = "";
        String family = "";
        // get collection of families
        Collection<GEDCOMFamilyRecord> famCollection = families.values();

        // iterator for collection
        Iterator<GEDCOMFamilyRecord> famIterator = famCollection.iterator();

        // iterate through all families
        while (famIterator.hasNext()) {
            GEDCOMFamilyRecord fam = (GEDCOMFamilyRecord) famIterator.next();

            // first family to set the id
            if (family.isEmpty()) {
                family = fam.getUniqueId();
            }

            // get children array
            ArrayList<String> childrenArrayList = fam.getChildren();

            // only check if family has children
            if (childrenArrayList != null) {
                // convert to child array
                String[] childrenArray = new String[childrenArrayList.size()];
                childrenArray = childrenArrayList.toArray(childrenArray);

                for (String childId : childrenArray) {
                    GEDCOMIndividualRecord child = individuals.get(childId);

                    // only check if child birthdate is provided (may be omitted)
                    if (child.getBirth() != null) {
                        // reset the age string and family string and counter
                        if (!(fam.getUniqueId().equals(family))) {
                            family = fam.getUniqueId();
                            age = "";
                            count = 0;
                        }

                        if (age.isEmpty()) {
                            age = child.getBirth();
                        } else {
                            if (age.equals(child.getBirth())) {
                                count++;
                                if (count > 2) {
                                    System.out.println("ANAMOLY - Family " + fam.getUniqueId() + " has more "
                                            + "than 3 children with the same birth date!");
                                }
                            } else {
                                age = child.getBirth();
                            }
                        }
                    }
                }
            }
        }
    }

    public void checkMarriedToChild() {
        // get collection of families
        Collection<GEDCOMFamilyRecord> famCollection = families.values();

        // iterator for collection
        Iterator<GEDCOMFamilyRecord> famIterator = famCollection.iterator();

        // iterate through all families
        while (famIterator.hasNext()) {
            GEDCOMFamilyRecord fam = (GEDCOMFamilyRecord) famIterator.next();

            // get children array
            ArrayList<String> childrenArrayList = fam.getChildren();

            // only check if family has children
            if (childrenArrayList != null) {
                // convert to child array, because I'm not sure how to work with an array list
                String[] childrenArray = new String[childrenArrayList.size()];
                childrenArray = childrenArrayList.toArray(childrenArray);

                // get husband
                GEDCOMIndividualRecord husband = individuals.get(fam.getHusband());
                // get wife
                GEDCOMIndividualRecord wife = individuals.get(fam.getWife());

                // uncomment to print IDs for debug
                // System.out.println("husb id = " + husband.getUniqueId() + ", wife id = " + wife.getUniqueId());
                // cycle through each child
                for (String childId : childrenArray) {

                	// uncomment to print id for debug
                    // System.out.println("child id = " + childId);
                    // if husband is also child, then wife is married to child (check fails)
                    if (0 == husband.getUniqueId().compareTo(childId)) {

                        GEDCOMIndividualRecord child = individuals.get(childId);

                        System.out.println("ERROR - MarriedToChild: Wife ( ID = " + wife.getUniqueId() + ") is married to child (ID = " + child.getUniqueId() + ")");
                    }

                    // if wife is also child, then husband is married to child (check fails)
                    if (0 == wife.getUniqueId().compareTo(childId)) {

                        GEDCOMIndividualRecord child = individuals.get(childId);

                        System.out.println("ERROR - MarriedToChild: Husband ( ID = " + husband.getUniqueId() + ") is married to child (ID = " + child.getUniqueId() + ")");
                    }

                } // for (String childId : childrenArray)
            } // if( childrenArray != null )
        } // while(famIterator.hasNext())
    }

    public void concurrentSpouses() {
        // get collection of families
        Collection<GEDCOMFamilyRecord> famCollection = families.values();

        // iterator for collection
        Iterator<GEDCOMFamilyRecord> famIterator = famCollection.iterator();

        //Family map with couples
        Map<String, ArrayList> familyCouple = new HashMap<>();

        // iterate through all families
        while (famIterator.hasNext()) {
            GEDCOMFamilyRecord fam = (GEDCOMFamilyRecord) famIterator.next();

            // get husband
            GEDCOMIndividualRecord husband = individuals.get(fam.getHusband());
            GEDCOMIndividualRecord wife = individuals.get(fam.getWife());

            //if the death dates are null then they are alive...record them as married
            if (husband.getDeath() == null && wife.getDeath() == null) {
                //couple array list
                ArrayList<String> couples = new ArrayList<>();

                couples.add(husband.getUniqueId());
                couples.add(wife.getUniqueId());

                //need to store Family IDs with Husband-Wife ID list
                familyCouple.put(fam.getUniqueId(), couples);
            }
        } //end famIterator

        String husbandID = "";
        String wifeID = "";
        //need to iterate though the map and check if any husband or wife IDs are in muiltiple family IDs
        for (Map.Entry<String, ArrayList> entry : familyCouple.entrySet()) {
            ArrayList<String> member = entry.getValue();

            if (husbandID.isEmpty() && wifeID.isEmpty()) {
                husbandID = member.get(0);
                wifeID = member.get(1);
                continue;
            }

            if (husbandID.equalsIgnoreCase(member.get(0))) {
                System.out.println("ERROR - Husband " + member.get(0) + " has more than one concurrent wife!");
            } else if (wifeID.equalsIgnoreCase(member.get(1))) {
                System.out.println("ERROR - Wife " + member.get(1) + " has more than one concurrent husband!");
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

                            case "DEAT":
                                ind.setIsDead(currentGEDCOMObj.getArguments());
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
