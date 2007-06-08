package org.mifos.application.surveys.helpers;

import org.apache.commons.lang.StringUtils;

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
	
	// TODO: replace this with locale lookup logic
	public String getLocalizedName() {
		return StringUtils.capitalize(type);	
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
