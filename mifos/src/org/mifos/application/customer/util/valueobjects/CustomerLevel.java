/**

 * CustomerLevel.java    version: xxx



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
package org.mifos.application.customer.util.valueobjects;
import java.io.Serializable;

import org.mifos.application.master.util.valueobjects.LookUpEntity;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * A class that represents a row in the 'customer_level' table.
 * This class may be customized as it is never re-generated
 * after being created.
 */
public class CustomerLevel extends ValueObject

{
    /**
     * Simple constructor of CustomerLevel instances.
     */
    public CustomerLevel()
    {
    }

     /** The composite primary key value. */
    private java.lang.Short levelId;

    /** The value of the customerLevel association. */
    private CustomerLevel parentCustomerLevel;

    /** The value of the lookupEntity association. */
    private LookUpEntity lookUpEntity;

    /** The value of the simple interactionFlag property. */
    private java.lang.Short interactionFlag;

    /** The value of the simple maxChildCount property. */
    private java.lang.Short maxChildCount;

    /** The value of the simple maxInstanceCount property. */
    private java.lang.Short maxInstanceCount;



    /**
     * Return the simple primary key value that identifies this object.
     * @return java.lang.Short
     */
    public java.lang.Short getLevelId()
    {
        return levelId;
    }



    /**
     * Return the value of the PARENT_LEVEL_ID column.
     * @return CustomerLevel
     */
    public CustomerLevel getParentCustomerLevel()
    {
        return parentCustomerLevel;
    }

    /**
     * Set the value of the PARENT_LEVEL_ID column.
     * @param customerLevel
     */
    public void setParentCustomerLevel(CustomerLevel parentCustomerLevel)
    {
        this.parentCustomerLevel = parentCustomerLevel;
    }

    /**
     * Return the value of the LEVEL_NAME_ID column.
     * @return LookupEntity
     */
    public LookUpEntity getLookupEntity()
    {
        return this.lookUpEntity;
    }

    /**
     * Set the value of the LEVEL_NAME_ID column.
     * @param lookupEntity
     */
    public void setLookUpEntity(LookUpEntity lookUpEntity)
    {
        this.lookUpEntity = lookUpEntity;
    }

    /**
     * Return the value of the INTERACTION_FLAG column.
     * @return java.lang.Short
     */
    public java.lang.Short getInteractionFlag()
    {
        return this.interactionFlag;
    }

    /**
     * Set the value of the INTERACTION_FLAG column.
     * @param interactionFlag
     */
    public void setInteractionFlag(java.lang.Short interactionFlag)
    {
        this.interactionFlag = interactionFlag;
    }

    /**
     * Return the value of the MAX_CHILD_COUNT column.
     * @return java.lang.Short
     */
    public java.lang.Short getMaxChildCount()
    {
        return this.maxChildCount;
    }

    /**
     * Set the value of the MAX_CHILD_COUNT column.
     * @param maxChildCount
     */
    public void setMaxChildCount(java.lang.Short maxChildCount)
    {
        this.maxChildCount = maxChildCount;
    }

    /**
     * Return the value of the MAX_INSTANCE_COUNT column.
     * @return java.lang.Short
     */
    public java.lang.Short getMaxInstanceCount()
    {
        return this.maxInstanceCount;
    }

    /**
     * Set the value of the MAX_INSTANCE_COUNT column.
     * @param maxInstanceCount
     */
    public void setMaxInstanceCount(java.lang.Short maxInstanceCount)
    {
        this.maxInstanceCount = maxInstanceCount;
    }



	/**
	 * @return Returns the lookUpEntity}.
	 */
	public LookUpEntity getLookUpEntity() {
		return lookUpEntity;
	}



	/**
	 * @param levelId The levelId to set.
	 */
	public void setLevelId(java.lang.Short levelId) {
		this.levelId = levelId;
	}







}
