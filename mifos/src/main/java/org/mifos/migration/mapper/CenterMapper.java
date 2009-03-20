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
 
package org.mifos.migration.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.hibernate.Session;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.customer.business.CustomerCustomFieldEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.migration.generated.Center;
import org.mifos.migration.generated.CustomField;
import org.mifos.migration.generated.FeeAmount;


public class CenterMapper {
	public static CenterBO mapCenterToCenterBO(Center center,
			UserContext userContext) {
		org.mifos.framework.business.util.Address address = 
			AddressMapper.mapXMLAddressToMifosAddress(center.getAddress());
		MeetingBO meeting = null;
		Date mfiJoiningDate = mapXMLGregorianCalendarToDate(
			center.getMfiJoiningDate());
		
		RecurrenceType meetingFrequency;
		if (center.getMonthlyMeeting() != null) {
			meeting = MeetingMapper.mapMonthlyMeetingToMeetingBO(
				center.getMonthlyMeeting());
			meetingFrequency = RecurrenceType.MONTHLY;
		}
		else {
			meeting = MeetingMapper.mapWeeklyMeetingToMeetingBO(
				center.getWeeklyMeeting());
			meetingFrequency = RecurrenceType.WEEKLY;
		}

		// TODO: extract Fee handling for reuse
		FeePersistence feePersistence = new FeePersistence();
		List<FeeView> fees = new ArrayList<FeeView>();
		for (FeeAmount feeAmount : center.getFeeAmount()) {
			FeeBO fee = feePersistence.getFee(feeAmount.getFeeId());
			if (fee == null) {
				throw new RuntimeException("Fee lookup failed for id: " + feeAmount.getFeeId());
			}
			CheckForFeeMeetingFrequencyMismatch(fee, meetingFrequency);
			
			FeeView feeView = new FeeView(userContext, fee);
			feeView.setAmount(feeAmount.getAmount().toPlainString());

			fees.add(feeView);
		}

		// TODO: split out CustomField handling for reuse		
		List<CustomFieldView> fields = new ArrayList<CustomFieldView>();
		for (CustomField field : center.getCustomField()) {
			if (field.getFieldId() != null) {
				String value;
				CustomFieldType type;
				if (field.getStringValue() != null) {
					value = field.getStringValue();
					type = CustomFieldType.ALPHA_NUMERIC;
				} else if (field.getNumericValue() != null) {
					value = ""+field.getNumericValue();
					type = CustomFieldType.NUMERIC;
				} else if (field.getDateValue() != null) {
					value = ""+field.getDateValue();
					type = CustomFieldType.DATE;
				} else {
					throw new RuntimeException("No custom field value was specified");
				}
				fields.add(new CustomFieldView(
					field.getFieldId(), value, type)); 
			} else {
				throw new RuntimeException("Custom field lookup by name not yet implemented");
			}
		}

		CenterBO centerBO;
		try {
			centerBO = new CenterBO(userContext, center.getName(), address,
					fields, fees, center.getExternalId(), mfiJoiningDate,
					new OfficePersistence().getOffice(center.getOfficeId()), 
					meeting, new PersonnelPersistence().getPersonnel(center.getLoanOfficerId()), new CustomerPersistence());
		}
		catch (CustomerException e) {
			throw new RuntimeException(e);
		} catch (PersistenceException e) {
            throw new RuntimeException(e);
        }


		return centerBO;
	}

	private static void CheckForFeeMeetingFrequencyMismatch(FeeBO fee, RecurrenceType meetingFrequency) {
		if (fee.isPeriodic() &&
				((fee.getFeeFrequency().getFeeMeetingFrequency().isMonthly() && meetingFrequency != RecurrenceType.MONTHLY) ||
						(fee.getFeeFrequency().getFeeMeetingFrequency().isWeekly() && meetingFrequency != RecurrenceType.WEEKLY))) {
			String feePeriod = "UNKNOWN";
			if (fee.getFeeFrequency().getFeeMeetingFrequency().isMonthly()) {
				feePeriod = "MONTHLY";
			} else if (fee.getFeeFrequency().getFeeMeetingFrequency().isWeekly()) {
				feePeriod = "WEEKLY";
			}
			throw new RuntimeException("Fee period and center meeting frequency " +
					"don't match. Fee frequency is " + feePeriod + 
					" while meeting frequency is " + meetingFrequency.name());
		}
	}
	
	public static Date mapXMLGregorianCalendarToDate(
			XMLGregorianCalendar calendar) {
		if (calendar == null)
			return null;

		return calendar.toGregorianCalendar().getTime();
	}

