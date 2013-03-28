package org.mifos.platform.questionnaire.domain;

import java.io.Serializable;


public class InformationOrder implements Serializable {

    private static final long serialVersionUID = 1L;
    
	private Integer id;
	private String name;
	private  Integer additionalQuestionId;
	private String page;
	private Integer order;
	
	public InformationOrder() {	
		super();
	}
	
	public InformationOrder(Integer id, String name,
			Integer additionalQuestionId, String page, Integer order) {
		this.id = id;
		this.name = name;
		this.additionalQuestionId = additionalQuestionId;
		this.page = page;
		this.order = order;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAdditionalQuestionId() {
		return additionalQuestionId;
	}
	public void setAdditionalName(Integer additionalQuestionId) {
		this.additionalQuestionId = additionalQuestionId;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	
}
