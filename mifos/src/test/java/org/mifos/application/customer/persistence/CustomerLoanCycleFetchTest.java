package org.mifos.application.customer.persistence;

import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class CustomerLoanCycleFetchTest extends MifosTestCase {
	
	public CustomerLoanCycleFetchTest() throws SystemException, ApplicationException {
        super();
    }

    public void testFetchLoanCountersForGroupQueryIsValid() throws Exception {
		try {
			new CustomerPersistence().fetchLoanCycleCounter(new GroupBO(){
				@Override
				public Integer getCustomerId() {
					return 1;
				}
				
				@Override
				public boolean isGroup() {
					return true;
				}
			});
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception fetching customer loan counters");
		}
	}
	
	public void testFetchLoanCountersForClientQueryIsValid() throws Exception {
		try {
			new CustomerPersistence().fetchLoanCycleCounter(new ClientBO(){
				@Override
				public Integer getCustomerId() {
					return 1;
				}
				
				@Override
				public boolean isGroup() {
					return false;
				}
				
				@Override
				public boolean isClient() {
					return true;
				}
			});
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception fetching customer loan counters");
		}
	}
}