	public static XMLGregorianCalendar mapDateToXMLGregorianCalendar(Date date) {
		if (date == null)
			return null;

		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		DatatypeFactory datatypeFactory;
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		}
		catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}

		XMLGregorianCalendar xmlCalendar = datatypeFactory
				.newXMLGregorianCalendar(calendar);
		// we want only the date, so don't use a timezone
		xmlCalendar.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

		return xmlCalendar;
	}
	
	public static XMLGregorianCalendar mapStringToXMLGregorianCalendar(String dateString) {
		DatatypeFactory datatypeFactory;
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		}
		catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
		XMLGregorianCalendar xmlCalendar = datatypeFactory
		.newXMLGregorianCalendar(dateString);
		// we want only the date, so don't use a timezone
		xmlCalendar.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		
		return xmlCalendar;
	}

	public static Center mapCenterBOToCenter(CenterBO center) {
		Center newCenter = new Center();
		newCenter.setName(center.getDisplayName());
		newCenter.setOfficeId(center.getOffice().getOfficeId());
		newCenter.setLoanOfficerId(center.getPersonnel().getPersonnelId());
		MeetingBO meeting = center.getCustomerMeeting().getMeeting();
		if (meeting.isMonthly()) {
			newCenter.setMonthlyMeeting(
				MeetingMapper.mapMeetingBOToMonthlyMeeting(meeting));
		}
		else if (meeting.isWeekly()) {
			newCenter.setWeeklyMeeting(
				MeetingMapper.mapMeetingBOToWeeklyMeeting(meeting));
		}
		else {
			throw new RuntimeException(
					"Unhandled meeting type (not MONTHLY or WEEKLY)");
		}
		newCenter.setExternalId(center.getExternalId());
		newCenter.setMfiJoiningDate(mapDateToXMLGregorianCalendar(
			center.getMfiJoiningDate()));
		newCenter.setAddress(AddressMapper.mapMifosAddressToXMLAddress(
			center.getAddress()));

		// TODO: extract CustomField handling for reuse
		Set<CustomerCustomFieldEntity> fields = center.getCustomFields();
		List<CustomField> customFields = newCenter.getCustomField();
		for (CustomerCustomFieldEntity field : fields) {
			CustomField customField = new CustomField();
			customField.setFieldId(field.getFieldId());
			CustomFieldDefinitionEntity fieldDefinition = getCustomFieldDefinitionEntity(field.getFieldId());
			if (fieldDefinition.getFieldType().compareTo(CustomFieldType.ALPHA_NUMERIC.getValue()) == 0) {
				customField.setStringValue(field.getFieldValue());
			} else if (fieldDefinition.getFieldType().compareTo(CustomFieldType.NUMERIC.getValue()) == 0) {
				customField.setNumericValue(Integer.valueOf(field.getFieldValue()));
			} else if (fieldDefinition.getFieldType().compareTo(CustomFieldType.DATE.getValue()) == 0) {
				customField.setDateValue(mapStringToXMLGregorianCalendar(field.getFieldValue()));
			} else {
				throw new RuntimeException("Unhandled CustomFieldType");
			}			
			customFields.add(customField);
		}

		// TODO: extract Fee handling for reuse
		List<FeeAmount> feeAmounts = newCenter.getFeeAmount();
		Set<AccountFeesEntity> accountFees = 
			center.getCustomerAccount().getAccountFees();
		for (AccountFeesEntity feeEntity : accountFees) {
			FeeBO fee = feeEntity.getFees();
			Double amount = feeEntity.getFeeAmount();
			FeeAmount feeAmount = new FeeAmount();
			feeAmount.setAmount(BigDecimal.valueOf(amount));
			feeAmount.setFeeId(fee.getFeeId());
			feeAmounts.add(feeAmount);
		}

		// TODO: an order is imposed on output for testing purposes
		// so that the output order can be made to match the input
		// order.  There should be a better way to do this 
		sortFeeAmountsByFeeId(newCenter);
		sortCustomFieldsById(newCenter);
		
		return newCenter;
	}
	
	private static CustomFieldDefinitionEntity getCustomFieldDefinitionEntity(Short id) {
		Session session = StaticHibernateUtil.getSessionTL();
		return (CustomFieldDefinitionEntity) session.get(CustomFieldDefinitionEntity.class, id);		
	}
	
	/* Sort the FeeAmount list by feeId to make sure we get the fees back
	 * in the same order they were created 
	 */
	private static void sortFeeAmountsByFeeId(Center center) {
		final class FeeAmountFeeIdComparator implements Comparator<FeeAmount> {
			public int compare(FeeAmount fee1, FeeAmount fee2) {
				return fee1.getFeeId() - fee2.getFeeId();
			}		
		}
		Collections.sort(center.getFeeAmount(), new FeeAmountFeeIdComparator());
	}

	/* Sort the CustomField list by fieldId to make sure we get them back
	 * in the same order they were created 
	 */
	private static void sortCustomFieldsById(Center center) {
		final class CustomFieldIdComparator implements Comparator<CustomField> {
			public int compare(CustomField field1, CustomField field2) {
				return field1.getFieldId() - field2.getFieldId();
			}		
		}
		Collections.sort(center.getCustomField(), new CustomFieldIdComparator());
	}
	
}
