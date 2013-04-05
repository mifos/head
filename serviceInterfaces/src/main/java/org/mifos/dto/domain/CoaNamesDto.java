package org.mifos.dto.domain;

public class CoaNamesDto {
private int sno;
private int glcodeId;
private String coaName;
private String amount;
private String trxnnotes;
private String glcodeValue;


	

public String getAmount() {
	return amount;
}
public void setAmount(String amount) {
	this.amount = amount;
}

public String getTrxnnotes() {
	return trxnnotes;
}
public void setTrxnnotes(String trxnnotes) {
	this.trxnnotes = trxnnotes;
}
public int getSno() {
	return sno;
}
public void setSno(int sno) {
	this.sno = sno;
}
public int getGlcodeId() {
	return glcodeId;
}
public void setGlcodeId(int glcodeId) {
	this.glcodeId = glcodeId;
}
public String getCoaName() {
	return coaName;
}
public void setCoaName(String coaName) {
	this.coaName = coaName;
}
public String getGlcodeValue() {
	return glcodeValue;
}
public void setGlcodeValue(String glcodeValue) {
	this.glcodeValue = glcodeValue;
}
}
