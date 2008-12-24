package org.mifos.framework.struts.tags;

import javax.servlet.jsp.JspException;

import org.dom4j.DocumentException;
import org.mifos.framework.MifosTestCase;
import static org.mifos.framework.TestUtils.assertWellFormedFragment;

public class MifosSelectTagTest extends MifosTestCase  {
	public void testRenderDoStartTag() throws JspException, DocumentException{
		MifosSelectTag mifosSelectTag = new MifosSelectTag();
		StringBuilder resultsDoStartTag = new StringBuilder();
		mifosSelectTag.setKeyhm("test1");
		mifosSelectTag.setPropertyExpr("test2");
		resultsDoStartTag.append("<input type=\"hidden\" name=\""+"test1"+"\" value=\""+"test2"+"\" />");
		assertEquals(resultsDoStartTag.toString(), mifosSelectTag.renderDoStartTag());
		assertWellFormedFragment(mifosSelectTag.renderDoStartTag());
	}
	public void testRenderDoEndTag() throws JspException, DocumentException{
		MifosSelectTag mifosSelectTag = new MifosSelectTag();
		StringBuilder resultsDoEndTag = new StringBuilder();
		resultsDoEndTag.append("<option value=\"\">");
		resultsDoEndTag.append("test");
		resultsDoEndTag.append("</option>");
		resultsDoEndTag.append("</select>");
		assertEquals(resultsDoEndTag.toString(), mifosSelectTag.renderDoEndTag("test"));
	}
}

