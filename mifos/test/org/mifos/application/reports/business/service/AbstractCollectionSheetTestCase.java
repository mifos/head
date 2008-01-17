package org.mifos.application.reports.business.service;

import static org.mifos.application.reports.ui.SelectionItem.ALL_CENTER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.ALL_LOAN_OFFICER_SELECTION_ITEM;
import static org.mifos.framework.util.helpers.NumberUtils.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.mifos.application.collectionsheet.business.CollSheetCustBO;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelFixture;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.NumberUtils;

public class AbstractCollectionSheetTestCase extends MifosTestCase {

	protected static final Integer ANY_ID = new Integer("1");
	protected static final Short ANY_SHORT_ID = NumberUtils
			.convertIntegerToShort(ANY_ID);
	protected static final Integer BRANCH_ID = Integer.valueOf(400);
	protected static final Short BRANCH_SHORT_ID = NumberUtils
			.convertIntegerToShort(BRANCH_ID);
	protected static final Integer CENTER_ID = Integer.valueOf(500);
	protected static final Integer LOAN_OFFICER_ID = Integer.valueOf(600);
	protected static final Short LOAN_OFFICER_SHORT_ID = Short
			.valueOf(LOAN_OFFICER_ID.shortValue());
	protected static final int GROUP_ID = Integer.valueOf(200);
	protected static final Integer ANOTHER_GROUP_ID = Integer.valueOf(201);
	protected static final Date TODAYS_DATE = DateUtils.currentDateAsSqlDate();
	protected static final int MAX_COUNT = 10;
	protected static final Integer ALL_CENTER_ID = convertShortToInteger(ALL_CENTER_SELECTION_ITEM
			.getId());
	protected static final Short ALL_LOAN_OFFICER_SHORT_ID = ALL_LOAN_OFFICER_SELECTION_ITEM
			.getId();
	protected static final Integer ALL_LOAN_OFFICER_ID = NumberUtils
			.convertShortToInteger(ALL_LOAN_OFFICER_SHORT_ID);
	protected static final PersonnelBO LOAN_OFFICER = PersonnelFixture
			.createLoanOfficer(LOAN_OFFICER_SHORT_ID);
	protected CollectionSheetService collectionSheetService;
	protected CollSheetCustBO center;
	protected CollSheetCustBO group;

	protected Integer s2i(Short s) {
		return NumberUtils.convertShortToInteger(s);
	}

	protected Short i2s(Integer s) {
		return NumberUtils.convertIntegerToShort(s);
	}

	protected List<CollSheetCustBO> generateClientCollectionSheets(
			int idStartFrom, CollSheetCustBO parentGroup, Short loanOfficerId) {
		List<CollSheetCustBO> generatedCollectionSheets = new ArrayList<CollSheetCustBO>();
		for (int i = idStartFrom; i < idStartFrom + 2; i++) {
			CollSheetCustBO customer = generateCollectionSheet(i,
					loanOfficerId, CustomerLevel.CLIENT);
			customer.setParentCustomerId(parentGroup.getCustId());
			generatedCollectionSheets.add(customer);
		}
		return generatedCollectionSheets;
	}

	protected CollSheetCustBO generateCollectionSheet(int custId,
			Short loanOfficerId, CustomerLevel customerLevel) {
		CollSheetCustBO collSheetCustBO = new CollSheetCustBO();
		collSheetCustBO.populateInstanceForTest(Integer.valueOf(custId),
				"Sample Individual Customer", customerLevel.getValue(),
				ANY_SHORT_ID, "", loanOfficerId);
		return collSheetCustBO;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		center = new CollSheetCustBO();
		center.populateInstanceForTest(CENTER_ID, "Sample Center",
				CustomerLevel.CENTER.getValue(), ANY_SHORT_ID, "",
				LOAN_OFFICER_SHORT_ID);
		group = new CollSheetCustBO();
		group.populateInstanceForTest(GROUP_ID, "Sample Group",
				CustomerLevel.GROUP.getValue(), ANY_SHORT_ID, "",
				LOAN_OFFICER_SHORT_ID);
	}
}
