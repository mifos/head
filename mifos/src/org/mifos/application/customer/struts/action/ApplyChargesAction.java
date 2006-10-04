/**
 
 * ApplyChargesAction.java    version: xxx
 
 
 
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

package org.mifos.application.customer.struts.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.framework.struts.action.MifosBaseAction;

/**
 * This class is used as Action class for Applying Charges for customers.
 */
public class ApplyChargesAction extends MifosBaseAction {
	
	public ApplyChargesAction() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected String getPath() {
		return null;
	}
	
	/**
	 * Returns keyMethod map to be added to the keyMethodMap of the base Action class.
	 * This method is called from <code>getKeyMethodMap<code> of the base Action Class.
	 * The map returned from this method is appended to the map of the <code>getKeyMethodMap<code> in the base action class and that will form the complete map for that action.
	 * The methods entered in this map are: -
	 * 1)remove - this is called to remove a recurring account fee.
	 */
	@Override
	public Map<String,String> appendToMap()
	{
		HashMap<String,String> keyMethodMap = new HashMap<String,String>();
		keyMethodMap.put(null, "remove");
		return keyMethodMap;
	}
	
	/**
	 *No need to override. 
	 */
	@Override
	public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse httpservletresponse)
	throws Exception
	{
		return null;
	}
	
	/**
	 *This method is called to remove the recurring fee associated with that customer account.
	 *It obtains the accountFeeId of the fee to be removed from the action form.  
	 */
	public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse httpservletresponse)
	throws Exception
	{
		return null;
	}
	
	/**
	 *No need to override.  
	 */
	@Override
	public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse httpservletresponse)
	throws Exception
	{
		return null;
	}
	
	/**
	 *No need to override. 
	 */
	@Override
	public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse httpservletresponse)
	throws Exception
	{
		return null;
	}
	
	/**
	 *It returns an ActionForward which is a chained action forward and calls get on the ViewDetails action to show the updated details on the page. 
	 */
	@Override
	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse httpservletresponse)
	throws Exception
	{
		return null;
	}
	
}
