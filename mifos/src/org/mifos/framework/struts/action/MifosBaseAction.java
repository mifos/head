/**
 
 * MifosBaseAction    version: 1.0
 
 
 
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

package org.mifos.framework.struts.action;



import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.LookupDispatchAction;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.framework.business.handlers.BusinessProcessor;
import org.mifos.framework.business.handlers.Delegator;
import org.mifos.framework.business.handlers.ServiceLocator;
import org.mifos.framework.business.handlers.ServiceLocatorFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.tabletag.TableTagConstants;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.DoubleSubmitException;
import org.mifos.framework.exceptions.ResourceNotCreatedException;
import org.mifos.framework.exceptions.SearchObjectNotCreatedException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.exceptions.ValueObjectConversionException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.MethodInvoker;
import org.mifos.framework.util.helpers.ResourceConstants;
import org.mifos.framework.util.helpers.SearchObject;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.framework.util.helpers.ValueObjectFactory;
import org.mifos.framework.util.helpers.ValueObjectUtil;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.MasterType;
import org.mifos.framework.util.valueobjects.ReturnType;
import org.mifos.framework.util.valueobjects.ValueObject;
import org.mifos.framework.components.tabletag.TableTagConstants;




/**
 * Base Action class through which all action classes in the system extend.
 * This class will have default implementation
 * of all methods like create,update,delete etc.
 * Any person willing to have custom implementation of these methods will have to
 * override these methods in the derived class.
 */
public abstract class MifosBaseAction extends LookupDispatchAction {
	
	private Delegator delegator =null;
	
	
	/**
	 * Overridden to provide custom key method map implementation.
	 * This method is final and anybody willing to put entries in this map need to
	 * implement {@link appendToMap} method.
	 * @see org.apache.struts.actions.LookupDispatchAction#getKeyMethodMap()
	 */
	protected final Map getKeyMethodMap() {
		Map<String,String> methodHashMap = new HashMap<String,String>();
		//	Map methodHashMap = new HashMap();
		methodHashMap.put(ResourceConstants.CREATE, "create");
		methodHashMap.put(ResourceConstants.UPDATE, "update");
		methodHashMap.put(ResourceConstants.DELETE, "delete");
		methodHashMap.put(ResourceConstants.SEARCH, "search");
		methodHashMap.put(ResourceConstants.PREVIEW,"preview");
		methodHashMap.put(ResourceConstants.PREVIOUS,"previous");
		methodHashMap.put(ResourceConstants.GET,"get");
		methodHashMap.put(ResourceConstants.LOAD,"load");
		methodHashMap.put(ResourceConstants.CANCEL,"cancel");
		methodHashMap.put(ResourceConstants.MANAGE,"manage");
		methodHashMap.put(ResourceConstants.VALIDATE,"validate");
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Inside getKeyMethodMap method", false, null);
		methodHashMap.putAll(appendToMap());
		return methodHashMap;
	}
	
	
	
	/**
	 * Returns keyMethod map to be added to the keyMethodMap of the base Action class.
	 * This method is called from <code>getKeyMethodMap<code> of the base Action Class.
	 * The map returned from this method is appended to the map of the <code>getKeyMethodMap<code> in the base action class and that will form the complete map for that action.
	 * @return
	 */
	public Map<String,String> appendToMap()
	//public Map appendToMap()
	{
		return new HashMap<String,String>();
		//return new HashMap();
	}
	
	
	
	/**
	 * Does some activities before the execute method is called like cleaningup of session,creating valueobject and context
	 * @param actionForm
	 * @param request
	 * @throws SystemException
	 */
	protected void preExecute(ActionForm actionForm,HttpServletRequest request)throws SystemException,ApplicationException {
		
		ValueObject valueObject = null;
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Inside preexecute method", false, null);
		
		handleTransaction(actionForm,request);
		doCleanUp(request);
		
		SessionUtils.setAttribute(Constants.PATH, getPath(), request.getSession());
		
		Context context = (Context)SessionUtils.getContext(getPath(), request.getSession());
		if(null == context){
			valueObject = getValueObject(getPath());
			context = createContext(valueObject,getPath());
			context.setUserContext((UserContext)SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession()));
			//this will add the context in the session which we might need
			// for master data and valueobjects
			SessionUtils.setRemovableAttribute(Constants.CONTEXT, context,getPath(), request.getSession());
			SessionUtils.setContext(getPath(),context,request.getSession());
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Context object not found in session hence creating it");
		}else{
			
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Context object found in session hence not necessary to create it");
		}
		
