
package org.mifos.application.checklist.business;


import java.util.LinkedHashSet;
import java.util.Set;

import org.mifos.application.checklist.persistence.service.CheckListPersistenceService;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.framework.business.BusinessObject;

public abstract class CheckListBO  extends BusinessObject{	

	public CheckListBO() {
		checklistDetailSet = new LinkedHashSet<CheckListDetailEntity>();//orderof details needs to be preserved
		checkListPersistenceService = new CheckListPersistenceService();
	}

	private Short checklistId;	
	private String checklistName;
	private Short checklistStatus;
	private Set<CheckListDetailEntity> checklistDetailSet;		
	private SupportedLocalesEntity supportedLocales;	
	
	private CheckListPersistenceService checkListPersistenceService ;
	

	public Short getChecklistId() {
		return checklistId;
	}

	private void setChecklistId(Short checklistId) {
		this.checklistId = checklistId;
	}

	public String getChecklistName() {
		return this.checklistName;
	}

	public void setChecklistName(String checklistName) {
		this.checklistName = checklistName;
	}

	public Short getChecklistStatus() {
		return this.checklistStatus;
	}

	public void setChecklistStatus(Short checklistStatus) {
		this.checklistStatus = checklistStatus;
	}

	public Set<CheckListDetailEntity> getChecklistDetailSet() {
		return this.checklistDetailSet;

	}

	private void setChecklistDetailSet(Set<CheckListDetailEntity> checklistDetailSet) {
		this.checklistDetailSet = checklistDetailSet;
	}	

	public SupportedLocalesEntity getSupportedLocales() {
		return this.supportedLocales;
	}

	public void setSupportedLocales(SupportedLocalesEntity supportedLocales) {
		this.supportedLocales = supportedLocales;
	}
	
	public void addChecklistDetail(CheckListDetailEntity checkListDetailEntity){	
		checkListDetailEntity.setCheckListBO(this);
		checklistDetailSet.add(checkListDetailEntity);
	}	
	
	public void create(){
		checkListPersistenceService.save(this);
	}
	
	public void delete(){
		checkListPersistenceService.delete(this);
	}
	
	public void update(){
		checkListPersistenceService.update(this);
	}
	
	public Short getEntityID(){
		return null;
	}
	
}
