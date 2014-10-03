package ssw555.project.team5.model;

import java.util.ArrayList;

public class GEDCOMFamilyRecord extends GEDCOMRecord {
	private String husband;
	private String wife;
	private String type;
	private String married;
	private String divorce;
	private String current;
	private ArrayList <String> children = new ArrayList <String> ();
	
	public String getHusband() {
		return husband;
	}
	public void setHusband(String husband) {
		this.husband = husband;
	}
	public String getWife() {
		return wife;
	}
	public void setWife(String wife) {
		this.wife = wife;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMarried() {
		return married;
	}
	public void setMarried(String married) {
		this.married = married;
	}
	public String getDivorce() {
		return divorce;
	}
	public void setDivorce(String divorce) {
		this.divorce = divorce;
	}
	public String getCurrent() {
		return current;
	}
	public void setCurrent(String current) {
		this.current = current;
	}
	public ArrayList<String> getChildren() {
		return children;
	}
	public void setChildren(String children) {
		this.children.add(children);
	}
	
}