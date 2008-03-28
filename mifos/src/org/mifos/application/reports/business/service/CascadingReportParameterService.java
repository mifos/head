/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
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
package org.mifos.application.reports.business.service;

import static org.mifos.application.reports.ui.DateSelectionItem.NA_MEETING_DATE;
import static org.mifos.application.reports.ui.SelectionItem.ALL_CENTER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.ALL_LOAN_OFFICER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.NA_BRANCH_OFFICE_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.NA_CENTER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.NA_LOAN_OFFICER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_BRANCH_OFFICE_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_CENTER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_LOAN_OFFICER_SELECTION_ITEM;
import static org.mifos.framework.util.helpers.NumberUtils.convertShortToInteger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.reports.ui.DateSelectionItem;
import org.mifos.application.reports.ui.SelectionItem;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.CollectionUtils;
import org.mifos.framework.util.helpers.DateUtils;

public class CascadingReportParameterService {

	private IReportsParameterService reportsParameterService;
	private PersonnelBusinessService personnelBusinessService;
	private CustomerBusinessService customerBusinessService;

	public CascadingReportParameterService(
			IReportsParameterService reportsParameterService,
			PersonnelBusinessService personnelBusinessService,
			CustomerBusinessService customerBusinessService) {
		this.reportsParameterService = reportsParameterService;
		this.personnelBusinessService = personnelBusinessService;
		this.customerBusinessService = customerBusinessService;
	}

	public List<SelectionItem> getBranchOfficesUnderUser(PersonnelBO user)
			throws ServiceException {
		List<SelectionItem> offices = new ArrayList<SelectionItem>();
		ArrayList<SelectionItem> branchOffices = new ArrayList<SelectionItem>();

		if (user == null)
			return CollectionUtils.asList(NA_BRANCH_OFFICE_SELECTION_ITEM);

		offices = reportsParameterService.getActiveBranchesUnderUser(user
				.getOfficeSearchId());

		if (offices.isEmpty())
			return CollectionUtils.asList(NA_BRANCH_OFFICE_SELECTION_ITEM);

		branchOffices.add(SELECT_BRANCH_OFFICE_SELECTION_ITEM);
		branchOffices.addAll(offices);
		return branchOffices;
	}

	public List<SelectionItem> getActiveLoanOfficersUnderUserInBranch(
			PersonnelBO user, Short branchId) throws ServiceException {
		List<SelectionItem> officers = new ArrayList<SelectionItem>();

		if (NA_BRANCH_OFFICE_SELECTION_ITEM.sameAs(branchId)) {
			officers.add(NA_LOAN_OFFICER_SELECTION_ITEM);
			return officers;
		}
		if (branchId == null
				|| SELECT_BRANCH_OFFICE_SELECTION_ITEM.sameAs(branchId)) {
			officers.add(SELECT_LOAN_OFFICER_SELECTION_ITEM);
			return officers;
		}

		if (user == null) {
			officers.add(NA_LOAN_OFFICER_SELECTION_ITEM);
			return officers;
		}

		if (user.isLoanOfficer()) {
			officers.add(SELECT_LOAN_OFFICER_SELECTION_ITEM);
			officers.add(new SelectionItem(user.getPersonnelId(), user
					.getDisplayName()));
			return officers;
		}

		List<SelectionItem> loanOfficers = reportsParameterService
				.getActiveLoanOfficersUnderOffice(branchId);
		officers.add(loanOfficers.isEmpty() ? NA_LOAN_OFFICER_SELECTION_ITEM
				: ALL_LOAN_OFFICER_SELECTION_ITEM);
		officers.addAll(loanOfficers);
		return officers;
	}

	public List<SelectionItem> getActiveCentersInBranchForLoanOfficer(
			Short branchId, Short loanOfficerId) throws ServiceException {
		List<SelectionItem> activeCenters = new ArrayList<SelectionItem>();
		if (loanOfficerId == null
				|| SELECT_LOAN_OFFICER_SELECTION_ITEM.sameAs(loanOfficerId))
			return CollectionUtils.asList(SELECT_CENTER_SELECTION_ITEM);

		if (NA_LOAN_OFFICER_SELECTION_ITEM.sameAs(loanOfficerId))
			return CollectionUtils.asList(NA_CENTER_SELECTION_ITEM);

		List<SelectionItem> loanOfficers = null;
		if (ALL_LOAN_OFFICER_SELECTION_ITEM.sameAs(loanOfficerId)) {
			loanOfficers = reportsParameterService
					.getActiveLoanOfficersUnderOffice(branchId);
		}
		else {
			PersonnelBO loanOfficer = personnelBusinessService
					.getPersonnel(loanOfficerId);
			loanOfficers = CollectionUtils.asList(new SelectionItem(loanOfficer
					.getPersonnelId(), loanOfficer.getDisplayName()));
		}

		List<SelectionItem> allCentersUnderBranch = new ArrayList<SelectionItem>();
		for (SelectionItem loanOfficer : loanOfficers) {
			List<SelectionItem> activeCentersUnderUser = reportsParameterService
					.getActiveCentersUnderUser(branchId, loanOfficer.getId());
			allCentersUnderBranch.addAll(activeCentersUnderUser);
		}
		activeCenters
				.add(allCentersUnderBranch.isEmpty() ? NA_CENTER_SELECTION_ITEM
						: ALL_CENTER_SELECTION_ITEM);
		activeCenters.addAll(allCentersUnderBranch);
		return activeCenters;
	}

