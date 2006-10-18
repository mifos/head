package org.mifos.application.master.business;

import org.mifos.framework.business.View;

public class LookUpMaster extends View {

	private Integer id;

	private Integer lookUpId;

	private String lookUpValue;

	public LookUpMaster() {
	}

	public LookUpMaster(java.lang.Integer lookUpId, String lookUpValue) {

		this.lookUpId = lookUpId;
		this.lookUpValue = lookUpValue;
	}

	public LookUpMaster(java.lang.Short id, java.lang.Integer lookUpId,
			String lookUpValue) {

		this.lookUpId = lookUpId;
		this.lookUpValue = lookUpValue;
		this.id = id.intValue();
	}

	public LookUpMaster(java.lang.Integer id, java.lang.Integer lookUpId,
			String lookUpValue) {

		this.lookUpId = lookUpId;
		this.lookUpValue = lookUpValue;
		this.id = id;
	}

	public java.lang.Integer getId() {
		return id;
	}

	public java.lang.Integer getLookUpId() {
		return lookUpId;
	}

	public java.lang.String getLookUpValue() {
		return lookUpValue;
	}

}
