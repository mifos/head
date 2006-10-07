package org.mifos.application.checklist.business.service;

import java.util.List;

import org.mifos.application.checklist.util.helpers.CheckListMasterView;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCheckListBusinessService extends MifosTestCase {

	private UserContext userContext = null;
	private CheckListBusinessService checkListBusinessService = null;
	
	@Override
	protected void setUp() throws Exception{
		userContext = TestObjectFactory.getUserContext();
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception{
		checkListBusinessService = null;
		userContext = null;
		super.tearDown();
	}
	
	public void testGetCheckListMasterData() throws Exception{
		checkListBusinessService = new CheckListBusinessService();
		List<CheckListMasterView> checkListMasterDataView = checkListBusinessService.getCheckListMasterData(userContext);
		assertNotNull(checkListMasterDataView);
		assertEquals(checkListMasterDataView.size(),5);
	}
}

