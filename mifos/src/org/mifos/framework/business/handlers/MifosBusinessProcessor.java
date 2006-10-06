/**
 
 * MifosBusinessProcessor    version: 1.0
 
 
 
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

package org.mifos.framework.business.handlers;






import java.util.ArrayList;
import java.util.List;

import org.mifos.framework.business.util.helpers.HeaderObject;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.dao.helpers.MasterDataRetriever;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ResourceNotCreatedException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DAOFactory;
import org.mifos.framework.util.helpers.MethodInvoker;
import org.mifos.framework.util.helpers.SearchObject;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;





/**
 *  This is the class in which all business logic would be present and this class will have corresponding methods like create , update etc.
 *  This base class will have default implementation of these methods and the respective submodule BusinessProcessors need to override this implementation only if required.
 */
public class MifosBusinessProcessor implements BusinessProcessor {
	
	
	/**
	 * This method is called by the delegator and it inturn calls the
	 * corresponding methods on the <code>BusinessProcessor</code> passing the
	 * Context object to that method. The method to called on the
	 * BusinessProcessor is identified by the value of
	 * <code>businessAction</code> attribute of the Context object. These
	 * methods on the BusinessProcessor are called by reflection.Before calling
	 * the actual method another method is called and the name of that method is
	 * formed by suffixing "SearchInitial" to the value returned by
	 * <code>getBusinessAction()</code> method of Context object. If the first
	 * method throws an exception the next method won't be called.
	 */
	public void execute(Context context) throws SystemException,ApplicationException {
		List <String>businessActionList = getBusinessActionList();
		HeaderObject headerObj = null;
		//use refliection to call context.getBusinessAction + "SearchInital"
		//ignore any MethodNotFoundexception
		MethodInvoker.invokeWithNoException(this, context.getBusinessAction()+"Initial",new Object[]{context},new Class[]{context.getClass()});
		MethodInvoker.invoke(this, context.getBusinessAction(),new Object[]{context},new Class[]{context.getClass()});
		if(null != businessActionList && !businessActionList.isEmpty()){
			for(String businessAction : businessActionList){
				MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("The fetch header method is being called for the method : " + businessAction);
				if(businessAction.equals(context.getBusinessAction())){
					headerObj = (HeaderObject)MethodInvoker.invoke(this, "fetchHeader",new Object[]{context,businessAction},new Class[]{context.getClass(),String.class});
				}
				if(null != headerObj){
					context.addBusinessResults("header_"+businessAction, headerObj);
				}
				
			}
			
		}// end -if
		
	}
	

	/**
	 * This method returns a list of business action method names on which we
	 * need to call fetchHeader method.
	 */
	protected List<String> getBusinessActionList() {
		List headerMethodList = new ArrayList();
		headerMethodList.add("get");
		return headerMethodList;
	}
	
	/**
	 * This is the default implementation added because this method is always
	 * added for get and some body has not implemented this method it would
	 * throw an Exception (BACKWARD COMPATIBILITY)
	 */
	public HeaderObject fetchHeader(Context context,String businessAction)
	throws SystemException,ApplicationException{
		return null;
	}

	/**
	 * It updates the ValueObject instance passed in the Context object in the database.
	 */
	public void update(Context context) throws SystemException,
	ApplicationException
	{
		
		MethodInvoker.invoke(getDAO(context.getPath()), "update",new Object[]{context},new Class[]{context.getClass()});
		
	}
	
	/**
	 * It calls the delete method on the DAO to delete the record from the database.
	 */
	public void delete(Context context) throws SystemException,
	ApplicationException
	{
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Inside delete method of MifosBusinessProcessor");
		MethodInvoker.invoke(getDAO(context.getPath()), "delete",new Object[]{context},new Class[]{context.getClass()});
		
	}
	
	/**
	 * It creates the ValueObject instance passed in the Context object in the
	 * database.
	 */
	public void create(Context context) throws SystemException,
	ApplicationException
	{
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Inside create method of MifosBusinessProcessor");
		MethodInvoker.invoke(getDAO(context.getPath()), "create",new Object[]{context},new Class[]{context.getClass()});
		
	}
	
	
	/**
	 * It gets the ValueObject instance from the database based on the primary
	 * key values which are already set in the ValueObject.
	 */
	public void get(Context context) throws SystemException,ApplicationException
	{
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Inside get method of MifosBusinessProcessor");
		MethodInvoker.invoke(getDAO(context.getPath()), "get",new Object[]{context},new Class[]{context.getClass()});
	}
	
