package org.mifos.application.rolesandpermission.business;

import java.util.Set;

import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.framework.business.PersistentObject;

public class ActivityEntity extends PersistentObject {

	private final Short id;

	private final ActivityEntity parent;

	private final LookUpValueEntity activityNameLookupValues;

	private final LookUpValueEntity descriptionLookupValues;

	private Short localeId;

	protected ActivityEntity() {
		this.id = null;
		this.parent = null;
		this.activityNameLookupValues = null;
		this.descriptionLookupValues = null;
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
		if (localeId == null)
			return null;
		String name = null;
		Set<LookUpValueLocaleEntity> lookupSet = getDescriptionLookupValues()
				.getLookUpValueLocales();
		for (LookUpValueLocaleEntity entity : lookupSet) {
			if (entity.getLocaleId().equals(localeId.shortValue())) {
				name = entity.getLookUpValue();
			}
		}
		return name;
	}

	public String getActivityName() {
		if (localeId == null)
			return null;
		String name = null;
		Set<LookUpValueLocaleEntity> lookupSet = getActivityNameLookupValues()
				.getLookUpValueLocales();
		for (LookUpValueLocaleEntity entity : lookupSet) {
			if (entity.getLocaleId().equals(localeId.shortValue())) {
				name = entity.getLookUpValue();
			}
		}
		return name;
	}

}
