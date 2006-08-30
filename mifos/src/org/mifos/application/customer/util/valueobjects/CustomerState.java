/**

 * CustomerState.java    version: xxx



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



import org.mifos.application.master.util.valueobjects.LookUpEntity;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * A class that represents a row in the 'customer_state' table.
 * This class may be customized as it is never re-generated
 * after being created.
 */
public class CustomerState extends ValueObject{

    /**
     * Simple constructor of CustomerState instances.
     */
    public CustomerState()
    {
    }

/** The composite primary key value. */
    private java.lang.Short statusId;

    /** The value of the lookupEntity association. */
    private LookUpEntity lookUpEntity;

    /** The value of the customerLevel association. */
    private CustomerLevel customerLevel;

    /** The value of the customerChecklistSet one-to-many association. */
    private java.util.Set customerChecklistSet;

    /** The value of the simple description property. */
    private java.lang.String description;


    /**
     * Return the simple primary key value that identifies this object.
     * @return java.lang.Short
     */
    public java.lang.Short getStatusId()
    {
        return statusId;
    }

    /**
     * Set the simple primary key value that identifies this object.
     * @param statusId
     */
    public void setStatusId(java.lang.Short statusId)
    {

        this.statusId = statusId;
    }

    /**
     * Return the value of the LEVEL_ID column.
     * @return CustomerLevel
     */
    public CustomerLevel getCustomerLevel()
    {
        return this.customerLevel;
    }

    /**
     * Set the value of the LEVEL_ID column.
     * @param customerLevel
     */
    public void setCustomerLevel(CustomerLevel customerLevel)
    {
        this.customerLevel = customerLevel;
    }

    /**
     * Return the value of the STATUS_LOOKUP_NAME column.
     * @return LookupEntity
     */
    public LookUpEntity getLookUpEntity()
    {
        return this.lookUpEntity;
    }

    /**
     * Set the value of the STATUS_LOOKUP_NAME column.
     * @param lookupEntity
     */
    public void setLookUpEntity(LookUpEntity lookUpEntity)
    {
        this.lookUpEntity = lookUpEntity;
    }

    /**
     * Return the value of the DESCRIPTION column.
     * @return java.lang.String
     */
    public java.lang.String getDescription()
    {
        return this.description;
    }

    /**
     * Set the value of the DESCRIPTION column.
     * @param description
     */
    public void setDescription(java.lang.String description)
    {
        this.description = description;
    }

    /**
     * Return the value of the CUSTOMER_STATUS_ID collection.
     * @return CustomerChecklist
     */
    public java.util.Set getCustomerChecklistSet()
    {
        return this.customerChecklistSet;
    }

    /**
     * Set the value of the CUSTOMER_STATUS_ID collection.
     * @param customerChecklistSet
     */
    public void setCustomerChecklistSet(java.util.Set customerChecklistSet)
    {
        this.customerChecklistSet = customerChecklistSet;
    }

}
