package ssw555.project.team5.model;

import java.util.ArrayList;

public class GEDCOMIndividualRecord extends GEDCOMRecord {
	private String name;
	private String givenName;
	private String surName;
	private String marriedName;
	private String sex;
	private String birth;
	private String isDead;
	private String death;
	private ArrayList <String> familyChildOf = new ArrayList <String> ();
	private ArrayList <String> familySpouseOf = new ArrayList <String> ();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGivenName() {
		return givenName;
	}
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	public String getSurName() {
		return surName;
	}
	public void setSurName(String surName) {
		this.surName = surName;
	}
	public String getMarriedName() {
		return marriedName;
	}
	public void setMarriedName(String marriedName) {
		this.marriedName = marriedName;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getBirth() {
		return birth;
	}
	public void setBirth(String birth) {
		this.birth = birth;
	}
	public String getIsDead() {
		return isDead;
	}
	public void setIsDead(String isDead) {
		this.isDead = isDead;
	}
	public String getDeath() {
		return death;
	}
	public void setDeath(String death) {
		this.death = death;
	}
	public ArrayList<String> getFamilyChildOf() {
		return familyChildOf;
	}
	public void setFamilyChildOf(String familyChildOf) {
		this.familyChildOf.add(familyChildOf);
	}
	public ArrayList<String> getFamilySpouseOf() {
		return familySpouseOf;
	}
	public void setFamilySpouseOf(String familySpouseOf) {
		this.familySpouseOf.add(familySpouseOf);
	}	
}