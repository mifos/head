package org.mifos.application.ppi.helpers;

public enum Country {
	INDIA(1);
	
	private int value;
	
	private Country(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public static Country fromInt(int id) {
		for (Country country : Country.values())
			if (country.getValue() == id)
				return country;
		throw new RuntimeException("No countries have id " + id);
	}
}
