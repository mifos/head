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
