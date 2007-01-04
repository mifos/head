/**
 
 * DecimalFormatValidatorCollection.java    version: xxx
 
 
 
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

package org.mifos.framework.struts.validators;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.Field;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.Resources;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.DecimalFieldHelper;
import org.mifos.framework.util.helpers.MethodInvoker;

public class DecimalFormatValidatorCollection {
	
	public DecimalFormatValidatorCollection() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * This class validates a decimal field of the action form.The format against which it would validate 
	 * will be obtained from the var elements defined in validation.xml. The name of the var element which is supposed 
	 * to hold the format should be 'format', and it should be specfied as (x,y) where x-y is the total number of digits before decimal  
	 * @param bean
	 * @param va
	 * @param field
	 * @param errors
	 * @param request
	 * @return
	 */
	public static boolean validateDecimalFormatCollection(
			Object bean,
			ValidatorAction va,
			Field field,
			ActionMessages errors,
			Validator validator,
			HttpServletRequest request){
		
		Double validatableField = null;
		boolean returnable = false;
		String format = null;
		String collectionType = null;
		Object fieldValue = null;
		String nestedPropertyName= null;
		String fieldToBeValidated = null;
		String[] propertyNamesNesting = null;
		try {
			fieldValue = MethodInvoker.invokeWithNoException(bean,"get"+field.getProperty(),new Object[]{},new Class[]{});
		} catch (SystemException e) {
			
			e.printStackTrace();
		} catch (ApplicationException e) {
			
			e.printStackTrace();
		}
		// get the field to be validated.
		
		nestedPropertyName = field.getVarValue("fieldName");
		propertyNamesNesting = nestedPropertyName.split("_");
//		get the format against which it has to be validated
		format = field.getVarValue("format");
		try{
			if(fieldValue instanceof List && fieldValue != null){
				
				for(Object obj : (List)fieldValue){
					Object targetReturned = obj;
					targetReturned = getFieldToBeValidated(propertyNamesNesting,targetReturned);
					if(null != targetReturned){
						fieldToBeValidated = targetReturned.toString();
						validatableField = new Double(fieldToBeValidated);
						validatableField = Math.abs(validatableField);
						// validate using a helper
						returnable= DecimalFieldHelper.validate(validatableField, format);
						if(! returnable){
							errors.add(field.getKey(),Resources.getActionMessage(validator,request,va,field));
							break;
							
						}
					}
				}
			}else if(fieldValue instanceof Set && fieldValue != null){
				for(Object obj : (Set)fieldValue){
					Object targetReturned = obj;
					targetReturned = getFieldToBeValidated(propertyNamesNesting,targetReturned);
					
					
					if(null != targetReturned){
						fieldToBeValidated = targetReturned.toString();
						validatableField = new Double(fieldToBeValidated);
						validatableField = Math.abs(validatableField);
						// validate using a helper
						returnable= DecimalFieldHelper.validate(validatableField, format);
						if(! returnable){
							errors.add(field.getKey(),Resources.getActionMessage(validator,request,va,field));
							break;
							
						}
					}
				}
			}else if(fieldValue instanceof Map && fieldValue != null){
				
			}
			
			
		}catch(NumberFormatException nfe){
			
			nfe.printStackTrace();
		}
		return returnable;
	}
	
	public static Double getFieldToBeValidated(
			String[] propertyNamesNesting, Object obj){
		Double returnable = null; 
		
		Object targetReturned = obj;
		for(int index=0 ; index < propertyNamesNesting.length ; index ++){
			try {
				StringBuilder methodName = new StringBuilder("get");
				methodName.append(propertyNamesNesting[index]);
				//char firstCharOfMethod = methodName.charAt(0);
				//methodName = methodName.replaceFirst(String.valueOf(firstCharOfMethod), methodName)
				targetReturned = MethodInvoker.invokeWithNoException(targetReturned,methodName.toString(),new Object[]{},new Class[]{});
			} catch (SystemException e) {
				throw new RuntimeException(e);
			} catch (ApplicationException e) {
				throw new RuntimeException(e);
			}
		}
		
		returnable = (Double)targetReturned;
		
		return returnable;
	}	
	
	
	
}
