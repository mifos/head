/**

 * ProductMixActionForm.java    version: 1.0

 

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
package org.mifos.application.productsmix.struts.actionforms;




import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.StringUtils;


public class ProductMixActionForm extends BaseActionForm {

	private String prdOfferingId;
	private String productType;
	private String productTypeName;
	private String productInstance;
	private String[] productMix;
	
	public String getProductTypeName() {
		return productTypeName;
	}

	public void setProductTypeName(String productTypeName) {
		this.productTypeName = productTypeName;
	}

	public String[] getProductMix() {
		return productMix;
	}

	public void setProductMix(String[] productMix) {
		this.productMix = productMix;
	}

	public String getProductInstance() {
		return productInstance;
	}

	public void setProductInstance(String productInstance) {
		this.productInstance = productInstance;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}


	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		String method = request.getParameter(ProductDefinitionConstants.METHOD);
		if (method != null && method.equals(Methods.load.toString())) {
		}
		if (method != null
				&& (method.equals(Methods.preview.toString()) || method
						.equals(Methods.editPreview.toString()))) {
		}
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
		String method = request.getParameter(ProductDefinitionConstants.METHOD);
		request.setAttribute(GroupConstants.METHODCALLED, method);
		if (method != null && method.equals(Methods.preview.toString())) {
			errors.add(super.validate(mapping, request));
			validateProductType(errors);
			validateProductInstance(errors);

		}
		return errors;
	}

	private void validateProductType(ActionErrors errors) {
		if (StringUtils.isNullOrEmpty(getProductType())){
			addError(errors, "productType",
					ProductDefinitionConstants.ERROR_MANDATORY,
					ProductDefinitionConstants.PRODUCT_TYPE);
			
		}
	}

	private void validateProductInstance(ActionErrors errors) {
		if (StringUtils.isNullOrEmpty(getProductInstance())){
			addError(errors, "productInstance",
					ProductDefinitionConstants.ERROR_MANDATORY,
					ProductDefinitionConstants.PRODUCT_INSTANCE_NAME);
			
		}
	}
	public void doCleanUp() {
		
		productType=null;
		productTypeName=null;
		productInstance=null;
		productMix=null;
		
		
	}

	public String getPrdOfferingId() {
		return prdOfferingId;
	}

	public void setPrdOfferingId(String prdOfferingId) {
		this.prdOfferingId = prdOfferingId;
	}



}
