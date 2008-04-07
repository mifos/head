package org.mifos.framework.util.helpers;

import javax.servlet.http.HttpServletRequest;

public class ServletUtils {

	public static String getParameter(HttpServletRequest request,
			String paramName) {
		return request.getParameter(paramName) != null ? request
				.getParameter(paramName)
				: request.getAttribute(paramName) != null ? request
						.getAttribute(paramName).toString() : null;
	}

}
