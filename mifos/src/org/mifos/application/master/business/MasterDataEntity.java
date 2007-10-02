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

	public String getName(Short localeId) {
		if (localeId == null)
			return null;
		String name = null;
		Set<LookUpValueLocaleEntity> lookupSet = getLookUpValue()
				.getLookUpValueLocales();
		for (LookUpValueLocaleEntity entity : lookupSet) {
			if (entity.getLocaleId().equals(localeId.shortValue())) {
				name = entity.getLookUpValue();
			}
		}
		return name;
	}

	public String getName() {
		return getName(getLocaleId());
	}

	public Set<LookUpValueLocaleEntity> getNames() {
		return getLookUpValue().getLookUpValueLocales();
	}

	protected void setName(Short localeId, String name) {
		if (localeId != null && StringUtils.isNullAndEmptySafe(name)) {
			Set<LookUpValueLocaleEntity> lookupSet = getLookUpValue()
					.getLookUpValueLocales();
			for (LookUpValueLocaleEntity entity : lookupSet) {
				if (entity.getLocaleId().equals(localeId.shortValue())
						&& !entity.getLookUpValue().equals(name)) {
					entity.setLookUpValue(name);
				}
			}
		}

	}
}
