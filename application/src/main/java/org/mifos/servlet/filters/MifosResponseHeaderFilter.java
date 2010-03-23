package org.mifos.servlet.filters;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class MifosResponseHeaderFilter
 */
public class MifosResponseHeaderFilter implements Filter {
	FilterConfig fc;
	
    /**
     * Default constructor. 
     */
    public MifosResponseHeaderFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		this.fc = null;
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

	    HttpServletResponse response =
	      (HttpServletResponse) res;
	    // set the provided HTTP response parameters
	    for (Enumeration e=fc.getInitParameterNames();
	        e.hasMoreElements();) {
	      String headerName = (String)e.nextElement();
	      response.addHeader(headerName,
	                 fc.getInitParameter(headerName));
	    }
	    // pass the request/response on
	    chain.doFilter(req, response);	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		this.fc = fConfig;
	}

}
