/**
 * 
 */
package org.mifos.application.master.business;

import java.util.Set;

import org.mifos.application.master.MessageLookup;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.StringUtils;

/**
 * Subclasses of this class provide access to the database tables
 * which correspond to enum-like classes.  
 * 
 * The replacement for a subclass of this class generally will be
 * an enum.
 * We generally expect to move looking up messages from the database
 * for localization (language and MFI) to {@link MessageLookup}.
 */
public abstract class MasterDataEntity extends PersistentObject {
	// values which override localized values are stored with locale =1 	
	public static Short CUSTOMIZATION_LOCALE_ID = (short)1;
	
	private Short localeId;

	/** The composite primary key value */
	private Short id;

	/** The value of the lookupValue association. */
	private LookUpValueEntity lookUpValue;

	public MasterDataEntity() {
	}

	public MasterDataEntity(Short id) {
		this.id = id;
	}

	public MasterDataEntity(Short id, Short localeId) {
		this.id = id;
		this.localeId = localeId;
	}

	public Short getId() {
		return id;
	}

	protected void setId(Short id) {
		this.id = id;
	}

	public LookUpValueEntity getLookUpValue() {
		return lookUpValue;
	}

	protected void setLookUpValue(LookUpValueEntity lookUpValue) {
		this.lookUpValue = lookUpValue;
	}

	public Short getLocaleId() {
		return localeId;
	}

	public void setLocaleId(Short localeId) {
		this.localeId = localeId;
	}

	/*
	 * We ignore the locale, in order to treat values in the database
	 * as customized values for all locales.
	 */
	public String getName(Short localeId) {
		// test cases depend upon the null locale behavior
		// it seems like a hack which should be refactored
		// TODO: remove test dependency on null localeId behavior
		//if (localeId == null) {
		//	return null;
		//}
		String name = MessageLookup.getInstance().lookup(getLookUpValue()); 
		return name;
		
	}

	public String getName() {
		return getName(getLocaleId());
	}

	/* 
	 * This method is currently used just for insuring that 
	 * all data is loaded within a given Hibernate session.
	 */
	public Set<LookUpValueLocaleEntity> getNames() {
		return getLookUpValue().getLookUpValueLocales();
	}

	/* Jan 18, 2008 work in progress 
	 * 
	 */
	protected void setName(Short localeId, String name) {
		// only support overrides for localeId = 1 
		localeId = CUSTOMIZATION_LOCALE_ID;
		if (localeId != null && StringUtils.isNullAndEmptySafe(name)) {
			Set<LookUpValueLocaleEntity> lookupSet = getLookUpValue()
					.getLookUpValueLocales();
			for (LookUpValueLocaleEntity entity : lookupSet) {
				if (entity.getLocaleId().equals(localeId.shortValue())
						&& (entity.getLookUpValue() == null 
						|| !entity.getLookUpValue().equals(name))) {
					entity.setLookUpValue(name);
				}
			}
		}

	}
}
