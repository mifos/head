package org.mifos.application.surveys.helpers;


public enum SurveyType {
	
	CLIENT("client"),
	GROUP("group"),
	CENTER("center"),
	LOAN("loan"),
	SAVINGS("savings"),
	ALL("all");
	
	private String type;
	
	private SurveyType(String type) {
		this.type = type;
	}
	
	public String getValue() {
		return type;
	}
	
	public static SurveyType fromString(String type) {
		for (SurveyType candidate : SurveyType.values()) {
			if (type.toLowerCase().equals(candidate.getValue())) {
				return candidate;
			}
		}
		
		throw new RuntimeException("no survey type " + type);
	}

}
