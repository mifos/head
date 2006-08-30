/**

 * CustomerHierarchy.java    version: xxx



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

package org.mifos.application.customer.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * A class that represents a row in the 'customer_hierarchy' table.
 * This class may be customized as it is never re-generated
 * after being created.
 */
public class CustomerHierarchy extends ValueObject{
    /**
     * Simple constructor of CustomerHierarchy instances.
     */
    public CustomerHierarchy()
    {
    }

/** The composite primary key value. */
    private java.lang.Integer hierarchyId;

    /** The value of the parentCustomer association. */
    private Customer parentCustomer;

    /** The value of the customer association. */
    private Customer customer;

    /** The value of the simple parentId property. */
    private java.lang.Integer parentId;

    /** The value of the simple status property. */
    private java.lang.Short status;

    /** The value of the simple startDate property. */
    private java.sql.Date startDate;

    /** The value of the simple endDate property. */
    private java.sql.Date endDate;

    /** The value of the simple updatedBy property. */
    private java.lang.Short updatedBy;

    /** The value of the simple updatedDate property. */
    private java.sql.Date updatedDate;


    /**
     * Return the simple primary key value that identifies this object.
     * @return java.lang.Integer
     */
    public java.lang.Integer getHierarchyId()
    {
        return hierarchyId;
    }

    /**
     * Set the simple primary key value that identifies this object.
     * @param hierarchyId
     */
    public void setHierarchyId(java.lang.Integer hierarchyId)
    {

        this.hierarchyId = hierarchyId;
    }

    /**
     * Return the value of the PARENT_ID column.
     * @return java.lang.Integer
     */
    public java.lang.Integer getParentId()
    {
        return this.parentId;
    }

    /**
     * Set the value of the PARENT_ID column.
     * @param parentId
     */
    public void setParentId(java.lang.Integer parentId)
    {
        this.parentId = parentId;
    }



    /**
     * Return the value of the STATUS column.
     * @return java.lang.Short
     */
    public java.lang.Short getStatus()
    {
        return this.status;
    }

    /**
     * Set the value of the STATUS column.
     * @param status
     */
    public void setStatus(java.lang.Short status)
    {
        this.status = status;
    }

    /**
     * Return the value of the START_DATE column.
     * @return java.util.Date
     */
    public java.sql.Date getStartDate()
    {
        return this.startDate;
    }

    /**
     * Set the value of the START_DATE column.
     * @param startDate
     */
    public void setStartDate(java.sql.Date startDate)
    {
        this.startDate = startDate;
    }

    /**
     * Return the value of the END_DATE column.
     * @return java.util.Date
     */
    public java.sql.Date getEndDate()
    {
        return this.endDate;
    }

    /**
     * Set the value of the END_DATE column.
     * @param endDate
     */
    public void setEndDate(java.sql.Date endDate)
    {
        this.endDate = endDate;
    }

    /**
     * Return the value of the UPDATED_BY column.
     * @return java.lang.Short
     */
    public java.lang.Short getUpdatedBy()
    {
        return this.updatedBy;
    }

    /**
     * Set the value of the UPDATED_BY column.
     * @param updatedBy
     */
    public void setUpdatedBy(java.lang.Short updatedBy)
    {
        this.updatedBy = updatedBy;
    }

    /**
     * Return the value of the UPDATED_DATE column.
     * @return java.util.Date
     */
    public java.sql.Date getUpdatedDate()
    {
        return this.updatedDate;
    }

	/**
	 * @return Returns the customer.
	 */
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * @param customer The customer to set.
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * @return Returns the parentCustomer.
	 */
	public Customer getParentCustomer() {
		return parentCustomer;
	}

	/**
	 * @param parentCustomer The parentCustomer to set.
	 */
	public void setParentCustomer(Customer parentCustomer) {
		this.parentCustomer = parentCustomer;
	}

	/**
	 * @param updatedDate The updatedDate to set.
	 */
	public void setUpdatedDate(java.sql.Date updatedDate) {
		this.updatedDate = updatedDate;
	}

}
