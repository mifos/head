package org.mifos.application.checklist.util.helpers;

public class CheckListStatesView {

	private String stateName;

	private Short stateId;

	//Id can be product type entity id for account type
	//and can be customer level entity id for customer type
	private Short id;
	
	public CheckListStatesView(Short stateId, String stateName, Short id){
		this.stateId = stateId;
		this.stateName = stateName;
		this.id = id;
	}

	public Short getStateId() {
		return stateId;
	}

	public void setStateId(Short stateId) {
		this.stateId = stateId;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public Short getId() {
		return id;
	}

	public void setId(Short id) {
		this.id = id;
	}



}