	/**
	 * This is the final method which is called whenever the
	 * <code>businessAction</code> attribute in the Context object is set to
	 * search. Thie method in turn calls the relevant search methods on the
	 * BusinessProcessor. The name of the method to be invoked on the
	 * BusinessProcessor is formed by prefixing "get" to the searchName which is
	 * obtained from the {@link SearchObject}.
	 */
	
	public final void search(Context context) throws SystemException,
	ApplicationException {
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Inside search method of MifosBusinessProcessor");
		SearchObject searchObject = null;
		searchObject = context.getSearchObject();
		String searchName = null;
		if(null != searchObject){
			searchName = searchObject.getFromSearchNodeMap(Constants.SEARCH_NAME);
		}
		if(null != searchName && !searchName.equals("")){
			
			MethodInvoker.invoke(this, "get"+searchName,new Object[]{context},new Class[]{context.getClass()});
		}
		
	}
	
	
	/**
	 * Returns the DAO corresponding to the path passed to it.
	 * The path should uniquely identify the dependency element in Dependency.xml
	 * DAO is returned using {@link DAOFactory}.
	 */
	protected DAO getDAO(String path) throws ResourceNotCreatedException {
		return (DAO)DAOFactory.getInstance().get(path);
	}
	
	/**
	 * This method is called when initially the page is supposed to be loaded
	 * for any further action. For e.g. when the user clicks on the link on the
	 * left menu to create a client.
	 */
	public void load(Context context) throws SystemException,ApplicationException {
	}
	
	/**
	 * This method is called when the user clicks cancel on the page which
	 * should take you to a common page. For e.g. when the user clicks on the
	 * link on the left menu to create a client.
	 */
	public void cancel(Context context) throws SystemException,ApplicationException {
	}
	
	/**
	 * This method is called when you want to manage a particular entity. For
	 * e.g. when the user clicks on the link on the left menu to create a
	 * client.
	 */
	public void manage(Context context) throws SystemException,ApplicationException {
	}
	
	/**
	 * This method is called when the method invoked on action class is preview.
	 * For e.g. when the user clicks on the link on the left menu to create a client.
	 * @param context
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void preview(Context context) throws SystemException,ApplicationException {
	}
	
	/**
	 * This method is called when the method invoked on action class is previous.
	 * For e.g. when the user clicks on the link on the left menu to create a client.
	 * @param context
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void previous(Context context) throws SystemException,ApplicationException {
	}
	/**
	 * This method is called when the method invoked on action class is next.
	 * For e.g. when the user has to traverse through many pages to input information.
	 * @param context
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void next(Context context) throws SystemException,ApplicationException {
	}
	
	/**
	 * This method is called whenever there is a validation error.For normal scenarios this need not be overridden.
	 * @param context
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void validate(Context context) throws SystemException,ApplicationException {
	}
	
/**
	 * This is used to get master data .
	 * @param entityId
	 * @param localeId
	 * @param searchResultName
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	protected SearchResults getMasterData(String entityName,Short localeId,String searchResultName)throws SystemException,ApplicationException{
		SearchResults searchResults = null;
		MasterDataRetriever masterDataRetriever = new MasterDataRetriever();
		searchResults = masterDataRetriever.retrieveMasterData(entityName, localeId, searchResultName);
		
		return searchResults;
	}
	
	/**
	 * It is used to retrieve master data where in the id in the main table is not the id from the LookUpValue table 
	 * but there is a different master table.
	 * @param entityName
	 * @param localeId
	 * @param searchResultName
	 * @param classpath
	 * @param column
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	protected SearchResults getMasterData(String entityName,Short localeId,String searchResultName,String classpath,String column)throws SystemException,ApplicationException{
		SearchResults searchResults = null;
		MasterDataRetriever masterDataRetriever = new MasterDataRetriever();
		searchResults = masterDataRetriever.retrieveMasterData(entityName, localeId, searchResultName,classpath,column);
		
		return searchResults;
	}
	
}
