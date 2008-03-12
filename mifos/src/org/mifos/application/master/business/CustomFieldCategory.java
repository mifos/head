/**

 * CustomFieldCategory.java

 

 * Copyright (c) 2005-2007 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2007 Grameen Foundation USA 
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

package org.mifos.application.master.business;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountCustomFieldEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerCustomFieldEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeCustomFieldEntity;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelCustomFieldEntity;
import org.mifos.application.util.helpers.EntityType;

/**
 * CustomFieldCategory is the set of object types for which custom 
 * fields are defined.  Each custom field for a given object type 
 * is defined by a {@link CustomFieldDefinitionEntity}.
 * 
 * Custom field instances are represented by:
 * 
 * {@link CustomerCustomFieldEntity} in {@link CustomerBO} (from which 
 * {@link ClientBO}, {@link GroupBO} and {@link CenterBO} are derived)
 * 
 * {@link AccountCustomFieldEntity} in {@link AccountBO} (from which 
 * {@link LoanBO} and {@link SavingsBO} are derived)
 * 
 * {@link PersonnelCustomFieldEntity} in {@link PersonnelBO}
 * 
 * {@link OfficeCustomFieldEntity} in {@link OfficeBO}
 */
public enum CustomFieldCategory {
	Personnel (17),
	Office (15),
	Client (1),
	Group (12),
	Center (20),
	Loan (22),
	Savings (21);
	
	Short value;

	CustomFieldCategory(int value) {
		this.value = (short)value;
	}

	public Short getValue() {
		return value;
	}
	
	public static CustomFieldCategory fromInt(int type) {
		for (CustomFieldCategory candidate : CustomFieldCategory.values()) {
			if (candidate.getValue() == type) {
				return candidate;
			}
		}
		throw new RuntimeException("no custom field category type " + type);
	}
	
	public EntityType mapToEntityType() {
		switch (this) {
			case Personnel: return EntityType.PERSONNEL;
			case Office: 	return EntityType.OFFICE;
			case Client:	return EntityType.CLIENT;
			case Group:		return EntityType.GROUP;
			case Center:	return EntityType.CENTER;
			case Loan:		return EntityType.LOAN;
			case Savings:	return EntityType.SAVINGS;
			default:		throw new RuntimeException("Unrecognized CustomFieldCategory: \"" + this + "\"");
		}
	}
	
	public static CustomFieldCategory getCustomFieldCategoryFromString(String category) {
		for (CustomFieldCategory customFieldCategory : values()) {
			if (category.equalsIgnoreCase(customFieldCategory.toString())) {
				return customFieldCategory;
			}
		}
		throw new RuntimeException("Unrecognized CustomFieldCategory: \"" + category + "\"");
	}
}
