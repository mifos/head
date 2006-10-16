/**

 * ValueObjectUtil.java    version: 1.0

 

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
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.struts.action.ActionForm;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SearchObjectNotCreatedException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.exceptions.ValueObjectConversionException;
import org.mifos.framework.util.valueobjects.ValueObject;


/**
 * Helper class which has methods to operate upon valueobjects.
 */
public class ValueObjectUtil {
	
	/**
	 * @param actionForm
	 * @param moduleName
	 * @param subModuleName
	 * @return
	 * @throws ValueObjectConversionException
	 * Converts the action form into value object by copying values 
	 * from the attributes of the actionform and setting them 
	 * into the value object.
	 **/
	
	public static ValueObject getValueObject(ActionForm actionForm,
			ValueObject valueObject, Locale locale)throws ValueObjectConversionException {
		
		try{
			
			if(null != valueObject){
				//converter from String to sql Date
				ConvertUtilsBean conBean = new ConvertUtilsBean();
				MifosSqlDateConverter converter = new MifosSqlDateConverter();
				MifosDoubleConverter mifosDoubleConverter=new MifosDoubleConverter();
				converter.setLocale(locale);
				conBean.register(converter, java.sql.Date.class);
				conBean.register(mifosDoubleConverter, Double.class);
				//register for FormFile to BLOB conversion
				//MifosInputFileConverter fileConverter = new MifosInputFileConverter();
				//conBean.register(fileConverter, FormFile.class);
				BeanUtilsBean bean = new BeanUtilsBean(conBean, BeanUtilsBean.getInstance().getPropertyUtils());
				bean.copyProperties(valueObject,actionForm);
				
				MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Convertion valueObject to action form using bean utils", false, null);
			}else{
				throw new ValueObjectConversionException("Convertion valueObject to action form using bean utils");
			}
		}catch(InvocationTargetException ite){
			ite.printStackTrace();
			throw new ValueObjectConversionException(ite);
		}catch(IllegalAccessException iae){
			iae.printStackTrace();
			throw new ValueObjectConversionException(iae);
		}catch(Exception e){
			e.printStackTrace();
			throw new ValueObjectConversionException(e);
		}finally{
		}
		return valueObject;
	}
	
	/**
	 * Returns the searchObject which is formed out of the action form passed to this method.
	 * This method assumes that the action form is basically a dynaaction form.
	 * It reads the actionform and converts it to a searchObject which is also a <link>BasicDynaBean</link>
	 * <p>
	 * It is mandatory for the actionform to have a map based property named searchNode 
	 * which will be a key value pair of the searchparameters and searchvalues 
	 */
	public static SearchObject getSearchObject(ActionForm actionForm)
	throws SearchObjectNotCreatedException{
		SearchObject searchObject = new SearchObject();
		Map<String,String> searchNodeMap = null;
		
		// WE NEED TO CHECK HERE IF SEAARCHNAME IS THERE IN THE MAP
		// OR WE NEED TO DEVISE ANOTHER MECHANISM TO MANDATING THAT
		// ALSO NEED TO CHANGE THE COMMENT AFTER CODE REVIEW
		try {
			searchNodeMap = (Map)MethodInvoker.invokeWithNoException(actionForm, "getSearchNodeMap",new Object[]{},new Class[]{} );
			searchObject.setSearchNodeMap(searchNodeMap);
		} catch (SystemException se) {
			
			se.printStackTrace();
			throw new SearchObjectNotCreatedException(se);
		} catch (ApplicationException ae) {
			
			ae.printStackTrace();
			throw new SearchObjectNotCreatedException(ae);
		}
		/*try{
			if(actionForm instanceof DynaActionForm){
				DynaActionForm searchForm = (DynaActionForm)actionForm;
				
				if(null != searchForm){
					DynaClass dynaClass = searchForm.getDynaClass();
					searchObject = new SearchObject(dynaClass);
					propertyValues1 = searchForm.getMap();
					propertyValues = (Map)propertyValues1.get("searchNode");
					if(null != propertyValues){
						keySet = propertyValues.keySet();
					}
					if(null != keySet){
						for(Object key: keySet){
							searchObject.set("searchNode",key.toString(), propertyValues.get(key ) );					 
						}
					}
				}
				
			}
		}catch(NullPointerException npe){
			npe.printStackTrace();
			throw new SearchObjectNotCreatedException(npe);
		}finally{
			
		}*/
		return searchObject;
	}
	
	
	/**
	 * @param actionForm
	 * @param moduleName
	 * @param subModuleName
	 * @return
	 * @throws ValueObjectConversionException
	 * Converts the action form into value object by copying values 
	 * from the attributes of the actionform and setting them 
	 * into the value object.
	 **/
	
	public static void populateBusinessObject(ActionForm actionForm,
			BusinessObject object, Locale locale)throws ValueObjectConversionException{
		
		try{
			
			if(null != object){
				//converter from String to sql Date
				ConvertUtilsBean conBean = new ConvertUtilsBean();
				MifosSqlDateConverter converter = new MifosSqlDateConverter();
				MifosDoubleConverter mifosDoubleConverter=new MifosDoubleConverter();
				converter.setLocale(locale);
				conBean.register(converter, java.sql.Date.class);
				conBean.register(mifosDoubleConverter, Double.class);
				//register for FormFile to BLOB conversion
				//MifosInputFileConverter fileConverter = new MifosInputFileConverter();
				//conBean.register(fileConverter, FormFile.class);
				BeanUtilsBean bean = new BeanUtilsBean(conBean, BeanUtilsBean.getInstance().getPropertyUtils());
				bean.copyProperties(object,actionForm);
				
				//MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Convertion valueObject to action form using bean utils", false, null);
			}else{
				throw new ValueObjectConversionException("");
			}
		}catch(InvocationTargetException ite){
			ite.printStackTrace();
			throw new ValueObjectConversionException(ite);
		}catch(IllegalAccessException iae){
			iae.printStackTrace();
			throw new ValueObjectConversionException(iae);
		}catch(Exception e){
			e.printStackTrace();
			throw new ValueObjectConversionException(e);
		}
	}
}
