package org.mifos.application.rolesandpermission.business;

import java.util.Set;

import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.framework.business.PersistentObject;

public class ActivityEntity extends PersistentObject {

	private Short id;

	private ActivityEntity parent;

	private LookUpValueEntity activityNameLookupValues;

	private LookUpValueEntity descriptionLookupValues;
	
	private Short localeId;

	public Short getId() {
		return id;
	}

	public void setId(Short id) {
		this.id = id;
	}

	public ActivityEntity getParent() {
		return parent;
	}

	public void setParent(ActivityEntity parent) {
		this.parent = parent;
	}

	public LookUpValueEntity getActivityNameLookupValues() {
		return activityNameLookupValues;
	}

	public void setActivityNameLookupValues(LookUpValueEntity activityNameLookupValues) {
		this.activityNameLookupValues = activityNameLookupValues;
	}

	public LookUpValueEntity getDescriptionLookupValues() {
		return descriptionLookupValues;
	}

	public void setDescriptionLookupValues(LookUpValueEntity descriptionLookupValues) {
		this.descriptionLookupValues = descriptionLookupValues;
	}
	
	public Short getLocaleId() {
		return localeId;
	}
	
	public void setLocaleId(Short localeId) {
		this.localeId = localeId;
	}

	public String getDescription() {
		if(localeId==null)
			return null;
		String name=null;
		Set<LookUpValueLocaleEntity> lookupSet = getDescriptionLookupValues().getLookUpValueLocales();
		for(LookUpValueLocaleEntity entity : lookupSet){
			if(entity.getLocaleId().equals(localeId.shortValue())){
				name=entity.getLookUpValue();
			}
		}
		return name;
	}

	public String getActivtyName(){
		if(localeId==null)
			return null;
		String name=null;
		Set<LookUpValueLocaleEntity> lookupSet = getActivityNameLookupValues().getLookUpValueLocales();
		for(LookUpValueLocaleEntity entity : lookupSet){
			if(entity.getLocaleId().equals(localeId.shortValue())){
				name=entity.getLookUpValue();
			}
		}
		return name;
	}
	

}
