package org.mifos.application.fees.struts.actionforms;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class TestFeeActionForm extends MifosTestCase {
    public TestFeeActionForm() throws SystemException, ApplicationException {
        super();
    }

    public void testIsAmountValidWithInvalidString() {
        FeeActionForm form = new FeeActionForm();
        form.setAmount("aaa");
        assertFalse(form.isAmountValid());
    }
}
