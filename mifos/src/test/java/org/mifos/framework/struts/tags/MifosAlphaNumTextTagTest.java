package org.mifos.framework.struts.tags;

import static org.mifos.framework.TestUtils.assertWellFormedFragment;

import org.dom4j.DocumentException;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class MifosAlphaNumTextTagTest extends MifosTestCase {
	
	public MifosAlphaNumTextTagTest() throws SystemException, ApplicationException {
        super();
    }


    public void testRenderFieldHiddenMandatory() throws DocumentException{
		MifosAlphaNumTextTag mifosAlphaNumTextTag = new MifosAlphaNumTextTag();
		mifosAlphaNumTextTag.setKeyhm("test1");
		mifosAlphaNumTextTag.setPropertyExpr("test2");
		StringBuffer inputsForhidden=new StringBuffer();
		inputsForhidden.append("<input type=\"hidden\" name=\""+"test1"+"\" value=\""+"test2"+"\" />");
		assertWellFormedFragment(mifosAlphaNumTextTag.renderFieldHiddenMandatory());
		assertEquals(inputsForhidden.toString(), mifosAlphaNumTextTag.renderFieldHiddenMandatory());
	}
	
	
	public void testRenderDoStartTag() throws DocumentException{
		MifosAlphaNumTextTag mifosAlphaNumTextTag = new MifosAlphaNumTextTag();
		StringBuilder inputsForhidden=new StringBuilder();
		inputsForhidden.append("<script src="+"\"pages/framework/js/func.js\""+">");
		inputsForhidden.append("</script>");
		inputsForhidden.append("<script src="+"\"pages/framework/js/func_test.js\""+">");
		inputsForhidden.append("</script>");
		assertWellFormedFragment(mifosAlphaNumTextTag.renderDoStartTag("test"));
		assertEquals(inputsForhidden.toString(),mifosAlphaNumTextTag.renderDoStartTag("test"));
	}
}
