
package org.mifos.application.checklist.business;

import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.framework.business.PersistentObject;

public class CheckListDetailEntity extends PersistentObject {	

	private static final long serialVersionUID = 8768768L;
	
	public CheckListDetailEntity() {
	}	

	private Integer detailId;	
	private String detailText;
	private Short answerType;
	private CheckListBO checkListBO;	
	private SupportedLocalesEntity supportedLocales;	

	public Integer getDetailId() {
		return detailId;
	}

	public void setDetailId(Integer detailId) {
		this.detailId = detailId;
	}
	
	public String getDetailText() {
		return this.detailText;
	}

	public void setDetailText(String detailText) {
		this.detailText = detailText;
	}
	
	public Short getAnswerType() {
		return this.answerType;
	}

	public void setAnswerType(Short answerType) {
		this.answerType = answerType;
	}	

	public SupportedLocalesEntity getSupportedLocales() {
		return this.supportedLocales;
	}

	public void setSupportedLocales(SupportedLocalesEntity supportedLocales) {
		this.supportedLocales = supportedLocales;
	}

	public CheckListBO getCheckListBO() {
		return checkListBO;
	}

	public void setCheckListBO(CheckListBO checkListBO) {
		this.checkListBO = checkListBO;
	}	
	
}
