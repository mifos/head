package org.mifos.application.customer.struts.actionforms;

import junit.framework.TestCase;

import org.mifos.application.customer.group.struts.actionforms.GroupCustActionForm;

public class CustomerActionFormTest extends TestCase {
	
	public void testNullInCustomerId() throws Exception {
		CustomerActionForm form = new GroupCustActionForm();
		form.setCustomerId(null);
		assertNull(form.getCustomerId());

		try {
			form.getCustomerIdAsInt();
			fail();
		}
		catch (NullPointerException expected) {
		}
	}

}
