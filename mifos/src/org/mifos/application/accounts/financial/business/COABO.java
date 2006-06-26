/**

 * COABO.java    version: 1.0

 

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

package org.mifos.application.accounts.financial.business;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.mifos.framework.business.BusinessObject;

public class COABO extends BusinessObject {
	private Short categoryId;

	private String categoryName;

	private Set subCategory;

	private GLCodeEntity associatedGlcode;

	private COAHierarchyEntity coaHierarchy;

	public COABO() {
		super(null);
	}

	public Short getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Short categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Set getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(Set subCategory) {
		this.subCategory = subCategory;
	}

	public GLCodeEntity getAssociatedGlcode() {
		return associatedGlcode;
	}

	public void setAssociatedGlcode(GLCodeEntity glcode) {
		this.associatedGlcode = glcode;
	}

	public COAHierarchyEntity getCoaHierarchy() {
		return coaHierarchy;
	}

	public void setCoaHierarchy(COAHierarchyEntity coaHierarchy) {
		this.coaHierarchy = coaHierarchy;
	}

	public Set<COABO> getCurrentSubCategory() {
		Set<COABO> applicableCOA = new HashSet<COABO>();
		if (subCategory != null) {
			Iterator<COAHierarchyEntity> iter = subCategory.iterator();
			while (iter.hasNext()) {
				COAHierarchyEntity coaHierarchy = iter.next();
				applicableCOA.addAll(coaHierarchy.getCoa().getAsscoicatedCOA());
			}
		}
		return applicableCOA;
	}

	public COABO getCOAHead() {
		COAHierarchyEntity headHiearchy = null;
		COAHierarchyEntity currentHiearchy = null;
		currentHiearchy = coaHierarchy;
		headHiearchy = currentHiearchy.getParentCategory();
		while (headHiearchy != null) {
			currentHiearchy = headHiearchy;
			headHiearchy = headHiearchy.getParentCategory();

		}

		return currentHiearchy.getCoa();

	}

	public Set<COABO> getAsscoicatedCOA() {
		Set<COABO> applicableCOA = new HashSet<COABO>();
		if (subCategory.size() == 0) {
			applicableCOA.add(this);
		} else {
			applicableCOA.addAll(getCurrentSubCategory());
		}
		return applicableCOA;

	}

	public boolean equals(Object obj) {

		COABO coaIncomming = (COABO) obj;
		if (coaIncomming.getCategoryId().shortValue() == this.categoryId
				.shortValue())
			return true;
		return false;
	}

	@Override
	public Short getEntityID() {
		return null;
	}

}
