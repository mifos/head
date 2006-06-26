/**

 * ActionVisitor.java    version: 1.0

 

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

package org.mifos.framework.struts.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class ActionVisitor {

	protected void visitCreate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, MifosBaseAction mifosBaseAction) {
	}

	protected void visitUpdate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, MifosBaseAction mifosBaseAction) {
	}

	protected void visitDelete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, MifosBaseAction mifosBaseAction) {
	}

	protected void visitSearch(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, MifosBaseAction mifosBaseAction) {
	}

	protected void visitPreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, MifosBaseAction mifosBaseAction) {
	}

	protected void visitGet(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, MifosBaseAction mifosBaseAction) {
	}

	protected void visitNext(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, MifosBaseAction mifosBaseAction) {
	}

	protected void visitPrevious(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, MifosBaseAction mifosBaseAction) {
	}

	protected void visitSetMasterDataForCreate(HttpServletRequest request,
			MifosBaseAction mifosBaseAction) {
	}

	protected void visitSetMasterDataForUpdate(HttpServletRequest request,
			MifosBaseAction mifosBaseAction) {
	}

	protected void visitSetMasterDataForDelete(HttpServletRequest request,
			MifosBaseAction mifosBaseAction) {
	}

	protected void visitSetMasterDataForSearch(HttpServletRequest request,
			MifosBaseAction mifosBaseAction) {
	}

	protected void visitSetMasterDataForPreview(HttpServletRequest request,
			MifosBaseAction mifosBaseAction) {
	}

	protected void visitSetMasterDataForGet(HttpServletRequest request,
			MifosBaseAction mifosBaseAction) {
	}

	protected void visitSetMasterDataForNext(HttpServletRequest request,
			MifosBaseAction mifosBaseAction) {
	}

	protected void visitSetMasterDataForPrevious(HttpServletRequest request,
			MifosBaseAction mifosBaseAction) {
	}

	protected void visitSetMasterDataForDoNothing(HttpServletRequest request,
			MifosBaseAction mifosBaseAction) {
	}

}//:~
