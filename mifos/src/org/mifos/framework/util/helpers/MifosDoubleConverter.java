/**
 * 
 */
package org.mifos.framework.util.helpers;

import org.apache.commons.beanutils.Converter;

public class MifosDoubleConverter implements Converter {

	public MifosDoubleConverter() {
	}

	public Object convert(Class type, Object value) {
		Double returnValue=null;
		if(value!=null && type!=null && !"".equals(value)){
			try {
			if(value instanceof String) {
				returnValue=Double.valueOf((String)value);
			}
			else {
				returnValue=Double.valueOf(value.toString());
			}
			}catch(NumberFormatException ne) {
				ne.printStackTrace();
			}
		}
		return returnValue;
	}

}
