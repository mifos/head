package org.mifos.application.accounts.util.helpers;

public class ApplicableCharge {
	
	private String feeId;
	private String feeName;
	private String amountOrRate;
	private String formula;
	private String periodicity;
	
	public String getFeeId() {
		return feeId;
	}
	public void setFeeId(String feeId) {
		this.feeId = feeId;
	}
	public String getFeeName() {
		return feeName;
	}
	public void setFeeName(String feeName) {
		this.feeName = feeName;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	public String getAmountOrRate() {
		return amountOrRate;
	}
	public void setAmountOrRate(String amountOrRate) {
		this.amountOrRate = amountOrRate;
	}
	public String getPeriodicity() {
		return periodicity;
	}
	public void setPeriodicity(String periodicity) {
		this.periodicity = periodicity;
	}
}
