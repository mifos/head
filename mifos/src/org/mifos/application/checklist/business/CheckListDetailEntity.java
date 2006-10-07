package org.mifos.application.checklist.business;

import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.framework.business.PersistentObject;

public class CheckListDetailEntity extends PersistentObject {

	private final Integer detailId;

	private String detailText;

	private Short answerType;

	private final CheckListBO checkListBO;

	private SupportedLocalesEntity supportedLocales;

	public CheckListDetailEntity() {
		this.detailId = null;
		this.checkListBO = null;
	}

	public CheckListDetailEntity(String detailText, Short answerType,
			CheckListBO checkListBO,
			Short localeId) {
		this.detailId = null;
		this.detailText = detailText;
		this.answerType = answerType;
		this.checkListBO = checkListBO;
		this.supportedLocales = new SupportedLocalesEntity(localeId);
	}

	public Integer getDetailId() {
		return detailId;
	}

	public String getDetailText() {
		return this.detailText;
	}

	private void setDetailText(String detailText) {
		this.detailText = detailText;
	}

	public Short getAnswerType() {
		return this.answerType;
	}

	private void setAnswerType(Short answerType) {
		this.answerType = answerType;
	}

	public SupportedLocalesEntity getSupportedLocales() {
		return this.supportedLocales;
	}

	private void setSupportedLocales(SupportedLocalesEntity supportedLocales) {
		this.supportedLocales = supportedLocales;
	}

	public CheckListBO getCheckListBO() {
		return checkListBO;
	}

}
