/**

 * ConvertionUtil.java    version: 1.0



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

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.struts.action.ActionForm;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.exceptions.ValueObjectConversionException;

/**
 * Helper class which has methods to operate upon valueobjects.
 */
public class ConvertionUtil {

	public static void populateBusinessObject(ActionForm actionForm,
			BusinessObject object, Locale locale)
			throws ValueObjectConversionException {

		try {

			if (null != object) {
				// converter from String to sql Date
				ConvertUtilsBean conBean = new ConvertUtilsBean();
				MifosSqlDateConverter converter = new MifosSqlDateConverter();
				MifosDoubleConverter mifosDoubleConverter = new MifosDoubleConverter();
				converter.setLocale(locale);
				conBean.register(converter, java.sql.Date.class);
				conBean.register(mifosDoubleConverter, Double.class);
				// register for FormFile to BLOB conversion
				// MifosInputFileConverter fileConverter = new
				// MifosInputFileConverter();
				// conBean.register(fileConverter, FormFile.class);
				BeanUtilsBean bean = new BeanUtilsBean(conBean, BeanUtilsBean
						.getInstance().getPropertyUtils());
				bean.copyProperties(object, actionForm);

				// MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Convertion
				// valueObject to action form using bean utils", false, null);
			} else {
				throw new ValueObjectConversionException("");
			}
		} catch (InvocationTargetException e) {
			throw new ValueObjectConversionException(e);
		} catch (IllegalAccessException e) {
			throw new ValueObjectConversionException(e);
		} catch (Exception e) {
			throw new ValueObjectConversionException(e);
		}
	}
}
