package org.mifos.application.accounting.business;

import org.mifos.framework.business.AbstractBusinessObject;

public class CoaBranchBO extends AbstractBusinessObject{

private int	coaid;
private String glcode;
private String globalofficenum;
private String coaname;

public int getCoaid() {
	return coaid;
}
public void setCoaid(int coaid) {
	this.coaid = coaid;
}

public String getGlcode() {
	return glcode;
}
public void setGlcode(String glcode) {
	this.glcode = glcode;
}
public String getGlobalofficenum() {
	return globalofficenum;
}
public void setGlobalofficenum(String globalofficenum) {
	this.globalofficenum = globalofficenum;
}

public String getCoaname() {
	return coaname;
}
public void setCoaname(String coaname) {
	this.coaname = coaname;
}
}
