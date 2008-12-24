package org.mifos.application.customer.client.struts.actionforms;

import junit.framework.TestCase;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.customer.client.util.helpers.ClientConstants;

public class ClientCustActionFormTest extends TestCase {
	
	public void testGoodDate() throws Exception {
		ClientCustActionForm form = new ClientCustActionForm();
		ActionErrors errors = new ActionErrors();
		form.setDateOfBirthDD("6");
		form.setDateOfBirthMM("12");
		form.setDateOfBirthYY("1950");
		form.validateDateOfBirth(null, errors);
		assertEquals(0, errors.size());
	}

	public void testFutureDate() throws Exception {
		ClientCustActionForm form = new ClientCustActionForm();
		ActionErrors errors = new ActionErrors();
		form.setDateOfBirthDD("6");
		form.setDateOfBirthMM("12");
		form.setDateOfBirthYY("2108");
		form.validateDateOfBirth(null, errors);
		assertEquals(1, errors.size());
		ActionMessage message = (ActionMessage) errors.get().next();
		assertEquals(ClientConstants.FUTURE_DOB_EXCEPTION, message.getKey());
	}
	
	public void testInvalidDate() throws Exception {
		ClientCustActionForm form = new ClientCustActionForm();
		ActionErrors errors = new ActionErrors();
		form.setDateOfBirthDD("2");
		form.setDateOfBirthMM("20");
		form.setDateOfBirthYY("1980");
		form.validateDateOfBirth(null, errors);
		assertEquals(1, errors.size());
		ActionMessage message = (ActionMessage) errors.get().next();
		assertEquals(ClientConstants.INVALID_DOB_EXCEPTION, message.getKey());
		
	}

}
