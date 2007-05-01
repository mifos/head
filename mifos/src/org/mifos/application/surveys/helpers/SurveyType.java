package org.mifos.application.surveys.helpers;

public enum SurveyType {
	CUSTOMERS("customers"),
	ACCOUNTS("accounts"),
	BOTH("both");
	
	private String type;
	
	private SurveyType(String type) {
		this.type = type;
	}
	
	public String getValue() {
		return type;
	}
	
	public static SurveyType fromString(String type) {
		for (SurveyType candidate : SurveyType.values()) {
			if (type.equals(candidate.getValue())) {
				return candidate;
			}
		}
		
		throw new RuntimeException("no survey type " + type);
	}

}
