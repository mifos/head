package org.mifos.application.surveys.helpers;

public enum QuestionState {
	INACTIVE(0),
	ACTIVE(1);
	
	private int value;
	
	private QuestionState(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public static QuestionState fromInt(int state) {
		for (QuestionState candidate : QuestionState.values()) {
			if (state == candidate.getValue()) {
				return candidate;
			}
		}
		throw new RuntimeException("no question state " + state);
	}
}
