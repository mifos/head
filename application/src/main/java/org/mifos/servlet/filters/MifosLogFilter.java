/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.servlet.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Servlet Filter implementation class MifosLogFilter
 */
public class MifosLogFilter implements Filter {

	/**
	 * Default constructor.
	 */
	public MifosLogFilter() {

	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		if (request instanceof HttpServletRequest) {

			HttpServletRequest httpRequest = (HttpServletRequest) request;

			String newTxnName = makeTxnName(httpRequest.getRequestURI(),
					httpRequest.getParameter("input"),
					httpRequest.getParameter("method"));

			httpRequest.setAttribute("mifos-txnname", newTxnName);

			chain.doFilter(httpRequest, response);
			return;
		}
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
	}

	private String makeTxnName(String requestURI, String inputName, String methodName) {

		String separator = " ";
		StringBuilder txnName = new StringBuilder();

		txnName.append(requestURI);

		if (inputName != null) {
			txnName.append(separator);
			txnName.append(inputName);
		}
		if (methodName != null) {
			txnName.append(separator);
			txnName.append(methodName);
		}

		return txnName.toString();
	}
}
