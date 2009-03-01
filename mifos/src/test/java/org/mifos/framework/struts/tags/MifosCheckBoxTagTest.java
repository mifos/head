package org.mifos.framework.struts.tags;

import org.dom4j.DocumentException;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

import static org.mifos.framework.TestUtils.assertWellFormedFragment;
public class MifosCheckBoxTagTest extends MifosTestCase {
	public MifosCheckBoxTagTest() throws SystemException, ApplicationException {
        super();
    }

    public void testRenderInputsForhidden() throws DocumentException{
		MifosCheckBoxTag mifosCheckBoxTag = new MifosCheckBoxTag();
		mifosCheckBoxTag.setKeyhm("test1");
		mifosCheckBoxTag.setPropertyExpr("test2");
		StringBuffer inputsForhidden=new StringBuffer();
		inputsForhidden.append("<input type=\"hidden\" name=\""+"test1"+"\" value=\""+"test2"+"\" />");
		assertEquals(inputsForhidden.toString(), mifosCheckBoxTag.renderInputsForhidden());
		assertWellFormedFragment(mifosCheckBoxTag.renderInputsForhidden());
	}
}
