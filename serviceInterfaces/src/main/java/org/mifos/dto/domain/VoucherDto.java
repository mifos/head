package org.mifos.dto.domain;

public class VoucherDto {
	private String sno;
	private String coaname;
	private String amount;
	private String transactionnotes;
	public String getSno() {
		return sno;
	}
	public void setSno(String sno) {
		this.sno = sno;
	}
	public String getCoaname() {
		return coaname;
	}
	public void setCoaname(String coaname) {
		this.coaname = coaname;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getTransactionnotes() {
		return transactionnotes;
	}
	public void setTransactionnotes(String transactionnotes) {
		this.transactionnotes = transactionnotes;
	}
}
