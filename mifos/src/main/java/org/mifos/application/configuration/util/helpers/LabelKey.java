package org.mifos.application.configuration.util.helpers;


/**
 * This class encapsulate the key used to index the locale specific string 
 * in labelCache map 
 */

public class LabelKey {
	
	private String key;
	
	private Short localeId;
	
	public LabelKey(String key,Short localeId) {
		this.localeId = localeId;
		this.key = key;
	}
	@Override
	public String toString() {
		
		return "[localeId="+localeId+"]"+"[key="+key+"]";
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof LabelKey))
			return false;
		else {
			LabelKey newObj = (LabelKey) obj;
			if (newObj.localeId.shortValue()==this.localeId.shortValue()
					&& newObj.key.equalsIgnoreCase(this.key))
				return true;
			else
				return false;
		}
	}
	@Override
	public int hashCode() {
		return this.localeId.hashCode() + this.key.hashCode();
	}

}
