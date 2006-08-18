package org.mifos.framework.business.util;

public class Name {
	
	private String firstName;

	private String middleName;

	private String lastName;

	private String secondLastName;



	public String getFirstName() {
		return firstName;
	}



	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}



	public String getLastName() {
		return lastName;
	}



	public void setLastName(String lastName) {
		this.lastName = lastName;
	}



	public String getMiddleName() {
		return middleName;
	}



	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}



	public String getSecondLastName() {
		return secondLastName;
	}



	public void setSecondLastName(String secondLastName) {
		this.secondLastName = secondLastName;
	}



	public Name() {

	}
	
	public Name(String firstName, String middleName, String secondLastName, String lastName) {
		this.firstName = firstName;
		this.middleName = middleName;
		this.secondLastName = secondLastName;
		this.lastName = lastName;
	}

}
