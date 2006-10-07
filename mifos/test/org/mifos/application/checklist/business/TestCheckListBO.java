package org.mifos.application.checklist.business;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.checklist.util.resources.CheckListConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCheckListBO extends MifosTestCase {

	private CustomerBO center;

	private CustomerCheckListBO customerCheckList = null;

	private AccountCheckListBO accountCheckList = null;

	private UserContext userContext = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(center);
		customerCheckList = null;
		accountCheckList = null;
		super.tearDown();
	}

	public void testCreateCheckListForCustomer() throws Exception {
		center = createCenter("center");
		customerCheckList = TestObjectFactory.createCustomerChecklist(center
				.getCustomerLevel().getId(),
				center.getCustomerStatus().getId(),
				CheckListConstants.STATUS_ACTIVE);
		assertEquals("productchecklist", customerCheckList.getChecklistName());
		TestObjectFactory.deleteChecklist(customerCheckList);
		HibernateUtil.closeSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
	}

	public void testCreateCheckListForProduct() throws Exception {
		Session session = HibernateUtil.getSessionTL();
		Transaction tx = null;
		tx = session.beginTransaction();

		ProductTypeEntity productTypeEntity = (ProductTypeEntity) session.get(
				ProductTypeEntity.class, (short) 2);

		AccountStateEntity accountStateEntity = new AccountStateEntity(
				AccountState.SAVINGS_ACC_PARTIALAPPLICATION);

		userContext = TestObjectFactory.getUserContext();
		accountCheckList = new AccountCheckListBO(productTypeEntity,
				accountStateEntity, "new name", Short.valueOf("1"),
				getCheckListDetails(), Short.valueOf("1"), userContext.getId());

		accountCheckList.save();
		assertEquals("new name", accountCheckList.getChecklistName());

		assertEquals(accountCheckList.getChecklistDetails().size(), 3);
		TestObjectFactory.deleteChecklist(accountCheckList);
		tx.commit();
		HibernateUtil.closeSession();

	}

	private CenterBO createCenter(String name) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createCenter(name, Short.valueOf("13"), "1.4",
				meeting, new Date(System.currentTimeMillis()));
	}

	private List<String> getCheckListDetails() {
		List<String> details = new ArrayList();
		details.add("new detail1");
		details.add("new detail2");
		details.add("new detail3");
		return details;
	}
}
