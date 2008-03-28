package org.mifos.application.fees.struts.actionforms;

import org.mifos.framework.MifosTestCase;

/**
 * User: tomb
 * Date: Mar 27, 2008
 * Time: 11:49:12 PM
 */
public class TestFeeActionForm extends MifosTestCase {
    public void testIsAmountValidWithInvalidString() {
        FeeActionForm form = new FeeActionForm();
        form.setAmount("aaa");
        assertFalse(form.isAmountValid());
    }
}
