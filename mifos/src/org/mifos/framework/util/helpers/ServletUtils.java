package org.mifos.framework.util.helpers;

import javax.servlet.http.HttpServletRequest;

public class ServletUtils {

	public static String getParameter(HttpServletRequest request,
			String paramName) {
		String parameter = request.getParameter(paramName);
		return parameter != null ? parameter : request.getAttribute(parameter)
				.toString();
	}

}
