package org.mifos.application.accounts.util.helpers;

public class ApplicableCharge {

	private String feeId;

	private String feeName;

	private String amountOrRate;

	private String formula;

	private String periodicity;

	private String paymentType;

	public ApplicableCharge() {

	}

	public ApplicableCharge(String feeId, String feeName, String amountOrRate,
			String formula, String periodicity,String paymentType) {
		this.feeId = feeId;
		this.feeName = feeName;
		this.amountOrRate = amountOrRate;
		this.formula = formula;
		this.periodicity = periodicity;
		this.paymentType=paymentType;
	}

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

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	

	
}
