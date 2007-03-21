package org.mifos.application.personnel.util.helpers;


public enum PersonnelLevel {

	LOAN_OFFICER(1), 
	NON_LOAN_OFFICER(2);

	private short value;

	private PersonnelLevel(int value) {
		this.value = (short) value;
	}

	public Short getValue() {
		return value;
	}

	public static PersonnelLevel fromInt(int id) {
		for (PersonnelLevel candidate : PersonnelLevel.values()) {
			if (candidate.value == id) {
				return candidate;
			}
		}
		throw new RuntimeException("no level " + id);
	}

}