	public List<DateSelectionItem> getMeetingDates(Short branchId,
			Short officerId, Integer customerId) throws ServiceException {
		List<DateSelectionItem> meetingDates = new ArrayList<DateSelectionItem>();
		if (branchId == null
				|| SELECT_BRANCH_OFFICE_SELECTION_ITEM.sameAs(branchId)
				|| NA_BRANCH_OFFICE_SELECTION_ITEM.sameAs(branchId)
				|| SELECT_LOAN_OFFICER_SELECTION_ITEM.sameAs(officerId)
				|| NA_LOAN_OFFICER_SELECTION_ITEM.sameAs(officerId)
				|| SELECT_CENTER_SELECTION_ITEM.sameAs(customerId)
				|| NA_CENTER_SELECTION_ITEM.sameAs(customerId)) {
			meetingDates.add(NA_MEETING_DATE);
			return meetingDates;
		}

		if (ALL_CENTER_SELECTION_ITEM.sameAs(customerId)) {
			List<SelectionItem> officers = new ArrayList<SelectionItem>();
			if (ALL_LOAN_OFFICER_SELECTION_ITEM.sameAs(officerId)) {
				officers = reportsParameterService
						.getActiveLoanOfficersUnderOffice(branchId);
			}
			else {
				officers = CollectionUtils.asList(new SelectionItem(officerId,
						null));
			}
			for (SelectionItem officer : officers) {
				List<SelectionItem> customers = new ArrayList<SelectionItem>();
				customers = reportsParameterService.getActiveCentersUnderUser(
						branchId, officer.getId());
				for (SelectionItem customer : customers) {
					List<DateSelectionItem> meetingDatesForCustomer = reportsParameterService
							.getMeetingDates(branchId, officer.getId(),
									convertShortToInteger(customer.getId()),
									today());
					if (meetingDatesForCustomer != null)
						meetingDates.addAll(meetingDatesForCustomer);
				}
			}
		}
		else {
			if (ALL_LOAN_OFFICER_SELECTION_ITEM.sameAs(officerId)) {
				CustomerBO customer = customerBusinessService
						.getCustomer(customerId);
				officerId = customer.getPersonnel().getPersonnelId();
			}
			List<DateSelectionItem> meetingDatesForOfficerBranchCustomer = reportsParameterService
					.getMeetingDates(branchId, officerId, customerId, today());
			if (meetingDatesForOfficerBranchCustomer != null)
				meetingDates.addAll(meetingDatesForOfficerBranchCustomer);
		}
		if (meetingDates.isEmpty())
			meetingDates.add(NA_MEETING_DATE);
		return meetingDates;
	}

	java.sql.Date today() {
		return DateUtils.sqlToday();
	}

	public List<CustomerBO> getApplicableCustomers(Short branchId,
			Short officerId, Integer customerId) throws ServiceException {
		List<CustomerBO> activeCenters = new ArrayList<CustomerBO>();
		if (ALL_CENTER_SELECTION_ITEM.sameAs(customerId)) {
			List<PersonnelBO> activeLoanOfficers = new ArrayList<PersonnelBO>();
			if (ALL_LOAN_OFFICER_SELECTION_ITEM.sameAs(officerId)) {
				activeLoanOfficers.addAll(personnelBusinessService
						.getActiveLoanOfficersUnderOffice(branchId));
			}
			else {
				activeLoanOfficers.add(personnelBusinessService
						.getPersonnel(officerId));
			}
			for (PersonnelBO loanOfficer : activeLoanOfficers) {
				activeCenters.addAll(customerBusinessService
						.getActiveCentersUnderUser(loanOfficer));
			}
			return activeCenters;
		}
		activeCenters.add(customerBusinessService.getCustomer(customerId));
		return activeCenters;
	}

	public void invalidate() {
		reportsParameterService.invalidate();
	}
}
