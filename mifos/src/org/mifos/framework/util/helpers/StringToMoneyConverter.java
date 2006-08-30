/**

 * StringToMoneyConverter.java    version: 1.0



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */
package org.mifos.framework.util.helpers;

import org.apache.commons.beanutils.Converter;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.configuration.business.SystemConfiguration;

/**
 * This class is used by bean utils to convert string to money object.
 * This converter can be registered with BeanUtils so that it can convert
 * string to money.
 */
public class StringToMoneyConverter implements Converter {

	private SystemConfiguration config;
	
	public StringToMoneyConverter(){
		this.config = Configuration.getInstance().getSystemConfig();
	}
		
	/**
	 * As of now this method returns a new Money object with the currency set to default currency
	 * and amount set to the BigDecimal obtained from the value passed to it.
	 * @see org.apache.commons.beanutils.Converter#convert(java.lang.Class, java.lang.Object)
	 */
	public Object convert(Class clazz, Object value) {
		String amnt = (String)value;
		MifosCurrency defaultCurrency = null; 
		defaultCurrency = config.getCurrency();
		if(amnt=="" || amnt.trim().equals("."))
			return new Money(defaultCurrency,0);
		else
			return new Money(defaultCurrency,Double.valueOf(amnt));
	}

}
