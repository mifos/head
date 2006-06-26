package org.mifos.framework.struts.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.struts.taglib.TagUtils;
import org.mifos.framework.security.util.resources.SecurityConstants;

public class SecurityParam extends BodyTagSupport {
	
	private String property;

	@Override
	public int doEndTag() throws JspException {
		

		pageContext.getSession().setAttribute(SecurityConstants.SECURITY_PARAM,property);
	        return EVAL_PAGE;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}


}
