/**
 * 
 */
package org.mifos.application.master.business;

import java.util.Set;

import org.mifos.framework.business.PersistentObject;

/**
 * @author rohitr
 *
 */
public abstract class MasterDataEntity extends PersistentObject {
	private Short localeId;
	
	/** The composite primary key value */
	private Short id;
	
	/** The value of the lookupValue association. */
	private LookUpValueEntity lookUpValue;
	
	public MasterDataEntity(){}
	
	public MasterDataEntity(Short id){
		this.id=id;
	}
	
	public MasterDataEntity(Short id, Short localeId){
		this.id=id;
		this.localeId=localeId;
	}
	
	public Short getId() {
		return id;
	}
	
	protected void setId(Short id) {
		this.id = id;
	}
	
	protected LookUpValueEntity getLookUpValue() {
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
		if(localeId==null)
			return null;
		String name=null;
		Set<LookUpValueLocaleEntity> lookupSet = getLookUpValue().getLookUpValueLocales();
		for(LookUpValueLocaleEntity entity : lookupSet){
			if(entity.getLocaleId().equals(localeId.shortValue())){
				name=entity.getLookUpValue();
			}
		}
		return name;
	}
	
	public String getName(){
		return getName(this.localeId);
	}
	
	public Set<LookUpValueLocaleEntity> getNames() {
		return getLookUpValue().getLookUpValueLocales();
	}
}
