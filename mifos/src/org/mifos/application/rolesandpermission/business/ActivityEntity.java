package org.mifos.application.rolesandpermission.business;

import java.util.HashSet;
import java.util.Set;

import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.framework.business.PersistentObject;

public class ActivityEntity extends PersistentObject {

	private final Short id;

	private ActivityEntity parent;

	private final LookUpValueEntity activityNameLookupValues;

	private final LookUpValueEntity descriptionLookupValues;

	private Short localeId;

	private final Set<RoleActivityEntity> roles = 
		new HashSet<RoleActivityEntity>();

	protected ActivityEntity() {
		this.id = null;
		this.parent = null;
		this.activityNameLookupValues = null;
		this.descriptionLookupValues = null;
	}
	
	ActivityEntity(int id) {
		this.id = (short)id;
		this.parent = null;
		this.activityNameLookupValues = null;
		this.descriptionLookupValues = null;
	}
	
	public ActivityEntity(short id, ActivityEntity parentActivityEntity, LookUpValueEntity lookUpValueEntity) {
		this.id = id;
		this.parent = parentActivityEntity;
		this.activityNameLookupValues = lookUpValueEntity;
		this.descriptionLookupValues = this.activityNameLookupValues;
	}

	public Short getId() {
		return id;
	}

	public ActivityEntity getParent() {
		return parent;
	}

	public LookUpValueEntity getActivityNameLookupValues() {
		return activityNameLookupValues;
	}

	public LookUpValueEntity getDescriptionLookupValues() {
		return descriptionLookupValues;
	}

	public Short getLocaleId() {
		return localeId;
	}

	public void setLocaleId(Short localeId) {
		this.localeId = localeId;
	}

	public String getDescription() {
		// can we get rid of this null check?
		if (localeId == null) {
			return null;
		}
		return MessageLookup.getInstance().lookup(getActivityNameLookupValues());
	}

	public String getActivityName() {
		// can we get rid of this null check?
		if (localeId == null) {
			return null;
		}
		return MessageLookup.getInstance().lookup(getActivityNameLookupValues());

	}

	public void setParent(ActivityEntity parent) {
		this.parent = parent;
	}

	public Set<RoleActivityEntity> getRoles() {
		return roles;
	}

}
