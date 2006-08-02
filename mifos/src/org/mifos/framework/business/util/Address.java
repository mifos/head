package org.mifos.framework.business.util;

import org.mifos.framework.util.helpers.StringUtils;


public class Address {

	private String line1;

	private String line2;

	private String line3;

	private String city;

	private String state;

	private String country;

	private String zip;

	private String phoneNumber;
	
	public Address() {
		super();
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLine1() {
		return line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public String getLine2() {
		return line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	public String getLine3() {
		return line3;
	}

	public void setLine3(String line3) {
		this.line3 = line3;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}
	
	public String getDisplayAddress(){
		String displayAddress="";
		if(StringUtils.isNullAndEmptySafe(getLine1())){			
			displayAddress = getLine1();
		}
		if(StringUtils.isNullAndEmptySafe(getLine2())&& StringUtils.isNullAndEmptySafe(getLine1())){
			displayAddress+=", "+ getLine2();
		}
		else if(StringUtils.isNullAndEmptySafe(getLine2())){
			displayAddress = getLine2();
		}
		if(StringUtils.isNullAndEmptySafe(getLine3())&& StringUtils.isNullAndEmptySafe(getLine2())||(StringUtils.isNullAndEmptySafe(getLine3())&& StringUtils.isNullAndEmptySafe(getLine1()))){			
			displayAddress+=", "+ getLine3();			
		}
		else if(!StringUtils.isNullAndEmptySafe(getLine3())){			
			displayAddress+=getLine3();
		}
		return displayAddress;
	}
}
