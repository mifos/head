/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.accounts.financial.business;

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
 * TODO: Rename this class to ChartOfAccountsBO (what else needs to change other
 * than COABO.hbm.xml and the stuff which an Eclipse rename will find?).
 *
 *  <p>Mifos's functional specification defines the Chart of Accounts (CoA) as "the list of accounts
 *  in the general ledger, systematically classified by title and number [the accounts General
 *  Ledger number, or GLCode]. In that context this class is misnamed, since an
 *  instance represents <em>one</em> account in the CoA.
 *
 *  <p>To further add to the confusion, the term "category" sometimes refers to an account
 *  and at other times to a grouping of accounts. Mifos's default COA includes four
 *  categories, ASSET, EXPENDITURE, LIABILITY, and INCOME. Each has several subcategories in
 *  the default CoA.
 *
 *  <p>Graphically speaking, the CoA is a forest of trees, headed by the four categories.
 *  Each account (COABO instance) can have zero or more subcategories (COABO instances)
 *  and zero or one parent (the head categories have no parent). If that's not confusing
 *  enough, only the head COABO instances have non-null <code>categoryType</code> fields, equal
 *  to one of the four strings listed above. The field is null for subcategories (child accounts).
 *
 *  <p>Navigation around this graph is accomplished using two fields:
 *
 *  <ul>
 *  <li><code>subCategories</code> represents the set of child accounts of this
 *  account, although technically it is a  Set of COAHierarchyEntity.</li>
 *  <li><code>coaHierarchy</code> is an entity (<code>COAHierarchyEntity</code>) that
 *  points to the parent account of this account.</li>
 *  <ul>
 *
 *  <p>This is not an optimal -- part of the navigation logic is in this class, and part
 *  is in the <code>COAHierarchyEntity</code>. The hierachical structure of the CoA should be separated
 *  from the accounts themselves. A better way would be to put all chart navigation
 *  into the <code>COAHierarchyEntity</code> instead of half and half.
 */

public class COABO extends BusinessObject {

    private Short accountId;

    private String accountName;

    /**
     * The Set of <code>COAHierarchyEntity</code> objects that are
     * immediate children of this account. Does not include recursive
     * descendants (see method getSubCategories). There is no setter for this
     * field, it is populated by Hibernate -- see query "COABO.getAllCoa" in
     * COABO.hbm.xml.
     *
     * TODO: retype this field as Set<COAHierarchyEntity>
     *
     */
    private Set subCategory;

    private GLCodeEntity associatedGlcode;

    private COAHierarchyEntity coaHierarchy;

    /**
     * Top-level general ledger accounts are also called "categories". If this
     * instance is a "category", this property will be non-null. While the Java
     * type is simply a String, accessors/mutators of this field shall
     * <strong>only</strong> use/provide the GLCategoryType constant. A UserType
     * could also be used to map {@link GLCategoryType} to this field, as seen
     * in <a href="http://www.hibernate.org/288.html">these</a> <a
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
    public COABO(final int accountId, final String accountName) {
        this.accountId = (short) accountId;
        this.accountName = accountName;
    }

    /**
     * Only used in unit tests.
     */
    public COABO(final int accountId, final String accountName, final GLCodeEntity glCodeEntity) {
        this.accountId = (short) accountId;
        this.accountName = accountName;
        this.associatedGlcode = glCodeEntity;
    }

    public COABO(final String accountName, final GLCodeEntity glCodeEntity) {
        this.accountName = accountName;
        this.associatedGlcode = glCodeEntity;
    }

    public Short getAccountId() {
        return accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(final String accountName) {
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

    public void setCoaHierarchy(final COAHierarchyEntity coaHierarchy) {
        this.coaHierarchy = coaHierarchy;
    }

    public GLCategoryType getCategoryType() {
        if (null == categoryType) {
            return null;
        }

        return GLCategoryType.fromString(categoryType);
    }

    public void setCategoryType(final GLCategoryType categoryType) {
        if (null == categoryType) {
            this.categoryType = null;
        } else {
            this.categoryType = categoryType.toString();
        }
    }

  /**
   * As of 2009-10-27, this method is never used outside of test cases its use in
   * getAssociatedChartOfAccounts, so should not be public.
   *
   * TODO Make this method private.
   *
   * @return the set of leaf (bottom-most) descendant accounts of this account,
   * or an empty set if this account has no subcategories (children)
   *
   */

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
     * @return a list of the immediate subcategories (immediate child accounts) of this account,
     * ordered by GLCode. If the account has no subcategories, return an empty list.
     */
    public List<COABO> getSubCategoryCOABOs() {
        List<COABO> subCategories = new ArrayList<COABO>();
        if (subCategory != null) {
            for (COAHierarchyEntity hierarchyEntity : (Set<COAHierarchyEntity>) subCategory) {
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

    /**
     *
     * @return If the account has no subcategories, return this account,
     * otherwise return the leaf (bottom-most) descendants of this account.
     */

    public Set<COABO> getAssociatedChartOfAccounts() {
        Set<COABO> applicableCOA = new HashSet<COABO>();
        if (null == subCategory || subCategory.size() == 0) {
            applicableCOA.add(this);
        } else {
            applicableCOA.addAll(getCurrentSubCategory());
        }
        return applicableCOA;

    }

    /**
     * convenience method gets the top-level accounting cateory
     */
    public GLCategoryType getTopLevelCategoryType() {
        return this.getCOAHead().getCategoryType();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        // if (getClass().)
        if (!(obj instanceof COABO))
            return false;
        COABO other = (COABO) obj;
        if (this.accountId == null) {
            if (other.accountId != null)
                return false;
        } else if (!this.accountId.equals(other.accountId))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.accountId == null) ? 0 : this.accountId.hashCode());
        return result;
    }

    /**
     * Compares general ledger codes lexicographically. Must use this type of
     * comparison since GL codes may have numbers <em>and</em> letters.
     */
    public class GLCodeComparator implements Comparator<COABO> {
        public int compare(final COABO coa1, final COABO coa2) {
            return coa1.getAssociatedGlcode().getGlcode().compareTo(coa2.getAssociatedGlcode().getGlcode());
        }
    }

}
