/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.reports.business.service;

import static org.mifos.framework.util.helpers.NumberUtils.convertIntegerToShort;
import static org.mifos.framework.util.helpers.NumberUtils.convertShortToInteger;
import static org.mifos.reports.ui.SelectionItem.ALL_CENTER_SELECTION_ITEM;
import static org.mifos.reports.ui.SelectionItem.ALL_LOAN_OFFICER_SELECTION_ITEM;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.mifos.application.collectionsheet.business.CollSheetCustBO;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficecFixture;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelFixture;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.NumberUtils;
import org.mifos.reports.ui.SelectionItem;

public class AbstractCollectionSheetIntegrationTestCase extends MifosIntegrationTestCase {

    public AbstractCollectionSheetIntegrationTestCase() throws Exception {
        super();
    }

    protected static final Integer ANY_ID = new Integer("1");
    protected static final Short ANY_SHORT_ID = NumberUtils.convertIntegerToShort(ANY_ID);
    protected static final Integer BRANCH_ID = Integer.valueOf(400);
    protected static final Short BRANCH_SHORT_ID = NumberUtils.convertIntegerToShort(BRANCH_ID);
    protected static final Integer CENTER_ID = Integer.valueOf(500);
    protected static final Integer LOAN_OFFICER_ID = Integer.valueOf(600);
    protected static final Short LOAN_OFFICER_SHORT_ID = Short.valueOf(LOAN_OFFICER_ID.shortValue());
    protected static final int GROUP_ID = Integer.valueOf(200);
    protected static final Integer ANOTHER_GROUP_ID = Integer.valueOf(201);
    protected static final Date TODAYS_DATE = DateUtils.currentDateAsSqlDate();
    protected static final int MAX_COUNT = 10;
    protected static final Integer ALL_CENTER_ID = ALL_CENTER_SELECTION_ITEM.getId();
    protected static final Integer ALL_LOAN_OFFICER_ID = ALL_LOAN_OFFICER_SELECTION_ITEM.getId();
    protected static PersonnelBO LOAN_OFFICER;
    protected CollectionSheetService collectionSheetService;
    protected CollSheetCustBO centerCollectionSheet;
    protected CollSheetCustBO groupCollectionSheet;

    protected static final Integer PERSONNEL_ANY_ID = Integer.valueOf(100);
    protected static final Short PERSONNEL_ANY_SHORT_ID = convertIntegerToShort(PERSONNEL_ANY_ID);

    protected List<OfficeBO> branchOffices;
    protected List<SelectionItem> branchOfficesSelectionItems;
    protected List<PersonnelBO> loanOfficers;
    protected List<SelectionItem> loanOfficersSelectionItems;
    protected List<CustomerBO> centers;
    protected List<SelectionItem> centerSelectionItems;
    protected static PersonnelBO ANY_PERSONNEL;
    protected PersonnelBO anyPersonnel;
    protected OfficeBO anyOffice;
    protected CenterBO center;
    protected static final OfficeBO OFFICE = OfficecFixture.createOffice(BRANCH_SHORT_ID);
    protected static final SelectionItem OFFICE_SELECTION_ITEM = new SelectionItem(convertShortToInteger(OFFICE
            .getOfficeId()), OFFICE.getOfficeName());

    protected Integer s2i(Short s) {
        return NumberUtils.convertShortToInteger(s);
    }

    protected Short i2s(Integer s) {
        return NumberUtils.convertIntegerToShort(s);
    }

    protected List<CollSheetCustBO> generateClientCollectionSheets(int idStartFrom, CollSheetCustBO parentGroup,
            Short loanOfficerId) {
        List<CollSheetCustBO> generatedCollectionSheets = new ArrayList<CollSheetCustBO>();
        for (int i = idStartFrom; i < idStartFrom + 2; i++) {
            CollSheetCustBO customer = generateCollectionSheet(i, loanOfficerId, CustomerLevel.CLIENT);
            customer.setParentCustomerId(parentGroup.getCustId());
            generatedCollectionSheets.add(customer);
        }
        return generatedCollectionSheets;
    }

    protected CollSheetCustBO generateCollectionSheet(int custId, Short loanOfficerId, CustomerLevel customerLevel) {
        CollSheetCustBO collSheetCustBO = new CollSheetCustBO(getCurrency());
        collSheetCustBO.populateInstanceForTest(Integer.valueOf(custId), "Sample Individual Customer", customerLevel
                .getValue(), ANY_SHORT_ID, "", loanOfficerId);
        return collSheetCustBO;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        LOAN_OFFICER = PersonnelFixture.createLoanOfficer(LOAN_OFFICER_SHORT_ID);
        ANY_PERSONNEL = PersonnelFixture.createNonLoanOfficer(PERSONNEL_ANY_SHORT_ID);
        centerCollectionSheet = new CollSheetCustBO(getCurrency());
        centerCollectionSheet.populateInstanceForTest(CENTER_ID, "Sample Center", CustomerLevel.CENTER.getValue(),
                ANY_SHORT_ID, "", LOAN_OFFICER_SHORT_ID);
        groupCollectionSheet = new CollSheetCustBO(getCurrency());
        groupCollectionSheet.populateInstanceForTest(GROUP_ID, "Sample Group", CustomerLevel.GROUP.getValue(),
                ANY_SHORT_ID, "", LOAN_OFFICER_SHORT_ID);
        branchOffices = new ArrayList<OfficeBO>();
        branchOfficesSelectionItems = new ArrayList<SelectionItem>();
        loanOfficers = new ArrayList<PersonnelBO>();
        loanOfficersSelectionItems = new ArrayList<SelectionItem>();
        centers = new ArrayList<CustomerBO>();
        centerSelectionItems = new ArrayList<SelectionItem>();
        for (int i = 0; i < MAX_COUNT; i++) {
            OfficeBO office = OfficecFixture.createOffice(String.valueOf(i) + ".");
            branchOffices.add(office);
            branchOfficesSelectionItems.add(new SelectionItem(convertShortToInteger(office.getOfficeId()), office
                    .getOfficeName()));
            PersonnelBO loanOfficer = PersonnelFixture.createPersonnel(new Short(String.valueOf(i)));
            loanOfficers.add(loanOfficer);
            loanOfficersSelectionItems.add(new SelectionItem(convertShortToInteger(loanOfficer.getPersonnelId()),
                    loanOfficer.getDisplayName()));

            Integer customerId = Integer.valueOf(i);

            CenterBO centerBO = new CenterBuilder().withName(customerId.toString()).withLoanOfficer(loanOfficer).build();

//            CenterBO centerBO = CenterBO.createInstanceForTest(customerId, new CustomerLevelEntity(CustomerLevel.CENTER),
//                    null, loanOfficer, customerId.toString());

            centers.add(centerBO);
            centerSelectionItems.add(new SelectionItem(centerBO.getCustomerId(), centerBO.getDisplayName()));
        }
        anyPersonnel = PersonnelFixture.createPersonnel(ANY_SHORT_ID);
        anyOffice = OfficecFixture.createOffice(ANY_SHORT_ID);

        center = new CenterBuilder().withName(CENTER_ID.toString()).withLoanOfficer(LOAN_OFFICER).build();
//        center = CenterBO.createInstanceForTest(CENTER_ID, new CustomerLevelEntity(CustomerLevel.CENTER),
//                null, LOAN_OFFICER, CENTER_ID.toString());
    }
}
