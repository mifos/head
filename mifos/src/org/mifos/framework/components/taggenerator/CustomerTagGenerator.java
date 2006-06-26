/**
 
 * CustomerTagGenerator.java    version: 1.0
 
 
 
 * Copyright © 2005-2006 Grameen Foundation USA
 
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
package org.mifos.framework.components.taggenerator;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.framework.business.BusinessObject;

public class CustomerTagGenerator extends TagGenerator{
	public CustomerTagGenerator(){
		setAssociatedGenerator(new OfficeTagGenerator());
	}
	
	protected StringBuilder build(BusinessObject obj){
		return build(obj,false);
	}
	
	protected StringBuilder build(BusinessObject obj, boolean selfLinkRequired){
		CustomerBO customer=(CustomerBO)obj;
		
		StringBuilder strBuilder = getAssociatedGenerator().build(customer.getOffice());
		if(strBuilder==null)
			strBuilder = new StringBuilder();
		
		buildLink(strBuilder,customer);
		return strBuilder;
	}
	
	private void buildLink(StringBuilder strBuilder, CustomerBO customer){
		if(customer==null)
			return;
		buildLink(strBuilder,customer.getParentCustomer());
		strBuilder.append(" / ");
		createCustomerLink(strBuilder,customer);
	}
	
	private void  createCustomerLink(StringBuilder strBuilder, CustomerBO customer){
		strBuilder.append("<a href=\"");
		strBuilder.append(getAction(customer));
		strBuilder.append(customer.getGlobalCustNum());
		strBuilder.append("\">");
		strBuilder.append(customer.getDisplayName());
		strBuilder.append("</a>");
	}
	
	private String getAction(CustomerBO customer){
		if(customer.getCustomerLevel().getLevelId().shortValue()==CustomerConstants.CENTER_LEVEL_ID)
			return "centerAction.do?method=get&globalCustNum=";
		else if (customer.getCustomerLevel().getLevelId().shortValue()==CustomerConstants.GROUP_LEVEL_ID)
			return "GroupAction.do?method=get&globalCustNum=";
		else if (customer.getCustomerLevel().getLevelId().shortValue()==CustomerConstants.CLIENT_LEVEL_ID)
			return "clientCreationAction.do?method=get&globalCustNum=";
		return "";
	}
	
}
