package org.mifos.application.productdefinition.struts.actionforms;

import junit.framework.TestCase;

import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.framework.components.logger.TestLogger;

public class LoanPrdActionFormTest extends TestCase {
	
	public void testApplicableMaster() throws Exception {
		LoanPrdActionForm form = new LoanPrdActionForm(new TestLogger());
		form.setPrdApplicableMaster(
			"" + ApplicableTo.CLIENTS.getValue());
		assertEquals(ApplicableTo.CLIENTS, 
			form.getPrdApplicableMasterEnum());
	}

	public void testSetFromEnum() throws Exception {
		LoanPrdActionForm form = new LoanPrdActionForm(new TestLogger());
		form.setPrdApplicableMaster(ApplicableTo.ALLCUSTOMERS);
		assertEquals(ApplicableTo.ALLCUSTOMERS, 
			form.getPrdApplicableMasterEnum());
	}

}
