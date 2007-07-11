package org.mifos.application.configuration.struts.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.struts.taglib.TagUtils;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.XmlBuilder;
import org.mifos.framework.util.helpers.Constants;

public class CustomFieldCategoryListTag extends BodyTagSupport {
	private String actionName;

	private String methodName;
	
	private String flowKey;

	public CustomFieldCategoryListTag() {
	}

	public CustomFieldCategoryListTag(String action, String method, String flow) {
		actionName = action;
		methodName = method;
		flowKey = flow;
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			UserContext userContext = (UserContext) pageContext.getSession()
			.getAttribute(Constants.USERCONTEXT);

			TagUtils.getInstance().write(pageContext, getCustomFieldCategoryList(userContext));
			
		} catch (Exception e) {
			/**
			    This turns into a (rather ugly) error 500.
			    TODO: make it more reasonable.
			 */
			throw new JspException(e);
		}
		return EVAL_PAGE;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	String getCustomFieldCategoryList(UserContext userContext) throws Exception {
		XmlBuilder html = new XmlBuilder();
		html.startTag(
				"table", "width", "95%", "border", "0",
				"cellspacing", "0", "cellpadding", "0");

		MasterPersistence masterPersistence = new MasterPersistence();
		for (String category : masterPersistence.getCustomFieldCategories()) {
			html.append(getCategoryRow(category));
		}			
			
		html.endTag("table");

		return html.getOutput();
	}
/*	
	<table width="90%" border="0" cellspacing="0" cellpadding="0">
	<tr class="fontnormal">
		<td>
			<img src="pages/framework/images/bullet_circle.gif"
				width="9" height="11" alt="">
		</td>
		<td>
			<a href="VIewCustomFields.htm">Personnel</a>
		</td>
	</tr>
*/	

	
	XmlBuilder getCategoryRow(String categoryName) {
		String urlencodedCategoryName = replaceSpaces(categoryName);
		XmlBuilder html = new XmlBuilder();
		String url = (actionName + "?method=" + methodName
						+ "&category=" + urlencodedCategoryName
						+ "&currentFlowKey=" + flowKey);
		html.startTag("tr", "class", "fontnormal");		
			bullet(html);
			html.startTag("td");
				html.startTag("a", "href", url);
				html.text(categoryName);
				html.endTag("a");
			html.endTag("td");
		html.endTag("tr");

		return html;
	}

	public String replaceSpaces(String name) {
		return name.trim().replaceAll(" ", "%20");
	}

	private void bullet(XmlBuilder html) {
		html.startTag("td", "width", "1%");
		html.singleTag("img", 
				"src", "pages/framework/images/bullet_circle.gif",
				"width", "9", "height", "11");
		html.endTag("td");
	}

	public String getFlowKey() {
		return flowKey;
	}

	public void setFlowKey(String flowKey) {
		this.flowKey = flowKey;
	}
}
