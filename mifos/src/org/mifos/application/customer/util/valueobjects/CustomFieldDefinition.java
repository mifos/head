/**

 * CustomFieldDefinition.java    version: xxx



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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.mifos.application.customer.center.util.helpers.ValidateMethods;
import org.mifos.application.customer.dao.CustomerUtilDAO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.master.util.valueobjects.LookUpEntity;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * A class that represents a row in the 'custom_field_definition' table.
 * This class may be customized as it is never re-generated
 * after being created.
 */
public class CustomFieldDefinition extends ValueObject{
    /**
     * Simple constructor of CustomFieldDefinition instances.
     */
    public CustomFieldDefinition()
    {
    }


    /** The composite primary key value. */
    private Short fieldId;

    /** The value of the lookupEntity association. */
    private LookUpEntity lookUpEntity;

    /** The value of the simple levelId property. */
    private Short levelId;

    /** The value of the simple fieldType property. */
    private Short fieldType;

    /** The value of the simple entityType property. */
    private Short entityType;

    /** The value of the simple mandatoryFlag property. */
    private Short mandatoryFlag;
 
    private String defaultValue;

    private String mandatoryStringValue;
    /**
     * Return the simple primary key value that identifies this object.
     * @return java.lang.Short
     */
    public Short getFieldId()
    {
        return fieldId;
    }

    /**
     * Set the simple primary key value that identifies this object.
     * @param fieldId
     */
    public void setFieldId(Short fieldId)
    {

        this.fieldId = fieldId;
    }

    /**
     * Return the value of the ENTITY_ID column.
     * @return LookupEntity
     */
    public LookUpEntity getLookUpEntity()
    {
        return this.lookUpEntity;
    }

    /**
     * Set the value of the ENTITY_ID column.
     * @param lookupEntity
     */
    public void setLookUpEntity(LookUpEntity lookUpEntity)
    {
        this.lookUpEntity = lookUpEntity;
    }

    /**
     * Return the value of the LEVEL_ID column.
     * @return Short
     */
    public Short getLevelId()
    {
        return this.levelId;
    }

    /**
     * Set the value of the LEVEL_ID column.
     * @param levelId
     */
    public void setLevelId(Short levelId)
    {
        this.levelId = levelId;
    }

    /**
     * Return the value of the FIELD_TYPE column.
     * @return Short
     */
    public Short getFieldType()
    {
        return this.fieldType;
    }

    /**
     * Set the value of the FIELD_TYPE column.
     * @param fieldType
     */
    public void setFieldType(Short fieldType)
    {
        this.fieldType = fieldType;
    }

    /**
     * Return the value of the ENTITY_TYPE column.
     * @return Short
     */
    public Short getEntityType()
    {
        return this.entityType;
    }

    /**
     * Set the value of the ENTITY_TYPE column.
     * @param entityType
     */
    public void setEntityType(Short entityType)
    {
        this.entityType = entityType;
    }

    /**
     * Return the value of the MANDATORY_FLAG column.
     * @return Short
     */
    public Short getMandatoryFlag()
    {
        return this.mandatoryFlag;
    }

    /**
     * Set the value of the MANDATORY_FLAG column.
     * @param mandatoryFlag
     */
    public void setMandatoryFlag(Short mandatoryFlag)
    {
        this.mandatoryFlag = mandatoryFlag;
    }

    
    public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean equals(Object obj){
		CustomFieldDefinition customFieldDefinition = (CustomFieldDefinition)obj;
		if(this.entityType.equals(customFieldDefinition.getEntityType())
				&& this.levelId.equals(customFieldDefinition.getLevelId())
				  && this.lookUpEntity.equals(customFieldDefinition.getLookUpEntity())
				  	&& this.fieldType.equals(customFieldDefinition.getFieldType())){
			return true;
		}else{
			return false;
		}
	}

	public int hashCode(){
		return entityType.hashCode()*levelId.hashCode()*fieldType.hashCode();
	}

	public String getMandatoryStringValue() {
		return (mandatoryFlag.shortValue() == Constants.YES ? CustomerConstants.YES_SMALL : CustomerConstants.NO_SMALL);
		
	}
	
	public static String convertDateToDbformat(Short fieldId , String fieldValue , Locale locale)throws SystemException {
		if(CustomerUtilDAO.getFieldType(fieldId.shortValue()) == CustomerConstants.DATE_FIELD_TYPE 
				&&! ValidateMethods.isNullOrBlank(fieldValue)){
				SimpleDateFormat sdf = (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.SHORT, locale);
				String userfmt = DateHelper.convertToCurrentDateFormat(((SimpleDateFormat) sdf).toPattern());
				fieldValue = DateHelper.convertUserToDbFmt(fieldValue , userfmt);
				
		}
		return fieldValue;
	}
	public boolean isMandatory(){
		return mandatoryFlag.shortValue()==Constants.YES;
	}
	
	

}
