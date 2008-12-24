package org.mifos.framework.business.util;

import org.mifos.framework.util.helpers.StringUtils;

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
	public String getDisplayName(){
		StringBuffer displayName=new StringBuffer("");
		if(!StringUtils.isNullOrEmpty(firstName))
			displayName.append(firstName);
		if(!StringUtils.isNullOrEmpty(middleName))
			displayName.append(" ").append(middleName);
		if(!StringUtils.isNullOrEmpty(secondLastName))
			displayName.append(" ").append(secondLastName);
		if(!StringUtils.isNullOrEmpty(lastName))
			displayName.append(" ").append(lastName);
		return displayName.toString();
	}
}
