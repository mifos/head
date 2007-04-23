package org.mifos.application.surveys.helpers;

public enum SurveyState {
	ACTIVE(1),
	INACTIVE(0);

	private final int value;

	private SurveyState(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static SurveyState fromInt(int state) {
		for (SurveyState candidate : SurveyState.values()) {
			if (state == candidate.getValue()) {
				return candidate;
			}
		}
		throw new RuntimeException("no survey state " + state);
	}

}
