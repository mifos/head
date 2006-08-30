package org.mifos.framework.components.compression;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: Filters all requests that are not coming thru controller
 * servlet.
 */
public class CompressFilter implements Filter {

	/**
     * The filter configuration object we are associated with.  If this value
     * is null, this filter instance is not currently configured.
     */
    private FilterConfig config = null;

    /**
     * Minimal reasonable threshold
     */
    private int minThreshold = 128;


    /**
     * The threshold number to compress
     */
    protected int compressionThreshold;

    /**
     * Debug level for this filter
     */
    private int debug = 0;
    
    /**
     * Place this filter into service.
     *
     * @param filterConfig The filter configuration object
     */

    public void init(FilterConfig filterConfig) {
    	config = filterConfig;
        if (filterConfig != null) {
            String value = filterConfig.getInitParameter("debug");
            if (value!=null) {
                debug = Integer.parseInt(value);
            } else {
                debug = 0;
            }
            String str = filterConfig.getInitParameter("compressionThreshold");
            if (str!=null) {
                compressionThreshold = Integer.parseInt(str);
                if (compressionThreshold != 0 && compressionThreshold < minThreshold) {
                    if (debug > 0) {
                        System.out.println("compressionThreshold should be either 0 - no compression or >= " + minThreshold);
                        System.out.println("compressionThreshold set to " + minThreshold);
                    }
                    compressionThreshold = minThreshold;
                }
            } else {
                compressionThreshold = 0;
            }

        } else {
            compressionThreshold = 0;
        }
    }

	/**
	 * destroy
	 */
	public void destroy() {
		this.config = null;
	}

	/**
	 * doFilter
	 * 
	 * @param request
	 *            ServletRequest
	 * @param response
	 *            ServletResponse
	 * @param chain
	 *            FilterChain
	 * @throws IOException
	 * @throws ServletException
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		 if (debug > 0) {
	            System.out.println("@doFilter");
	        }

	        if (compressionThreshold == 0) {
	            if (debug > 0) {
	                System.out.println("doFilter gets called, but compressionTreshold is set to 0 - no compression");
	            }
	            chain.doFilter(request, response);
	            return;
	        }

	        if (response instanceof HttpServletResponse) {
	                CompressionServletResponseWrapper wrappedResponse =
	                    new CompressionServletResponseWrapper((HttpServletResponse)response);
	                wrappedResponse.setDebugLevel(debug);
	                wrappedResponse.setCompressionThreshold(compressionThreshold);
	                if (debug > 0) {
	                    System.out.println("doFilter gets called with compression");
	                }
	                try {
	                    chain.doFilter(request, wrappedResponse);
	                } finally {
	                    wrappedResponse.finishResponse();
	                }
	           return;
	        }
	    }

	    /**
	     * Set filter config
	     * This function is equivalent to init. Required by Weblogic 6.1
	     *
	     * @param filterConfig The filter configuration object
	     */
	    public void setFilterConfig(FilterConfig filterConfig) {
	        init(filterConfig);
	    }

	    /**
	     * Return filter config
	     * Required by Weblogic 6.1
	     */
	    public FilterConfig getFilterConfig() {
	        return config;
	    }
	    
}
