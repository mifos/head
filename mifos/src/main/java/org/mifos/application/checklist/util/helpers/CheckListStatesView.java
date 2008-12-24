package org.mifos.application.checklist.util.helpers;

import java.io.Serializable;

public class CheckListStatesView implements Serializable {

	private String stateName;

	private Short stateId;

	private Short id;

	public CheckListStatesView(Short stateId, String stateName, Short id) {
		this.stateId = stateId;
		this.stateName = stateName;
		this.id = id;
	}

	public Short getStateId() {
		return stateId;
	}

	public String getStateName() {
		return stateName;
	}

	public Short getId() {
		return id;
	}
}
