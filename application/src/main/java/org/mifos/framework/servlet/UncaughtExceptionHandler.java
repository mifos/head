/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.framework.servlet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.rest.approval.service.RESTCallInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.webflow.execution.repository.FlowExecutionRestorationFailureException;
import org.mifos.reports.pentaho.util.JNDIException;

public class UncaughtExceptionHandler extends SimpleMappingExceptionResolver {

    private static final Logger logger = LoggerFactory.getLogger(UncaughtExceptionHandler.class);

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request,  HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView modelAndView = checkForAccessDenied(ex, request);
        if (modelAndView == null) {
        	modelAndView = checkForPageJndiException(ex, request);
    	}
        if ( modelAndView == null ){
        	modelAndView = checkForPageExpiredException(ex, request);
        }

        if (request.getRequestURI().endsWith("json")) {
            if (modelAndView == null && ex instanceof RESTCallInterruptException) {
                // should move to explicit @ExceptionHandler(RESTCallInterruptException) controller method
                modelAndView = new ModelAndView();
                modelAndView.addObject("status", "interrupt");
                modelAndView.addObject("approvalId", ((RESTCallInterruptException) ex).getApprovalId());
                modelAndView.addObject("cause", "The call has been interrupt for approval");
                return modelAndView;
            }

            if (modelAndView == null || ex instanceof AccessDeniedException ) {
                // should move to explicit @ExceptionHandler(Exception) controller method
                modelAndView = new ModelAndView();
                modelAndView.addObject("status", "error");
                modelAndView.addObject("cause", ex.getMessage());
                logger.error("REST API exception : URI '" + request.getRequestURI() + "'", ex);
                return modelAndView;
            }
        }

        if (modelAndView == null) {
            modelAndView = super.doResolveException(request, response, handler, ex);
        }

        if (modelAndView != null && !"HEAD".equals(request.getMethod())) {
            String requestUri = request.getRequestURI();
            logger.error("Uncaught exception while accessing '" + requestUri + "'", ex);

            modelAndView.addObject("uncaughtException", ex);
            modelAndView.addObject("requestUri", requestUri);
            if (ex != null) {
                Writer result = new StringWriter();
                ex.printStackTrace(new PrintWriter(result));
                modelAndView.addObject("stackString", result.toString());
            }
        }

        return modelAndView;
    }

    private ModelAndView checkForAccessDenied(Exception ex, HttpServletRequest request) {
        if (ex instanceof AccessDeniedException) {
            ModelAndView modelAndView = null;
            String viewName = determineViewName(ex, request);
            if(viewName != null) {
                modelAndView = getModelAndView(viewName, ex, request);
            }
            return modelAndView;
        }

        if (ex.getCause() != null && ex.getCause() instanceof Exception) {
            return checkForAccessDenied((Exception) ex.getCause(), request);
        }

        return null;
    }

    private ModelAndView checkForPageExpiredException(Exception ex, HttpServletRequest request){
    	if ( ex instanceof PageExpiredException || ex instanceof FlowExecutionRestorationFailureException){
    		ModelAndView modelAndView = null;
    		String viewName = determineViewName(ex, request);
    		if (viewName != null){
    			modelAndView = getModelAndView(viewName, ex, request);
    		}
    		return modelAndView;
    	}

        if (ex.getCause() != null && ex.getCause() instanceof Exception) {
            return checkForPageExpiredException((Exception) ex.getCause(), request);
        }

    	return null;
    }
    
    private ModelAndView checkForPageJndiException(Exception ex, HttpServletRequest request) {
    	if (ex instanceof JNDIException) {
    		ModelAndView modelAndView = null;
    		String viewName = determineViewName(ex, request);
    		if (viewName != null){
    			modelAndView = getModelAndView(viewName, ex, request);
    		}
    		return modelAndView;
    	}
    	return null;
    }

}
