package org.mifos.application.master.business;

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
	Personnel,
	Office,
	Client,
	Group,
	Center,
	Loan,
	Savings;
}
