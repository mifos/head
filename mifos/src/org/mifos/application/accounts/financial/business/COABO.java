/**

 * COABO.java    version: 1.0

 

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

package org.mifos.application.accounts.financial.business;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.mifos.framework.business.BusinessObject;

/**
 * Chart of Accounts.
 * 
 * TODO: Rename this class to ChartOfAccountsBO (what else needs to change
 * other than COABO.hbm.xml and the stuff which an Eclipse rename will
 * find?).
 */
public class COABO extends BusinessObject {
	private Short categoryId;

	private String categoryName;

	private Set subCategory;

	private GLCodeEntity associatedGlcode;

	private COAHierarchyEntity coaHierarchy;

	protected COABO() {
		super();
		categoryId = null;
		categoryName = null;
		subCategory = new HashSet();
		associatedGlcode = null;
		coaHierarchy = null;
	}
	
	public COABO(int categoryId, String categoryName) {
		this.categoryId = (short) categoryId;
		this.categoryName = categoryName;
	}

	public Short getCategoryId() {
		return categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public Set getSubCategory() {
		return subCategory;
	}

	public GLCodeEntity getAssociatedGlcode() {
		return associatedGlcode;
	}

	public COAHierarchyEntity getCoaHierarchy() {
		return coaHierarchy;
	}

	public Set<COABO> getCurrentSubCategory() {
		Set<COABO> applicableCOA = new HashSet<COABO>();
		if (subCategory != null) {
			Iterator<COAHierarchyEntity> iter = subCategory.iterator();
			while (iter.hasNext()) {
				COAHierarchyEntity coaHierarchy = iter.next();
				applicableCOA.addAll(coaHierarchy.getCoa().getAssociatedChartOfAccounts());
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

	public Set<COABO> getAssociatedChartOfAccounts() {
		Set<COABO> applicableCOA = new HashSet<COABO>();
		if (subCategory.size() == 0) {
			applicableCOA.add(this);
		} else {
			applicableCOA.addAll(getCurrentSubCategory());
		}
		return applicableCOA;

	}

	@Override
	public final boolean equals(Object otherObject) {
        if (!(otherObject instanceof COABO)) {
			return false;
		}

		COABO other = (COABO) otherObject;
		if (other.getCategoryId().shortValue() == this.categoryId
				.shortValue())
			return true;
		return false;
	}

    @Override
    public int hashCode() {
        if (this.getCategoryId() == null) {
            return super.hashCode();
        }
        else {
            return(this.getCategoryId().hashCode());
        }
    }

}
