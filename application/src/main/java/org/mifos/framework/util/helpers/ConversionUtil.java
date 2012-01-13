/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.framework.util.helpers;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.struts.action.ActionForm;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.exceptions.ValueObjectConversionException;

/**
 * Helper class which has methods to operate upon valueobjects.
 */
public class ConversionUtil {

    public static void populateBusinessObject(ActionForm actionForm, AbstractBusinessObject object, Locale locale)
            throws ValueObjectConversionException {

        try {

            if (null != object) {
                ConvertUtilsBean conBean = new ConvertUtilsBean();
                MifosSqlDateConverter converter = new MifosSqlDateConverter();
                MifosDoubleConverter mifosDoubleConverter = new MifosDoubleConverter();
                MifosStringToJavaUtilDateConverter stringToJavaDateConverter = new MifosStringToJavaUtilDateConverter();
                converter.setLocale(locale);
                conBean.register(stringToJavaDateConverter, java.util.Date.class);
                conBean.register(converter, java.sql.Date.class);
                conBean.register(mifosDoubleConverter, Double.class);

                BeanUtilsBean bean = new BeanUtilsBean(conBean, BeanUtilsBean.getInstance().getPropertyUtils());
                bean.copyProperties(object, actionForm);
            } else {
                throw new IllegalArgumentException("business object was null");
            }
        } catch (InvocationTargetException e) {
            throw new ValueObjectConversionException(e);
        } catch (IllegalAccessException e) {
            throw new ValueObjectConversionException(e);
        } catch (Exception e) {
            throw new ValueObjectConversionException(e);
        }
    }
    
    public static String formatNumber(final String number) {
    	String num = "";
        String decimal = "";
        int index = number.lastIndexOf('.');
        
        if(index >= 0) {
            num = number.substring(0, index);
            decimal = number.substring(index);
        } else {
            num = number;
        }
        
        StringBuilder builder = new StringBuilder(num);
        if(num.length() > 3){
        for(int i = num.length() - 3; i >= 0; i -= 4) {            
        	builder = builder.insert(i, ',');
        	}
        }
        if(!decimal.equalsIgnoreCase(".0")) {
        builder = builder.append(decimal);
        }
        return builder.toString();
    }
}
