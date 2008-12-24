/**
 * 
 */
package org.mifos.framework.util.helpers;

import org.apache.commons.beanutils.Converter;
import org.mifos.framework.util.LocalizationConverter;

public class MifosDoubleConverter implements Converter {

	public MifosDoubleConverter() {
	}

	public Object convert(Class type, Object value) {
		Double returnValue=null;
		if(value!=null && type!=null && !"".equals(value)){
			try {
			if(value instanceof String) {
				returnValue=LocalizationConverter.getInstance().getDoubleValueForCurrentLocale((String)value);
			}
			else {
				returnValue=LocalizationConverter.getInstance().getDoubleValueForCurrentLocale(value.toString());
			}
			}catch(NumberFormatException ne) {
				ne.printStackTrace();
			}
		}
		return returnValue;
	}

}
