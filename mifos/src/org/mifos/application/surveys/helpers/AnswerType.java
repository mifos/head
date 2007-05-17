package org.mifos.application.surveys.helpers;

public enum AnswerType {
	FREETEXT(2),
	NUMBER(3),
	CHOICE(4),
	DATE(5);
	
	private int value;
	
	private AnswerType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public String getString() {
		return this.toString();
	}
	
	public static AnswerType fromInt(int type) {
		for (AnswerType candidate : AnswerType.values()) {
			if (type == candidate.getValue()) {
				return candidate;
			}
		}
		throw new RuntimeException("no answer type " + type);
	}
}
