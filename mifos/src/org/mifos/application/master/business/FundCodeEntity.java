package org.mifos.application.master.business;

import java.io.Serializable;

public class FundCodeEntity implements Serializable {
	private final Short fundCodeId;
	
	private String fundCodeValue;
	
	public FundCodeEntity(String fundCode) {
		this.fundCodeId = null;
		this.fundCodeValue = fundCode;
	}
	
	protected FundCodeEntity() {
		this.fundCodeId = null;
		this.fundCodeValue = null;
	}

	public String getFundCodeValue() {
		return fundCodeValue;
	}

	public void setFundCodeValue(String fundCode) {
		this.fundCodeValue = fundCode;
	}

	public Short getFundCodeId() {
		return fundCodeId;
	}
}
