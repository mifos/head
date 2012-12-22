package org.mifos.dto.domain;

public class CoaNamesDto {
private int sno;
private int glcodeId;
private String coaName;
private String Amount;
private String transactionnotes;
private String glcodeValue;
private String glcode;
public String getGlcode() {
	return glcode;
}
public void setGlcode(String glcode) {
	this.glcode = glcode;
}
public String getAmount() {
	return Amount;
}
public void setAmount(String amount) {
	Amount = amount;
}
public String getTransactionnotes() {
	return transactionnotes;
}
public void setTransactionnotes(String transactionnotes) {
	this.transactionnotes = transactionnotes;
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
