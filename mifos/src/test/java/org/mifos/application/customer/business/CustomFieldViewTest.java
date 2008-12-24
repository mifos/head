package org.mifos.application.customer.business;

import junit.framework.TestCase;

import org.mifos.application.master.business.CustomFieldView;

public class CustomFieldViewTest extends TestCase {
	
	public void testEmpty() throws Exception {
		// The main point here is that we shouldn't get
		// NullPointerException for these operations.

		CustomFieldView view = new CustomFieldView();
		assertEquals("org.mifos.application.master.business.CustomFieldView@0", 
			view.toString());
		view.hashCode();

		CustomFieldView view2 = new CustomFieldView();
		assertTrue(view.equals(view2));
	}

}
