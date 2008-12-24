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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
	
	private Short accountId;

	private String accountName;

	/** Child accounts are also called subcategories. */
	private Set subCategory;

	private GLCodeEntity associatedGlcode;

	private COAHierarchyEntity coaHierarchy;
	
	/**
	 * Top-level general ledger accounts are also called "categories". If this
	 * instance is a "category", this property will be non-null. While the Java
	 * type is simply a String, accessors/mutators of this field shall
	 * <strong>only</strong> use/provide the GLCategoryType constant. A
	 * UserType could also be used to map {@link GLCategoryType} to this field,
	 * as seen in <a href="http://www.hibernate.org/288.html">these</a> <a
	 * href="http://www.hibernate.org/203.html">examples</a>, but this
	 * "lightweight" solution seemed Good Enough for the time being.
	 */
	private String categoryType;

	protected COABO() {
		super();
		accountId = null;
		accountName = null;
		subCategory = new HashSet();
		associatedGlcode = null;
		coaHierarchy = null;
		categoryType = null;
	}
	
	/**
	 * Only used in unit tests.
	 */
	public COABO(int accountId, String accountName) {
		this.accountId = (short) accountId;
		this.accountName = accountName;
	}

	/**
	 * Only used in unit tests.
	 */
	public COABO(int accountId, String accountName, GLCodeEntity glCodeEntity) {
		this.accountId = (short) accountId;
		this.accountName = accountName;
		this.associatedGlcode = glCodeEntity;
	}
	
	public COABO(String accountName, GLCodeEntity glCodeEntity) {
		this.accountName = accountName;
		this.associatedGlcode = glCodeEntity;
	}

	public Short getAccountId() {
		return accountId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
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

	public void setCoaHierarchy(COAHierarchyEntity coaHierarchy) {
		this.coaHierarchy = coaHierarchy;
	}

	public GLCategoryType getCategoryType() {
		if (null == categoryType)
			return null;
		else
			return GLCategoryType.fromString(categoryType);
	}

	public void setCategoryType(GLCategoryType categoryType) {
		if (null == categoryType)
			this.categoryType = null;
		else
			this.categoryType = categoryType.toString();
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
	
	public String getGlCode() {
		return getAssociatedGlcode().getGlcode();
	}

	/**
	 * Get a list of the subcategories of this account
	 * ordered by GLCode.
	 */
	public List<COABO> getSubCategoryCOABOs() {
		List<COABO> subCategories = new ArrayList<COABO>();
		if (subCategory != null) {
			for (COAHierarchyEntity hierarchyEntity : (Set<COAHierarchyEntity>)subCategory) {
				subCategories.add(hierarchyEntity.getCoa());			
			}
		}
		Collections.sort(subCategories, new GLCodeComparator());
		return subCategories;
	}

	
	public COABO getCOAHead() {
		COAHierarchyEntity headHiearchy = null;
		COAHierarchyEntity currentHiearchy = null;
		currentHiearchy = coaHierarchy;
		headHiearchy = currentHiearchy.getParentAccount();
		while (headHiearchy != null) {
			currentHiearchy = headHiearchy;
			headHiearchy = headHiearchy.getParentAccount();

		}

		return currentHiearchy.getCoa();

	}

	public Set<COABO> getAssociatedChartOfAccounts() {
		Set<COABO> applicableCOA = new HashSet<COABO>();
		if (null == subCategory || subCategory.size() == 0) {
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
		if (other.getAccountId().equals(this.accountId))
			return true;
		return false;
	}

    @Override
    public int hashCode() {
        if (this.getAccountId() == null) {
            return super.hashCode();
        }
        else {
            return(this.getAccountId().hashCode());
        }
    }

    /**
     * Compares general ledger codes lexicographically. Must use this type of
     * comparison since GL codes may have numbers <em>and</em> letters. 
     */
    public class GLCodeComparator implements Comparator<COABO> {
		public int compare(COABO coa1, COABO coa2) { 
			return coa1.getAssociatedGlcode().getGlcode().compareTo(
					coa2.getAssociatedGlcode().getGlcode());
		}
	}

}
