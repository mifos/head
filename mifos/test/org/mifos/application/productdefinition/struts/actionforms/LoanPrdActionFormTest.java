package org.mifos.application.productdefinition.struts.actionforms;

import org.mifos.application.productdefinition.util.helpers.PrdApplicableMaster;
import org.mifos.framework.components.logger.TestLogger;

import junit.framework.TestCase;

public class LoanPrdActionFormTest extends TestCase {
	
	public void testApplicableMaster() throws Exception {
		LoanPrdActionForm form = new LoanPrdActionForm(new TestLogger());
		form.setPrdApplicableMaster(
			"" + PrdApplicableMaster.CLIENTS.getValue());
		assertEquals(PrdApplicableMaster.CLIENTS, 
			form.getPrdApplicableMasterEnum());
	}

	public void testSetFromEnum() throws Exception {
		LoanPrdActionForm form = new LoanPrdActionForm(new TestLogger());
		form.setPrdApplicableMaster(PrdApplicableMaster.ALLCUSTOMERS);
		assertEquals(PrdApplicableMaster.ALLCUSTOMERS, 
			form.getPrdApplicableMasterEnum());
	}

}