		if(isActionFormToValueObjectConversionReq((String)request.getParameter("method"))){	
			UserContext userContext = (UserContext)request.getSession().getAttribute(LoginConstants.USERCONTEXT);
			Locale locale=null;
			if(null != userContext) {
				locale= userContext.getPereferedLocale();
				if(null==locale) {
					locale=userContext.getMfiLocale();
				}
			}
			convertActionFormToValueObject(actionForm,context.getValueObject(),locale);
		}
		request.setAttribute( Constants.CONTEXT , context );
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("setting context object in request", false, null);
	}
	
	/**
	 * This method handles UI transactions in the sense that it reads the annotations on the method invoked 
	 * and then based on the values in annotation elements it takes the following action.
	 * 1.saveToken - It saves a new token in the session.
	 * 2.validateAndResetToken - It validates the token and then resets the token.
	 * 3.joinToken - It checks if the token is present it does nothing else it saves a new token in the session.
	 * 
	 * If there is no annotation present it does nothing.  
	 * @param actionForm
	 * @param request
	 * @throws SystemException
	 * @throws ApplicationException - if the annotation is of type validateAndResetToken and token comes out to be in valid
	 * in that case it throws DoubleSubmitException.
	 */
	protected void handleTransaction(ActionForm actionForm,HttpServletRequest request)throws SystemException,ApplicationException{
		try {
			// get the name of the method which is to be invoked in the action class.
			String methodName = request.getParameter("method");
			Method methodToExecute = this.clazz.getMethod(methodName, new Class[]{ActionMapping.class,ActionForm.class,HttpServletRequest.class,HttpServletResponse.class});
			// get the annotation which demarcates the transaction 
			TransactionDemarcate annotation = methodToExecute.getAnnotation(TransactionDemarcate.class);
			
			// annotation would be null if it is not defined for that particular method
			if(null != annotation && annotation.saveToken()){
				MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Saving a new token");
				saveToken(request);
			}else if (null != annotation && annotation.validateAndResetToken()){
				MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Validating the token");
				if(isTokenValid(request)){
					MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Token is valid and resetting the token");
					resetToken(request);
				}else{
					MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Token is invalid hence throwing an exception");
					throw new DoubleSubmitException(ExceptionConstants.DOUBLESUBMITEXCEPTION);
				}
			}else if (null != annotation && annotation.joinToken()){
				
				if(!isTokenValid(request)){
					MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Token is not valid and hence saving a new  token");
					saveToken(request);
				}
			}
			
			
			
		} catch (SecurityException se) {
			
			se.printStackTrace();
		} catch (NoSuchMethodException nsme) {
			
			nsme.printStackTrace();
		}
	}
	
	
	/**
	 * @return
	 */
	protected boolean isActionFormToValueObjectConversionReq(String methodName) {
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Before converting action form to value object checking for method name " + methodName);
		if(null !=methodName && methodName.equals("manage")){
			
			return false;
		}else{
			return true;
		}
		
	}
	
	
	
	/**
	 * Cleans up the session attributes which were set in the previous action class.
	 * Only those attributes are cleaned which were set in the session using
	 * <code>SessionUtils.setRemovableAttribute()</code>
	 * @param request
	 */
	protected void doCleanUp(HttpServletRequest request)
	{
		if(!(request.getParameter("method").equals(TableTagConstants.SEARCHNEXT) || request.getParameter("method").equals(TableTagConstants.SEARCHPREV)))
		{
			try
			{
				Context context = null;
				String previousPath = (String)SessionUtils.getAttribute(Constants.PATH, request.getSession());
				String currentPath = getPath();
				if(null != previousPath && !currentPath.equals(previousPath))
				{
					context = (Context)SessionUtils.getContext(previousPath, request.getSession());
				}
				else
					context = (Context)SessionUtils.getContext(currentPath, request.getSession());
				
				if(context != null)
				{
					QueryResult queryResult = context.getSearchResult();
					if(queryResult != null)
					{
						cleanUpSearch(request);
						queryResult.close();
						context.setSearchResult(null);
					}
				}
			}
			catch(Exception e)
			{
				MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).error("Error while cleaning Up Search Object");		
			}
			
		}
		if(performCleanUp())
		{
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Inside docleanup method", false, null);
			String previousPath = (String)SessionUtils.getAttribute(Constants.PATH, request.getSession());
			if(null != previousPath && !getPath().equals(previousPath)){
				SessionUtils.doCleanUp(previousPath, request.getSession());
				MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Do clean up has been called because previous path is " + previousPath +" and current path is "+getPath());
			}
		}
		
	}
	
	protected boolean performCleanUp()
	{
		return true;
	}
	
	/**
	 * Calls the differnt methods of the action class in the order <br>
	 * <code>preExecute<br>execute<br>postexecute</code>
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response)	throws Exception {
		
		ActionForward forward = null;
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Inside execute method of MifosBaseAction", false, null);
		preExecute(form, request);
		forward =  super.execute(mapping, form, request, response);
		postExecute(request);
		return forward;
		
	}
	
	/**
	 * Does some common activities required to be done after each of the method calls like
	 * <code>create<br>update<load> etc. </code>.One such activity is reading the context and set every ting in the request scope.
	 * @param request
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	protected void postExecute(HttpServletRequest request)throws SystemException,ApplicationException {
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Inside postExecute method", false, null);
		
		modelToRequestTranslator(request);
		
	}
	
	
	/**
	 * This is the method which would be called for any create operation in the application.
	 * It delegates the call to the business processor and  passing the {@link Context} object.
	 * The <code>Context</code> object to be passed is obtained from the request as that is the scope where it was set by the <code>preExecute</code> method.
	 * Before delegating the call to the <code>BusinessProcessor</code> the <code>businessAction</code> attribute of the <code>Context</code> object is set to "create".
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@TransactionDemarcate(validateAndResetToken=true)
	public ActionForward create(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		
		ActionForward forward = null;
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Inside base create method", false, null);
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction("create");
		forward = (ActionForward)MethodInvoker.invokeWithNoException(this, "customCreate", new Object[]{mapping,form,request,response}, new Class[]{ActionMapping.class,ActionForm.class,HttpServletRequest.class,HttpServletResponse.class});
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("After visit create", false, null);
		delegate(context,request);
		// this clean up the context because it won't be required on the create page.
		cleanUpContext(request);
		// this sets the action form to null
		form = null;	
		if(null != forward){
			return forward;
		}else{
			return mapping.findForward(Constants.CREATE_SUCCESS);
		}
		
	}
	
	/**
	 * This method cleans up the context in the sense that it removes only the master data which is set in the 
	 * context as attributes, it will not remove the business results. 
	 * @param request
	 */
	protected void cleanUpContext(HttpServletRequest request) {
		Context context = (Context)SessionUtils.getAttribute(Constants.CONTEXT, request.getSession());
		context.cleanAttributes();
		
	}
	
	
	
	/**
	 * This is the method which would be called for any delete operation in the application.
	 * It delegates the call to the business processor and  passing the {@link Context} object.
	 * The <code>Context</code> object to be passed is obtained from the request as that is the scope where it was set by the <code>preExecute</code> method.
	 * Before delegating the call to the <code>BusinessProcessor</code> the <code>businessAction</code> attribute of the <code>Context</code> object is set to "delete".
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward delete(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		
		ActionForward forward = null;
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Inside base create method", false, null);
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction("delete");
		forward = (ActionForward)MethodInvoker.invokeWithNoException(this, "customDelete", new Object[]{mapping,form,request,response}, new Class[]{ActionMapping.class,ActionForm.class,HttpServletRequest.class,HttpServletResponse.class});
		delegate(context,request);
		if(null != forward){
			return forward;
		}else{
			return mapping.findForward(Constants.DELETE_SUCCESS);
		}
		
	}
	
	
	
	
	/**
	 * This is the method which would be called for any cancel operation in the application.
	 * It delegates the call to the business processor and  passing the {@link Context} object.
	 * The <code>Context</code> object to be passed is obtained from the request as that is the scope where it was set by the <code>preExecute</code> method.
	 * Before delegating the call to the <code>BusinessProcessor</code> the <code>businessAction</code> attribute of the <code>Context</code> object is set to "cancel".
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward cancel(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		ActionForward forward = null;
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Inside base cancel method", false, null);
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction("cancel");
		forward = (ActionForward)MethodInvoker.invokeWithNoException(this, "customCancel", new Object[]{mapping,form,request,response}, new Class[]{ActionMapping.class,ActionForm.class,HttpServletRequest.class,HttpServletResponse.class});
		delegate(context,request);
		if(null != forward){
			return forward;
		}else{
			return mapping.findForward(Constants.CANCEL_SUCCESS);
		}
		
	}
	
	
	/**
	 * This is the method which would be called for any manage operation in the application.
	 * It delegates the call to the business processor and  passing the {@link Context} object.
	 * The <code>Context</code> object to be passed is obtained from the request as that is the scope where it was set by the <code>preExecute</code> method.
	 * Before delegating the call to the <code>BusinessProcessor</code> the <code>businessAction</code> attribute of the <code>Context</code> object is set to "manage".
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@TransactionDemarcate(joinToken = true)
	public ActionForward manage(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		ActionForward forward = null;
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Inside base cancel method", false, null);
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction("manage");
		forward = (ActionForward)MethodInvoker.invokeWithNoException(this, "customManage", new Object[]{mapping,form,request,response}, new Class[]{ActionMapping.class,ActionForm.class,HttpServletRequest.class,HttpServletResponse.class});
		delegate(context,request);
		if(null != forward){
			return forward;
		}else{
			return mapping.findForward(Constants.MANAGE_SUCCESS);
		}
		
	}
	
	
	
	/**
	 * This is the method which would be called for any "previous" operation in the application.
	 * It delegates the call to the business processor and  passing the {@link Context} object.
	 * The <code>Context</code> object to be passed is obtained from the request as that is the scope where it was set by the <code>preExecute</code> method.
	 * Before delegating the call to the <code>BusinessProcessor</code> the <code>businessAction</code> attribute of the <code>Context</code> object is set to "previous".
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward previous(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		ActionForward forward = null;
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction("previous");
		forward = (ActionForward)MethodInvoker.invokeWithNoException(this, "customPrevious", new Object[]{mapping,form,request,response}, new Class[]{ActionMapping.class,ActionForm.class,HttpServletRequest.class,HttpServletResponse.class});
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("After visit Previous", false, null);
		delegate(context,request);
		if(null != forward){
			return forward;
		}else{
			return mapping.findForward(Constants.PREVIOUS_SUCCESS);
		}
		
		
	}
	
	/**
	 * This is the method which would be called for any "preview" operation in the application.
	 * It delegates the call to the business processor and  passing the {@link Context} object.
	 * The <code>Context</code> object to be passed is obtained from the request as that is the scope where it was set by the <code>preExecute</code> method.
	 * Before delegating the call to the <code>BusinessProcessor</code> the <code>businessAction</code> attribute of the <code>Context</code> object is set to "preview".
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@TransactionDemarcate(joinToken = true)
	public ActionForward preview(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		
		ActionForward forward = null;
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction("preview");
		forward = (ActionForward)MethodInvoker.invokeWithNoException(this, "customPreview", new Object[]{mapping,form,request,response}, new Class[]{ActionMapping.class,ActionForm.class,HttpServletRequest.class,HttpServletResponse.class});
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("After visit Preview", false, null);
		delegate(context,request);
		
		//modelToRequestConvertor(context.getValueObject(),request);
		if(null != forward){
			return forward;
		}else{
			return mapping.findForward(Constants.PREVIEW_SUCCESS);
		}
		
		
		
	}
	
	/**
	 * This is the method which would be called for any "validate" operation in the application.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward validate(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		
		ActionForward forward = null;
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction("validate");
		forward = (ActionForward)MethodInvoker.invokeWithNoException(this, "customValidate", new Object[]{mapping,form,request,response}, new Class[]{ActionMapping.class,ActionForm.class,HttpServletRequest.class,HttpServletResponse.class});
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("After visit Validate", false, null);
		delegate(context,request);
		if(null != forward){
			return forward;
		}else{
			String methodCalled =(String)request.getAttribute("methodCalled");
			if(null != methodCalled) {
				return mapping.findForward(methodCalled+"_failure");
			}
			else {
				return null;
			}
		}
		
		
		
	}
	/**
	 * This is the method which would be called for any "update" operation in the application.
	 * It delegates the call to the business processor and  passing the {@link Context} object.
	 * The <code>Context</code> object to be passed is obtained from the request as that is the scope where it was set by the <code>preExecute</code> method.
	 * Before delegating the call to the <code>BusinessProcessor</code> the <code>businessAction</code> attribute of the <code>Context</code> object is set to "update".
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward update(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		
		ActionForward forward = null;
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction("update");
		forward = (ActionForward)MethodInvoker.invokeWithNoException(this, "customUpdate", new Object[]{mapping,form,request,response}, new Class[]{ActionMapping.class,ActionForm.class,HttpServletRequest.class,HttpServletResponse.class});
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("After visit Update", false, null);
		delegate(context,request);
		
		if(null != forward){
			return forward;
		}else{
			return mapping.findForward(Constants.UPDATE_SUCCESS);
		}
	}
	
	/**
	 * This is the method which would be called for any "get" operation in the application.
	 * It delegates the call to the business processor and  passing the {@link Context} object.
	 * The <code>Context</code> object to be passed is obtained from the request as that is the scope where it was set by the <code>preExecute</code> method.
	 * Before delegating the call to the <code>BusinessProcessor</code> the <code>businessAction</code> attribute of the <code>Context</code> object is set to "get".
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@TransactionDemarcate(saveToken = true)
	public ActionForward get(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		
		ActionForward forward = null;
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction("get");
		forward = (ActionForward)MethodInvoker.invokeWithNoException(this, "customGet", new Object[]{mapping,form,request,response}, new Class[]{ActionMapping.class,ActionForm.class,HttpServletRequest.class,HttpServletResponse.class});
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("After visit Get", false, null);
		delegate(context,request);
		//modelToRequestConvertor(context.getValueObject(),request);
		if(null != forward){
			return forward;
		}else{
			return mapping.findForward(Constants.GET_SUCCESS);
		}
	}
	
	
	
	/**
	 * This is the method which would be called for any "search" operation in the application.
	 * It delegates the call to the business processor and  passing the {@link Context} object.
	 * The <code>Context</code> object to be passed is obtained from the request as that is the scope where it was set by the <code>preExecute</code> method.
	 * Before delegating the call to the <code>BusinessProcessor</code> the <code>businessAction</code> attribute of the <code>Context</code> object is set to "search".
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward search(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		
		ActionForward forward = null;
		request.setAttribute(Constants.CURRENTFLOWKEY, request
				.getParameter(Constants.CURRENTFLOWKEY));
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		context.setSearchObject(formSearchObject(form,request));
		context.setBusinessAction("search");
		forward = (ActionForward)MethodInvoker.invokeWithNoException(this, "customSearch", new Object[]{mapping,form,request,response}, new Class[]{ActionMapping.class,ActionForm.class,HttpServletRequest.class,HttpServletResponse.class});
		delegate(context,request);
		if(null != forward){
			return forward;
		}else{
			return mapping.findForward(Constants.SEARCH_SUCCESS);
		}
	}
	
	
	
	/**
	 * This is the method which would be called for any "load" operation in the application.
	 * It delegates the call to the business processor and  passing the {@link Context} object.
	 * The <code>Context</code> object to be passed is obtained from the request as that is the scope where it was set by the <code>preExecute</code> method.
	 * Before delegating the call to the <code>BusinessProcessor</code> the <code>businessAction</code> attribute of the <code>Context</code> object is set to "load".
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@TransactionDemarcate(saveToken = true)
	public ActionForward load(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		
		ActionForward forward = null;
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction(ResourceConstants.LOAD);
		saveToken(request);
		forward = (ActionForward)MethodInvoker.invokeWithNoException(this, "customLoad", new Object[]{mapping,form,request,response}, new Class[]{ActionMapping.class,ActionForm.class,HttpServletRequest.class,HttpServletResponse.class});
		delegate(context,request);
		if(null != forward){
			return forward;
		}else{
			return mapping.findForward(Constants.LOAD_SUCCESS);
		}
	}
	
	/**
	 * Returns the {@link SearchObject} which is formed by reading the form object passed to it as parameter.
	 * This method in turn calls the <code>getSearchObject</code> method  on {@link ValueObjectUtil} class.
	 * @param request
	 * @return
	 * @throws SearchObjectNotCreatedException - If there is any exception in forming the SearchObject
	 */
	protected SearchObject formSearchObject(ActionForm form,HttpServletRequest request) throws SearchObjectNotCreatedException {
		
		return ValueObjectUtil.getSearchObject(form);
	}
	
	
	
	
	/**
	 * Return an object of ValueObject corresponding to the path using a {@link ValueObjectFactory}.
	 * The valueObject factory reads the dependency.xml file which has the FQClassName for the ValueObject
	 * along with the path attribute same as that returned by the <code>getPath()</code> method of the action class
	 * @param path - Should be obtained calling <code>getPath()</code> method of the action class
	 * @return
	 * @throws ResourceNotCreatedException
	 */
	protected ValueObject getValueObject(String path)throws ResourceNotCreatedException{
		ValueObject valueObject = null;
		try {
			
			valueObject = (ValueObject)ValueObjectFactory.getInstance().get(path);
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("after getting valueobject using valueobject factory");
		} catch (ResourceNotCreatedException rnce) {
			
			rnce.printStackTrace();
		}
		return valueObject;
	}
	
	
	
	/**
	 * This method uses the helper class ValueObjectConvertor and gets a value object given an action form,modulename and submodule name
	 * @param actionForm
	 * @param name
	 * @return
	 * @throws ValueObjectConversionException
	 */
	protected ValueObject convertActionFormToValueObject(ActionForm actionForm,ValueObject valueObject, Locale locale)throws ValueObjectConversionException {
		
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Converting valueObject to action form", false, null);
		return ValueObjectUtil.getValueObject(actionForm,valueObject, locale);
	}
	
	
	/**
	 *  This method gets the Delegator and delegates the call to the BusinessProcessor where the actual business logic lies.
	 * @param context
	 * @param request
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	protected void delegate(Context context,HttpServletRequest request)throws SystemException,ApplicationException {
		if(null == delegator){
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Delegator is null hence getting it from request", false, null);
			delegator = getDelegator(request);
			delegator.process(context);
		}else if(null != delegator){
			delegator.process(context);
			
		}else{
			throw new SystemException();
		}
	}
	
	
	/**
	 * Needs to be implemented by the Action class which extends MifosBaseAction.
	 * @return - String which uniquely identifies the dependency element in Dependency.xml
	 */
	protected abstract String getPath();
	
	/**
	 * This gets the delegator object from the application context.It was set in the application context by the initializer plugin.
	 * @param request
	 * @return
	 */
	protected final Delegator getDelegator(HttpServletRequest request) {
		delegator =  (Delegator)(request.getSession().getServletContext().getAttribute(Constants.DELEGATOR));
		return delegator;
	}
	
	
	/**
	 * Returns an object of {@link Context} class which acts as a wrapper around the parameters that we need to send to the BusinessProcessor.
	 * @param valueObject
	 * @param moduleName
	 * @param subModuleName
	 * @return
	 *
	 */
	protected final Context createContext(ValueObject valueObject, String name) {
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Creating Context afresh", false, null);
		Context context = new Context(name,valueObject);
		return context;
	}
	
	/**
	 * Returns the business processor corresponding to the path passed to it
	 * @param path
	 * @return
	 * @throws ResourceNotCreatedException
	 */
	protected BusinessProcessor getBusinessProcessor(String path,String businessProcessorImplementation)throws ResourceNotCreatedException{
		ServiceLocatorFactory serviceLocatorFactory = null;
		ServiceLocator serviceLocator = null;
		BusinessProcessor businessProcessor = null;
		
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("In getBusinessProcessor Method", false, null);
		try{
			serviceLocatorFactory = ServiceLocatorFactory.getInstance();
			serviceLocator = serviceLocatorFactory.getServiceLocator(businessProcessorImplementation);
			if(null != serviceLocator){
				businessProcessor = serviceLocator.getBusinessProcessor(path);
				MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Getting business processor from servicelocator", false, null);
			}
			
		}catch(ResourceNotCreatedException rnce){
			throw rnce;
		}
		return businessProcessor;
	}
	
	
	
	/**
	 * @param valueObject
	 * @param request
	 */
	protected void modelToRequestConvertor(ValueObject valueObject,HttpServletRequest request){
		/*	MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Inside Model to request convertor", false, null);
		 // get all the methods using reflection
		  Method[] methodArray = valueObject.getClass().getMethods();
		  String methodName = null;
		  String methodNameInRequest = null;
		  if(null != methodArray){
		  // if methodArray is not null iterate over them
		   for(Method m:methodArray){
		   methodName = m.getName();
		   // if it is a getter method invoke that method
		    if(methodName.startsWith("get")){
		    // the exceptions are digested because even if there is
		     // an exception we should continue with other methods
		      methodNameInRequest = methodName.substring(3,methodName.length());
		      try {
		      request.setAttribute(methodNameInRequest, m.invoke(valueObject, null));
		      } catch (IllegalArgumentException e) {
		      e.printStackTrace();
		      } catch (IllegalAccessException e) {
		      e.printStackTrace();
		      } catch (InvocationTargetException e) {
		      e.printStackTrace();
		      }
		      }// end-if
		      }// end-for
		      }// end-if*/
	}//:~
	
	private void cleanUpSearch(HttpServletRequest request)
	{
		// need to have a cleaner way of cleaning
		SessionUtils.setRemovableAttribute("TableCache",null,TableTagConstants.PATH,request.getSession());
		SessionUtils.setRemovableAttribute("current",null,TableTagConstants.PATH,request.getSession());
		SessionUtils.setRemovableAttribute("meth",null,TableTagConstants.PATH,request.getSession());
		SessionUtils.setRemovableAttribute("forwardkey",null,TableTagConstants.PATH,request.getSession());
		SessionUtils.setRemovableAttribute("action",null,TableTagConstants.PATH,request.getSession());
		
	}
	
	/**
	 * Reads the Context object and sets the ValueObject and attributes in the request
	 * so that these can be accessed in the jsp.
	 * This method is called from <code>postExecute</code> method.
	 * @param request
	 */
	protected void modelToRequestTranslator(HttpServletRequest request){
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		String resultName =null;
		ValueObject valueObject =context.getValueObject();
		
		if(null!=valueObject ){
			resultName= valueObject.getResultName();
		}
		if(null != resultName){
			request.setAttribute(resultName,context.getValueObject());
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Setting valueobject in request after reading from context by name "+ resultName);
		}
		
		// reads the list of attributes and sets it in the request
		List returnValues=context.getAttributes();
		if(null != returnValues){
			for(Object obj:returnValues){
				resultName= ((ReturnType)obj).getResultName();
				if(null != resultName ){
					if(obj instanceof MasterType){
						request.setAttribute(resultName,((MasterType)obj).getValue());
						MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Setting objects in request after reading from context by name "+ resultName);
					}
					
				}
				
			}// end -for
		}
	}
	
}
