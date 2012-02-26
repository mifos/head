package org.mifos.platform.rest.ui.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

/**
 * Intercept missing request parameter exception handler for REST methods
 */
public class RESTDefaultHandlerExceptionResolver extends AbstractHandlerExceptionResolver {

    public RESTDefaultHandlerExceptionResolver() {
        setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        try {
            if (ex instanceof MissingServletRequestParameterException) {
                return handleMissingServletRequestParameter((MissingServletRequestParameterException) ex, request,
                        response, handler);
            }
        } catch (Exception handlerException) {
            logger.warn("Handling of [" + ex.getClass().getName() + "] resulted in Exception", handlerException);
        }
        return null;
    }

    protected @ResponseBody ModelAndView handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
            HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (request.getRequestURI().endsWith(".json")) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("status", "error");
            modelAndView.addObject("cause", ex.getMessage());
            return modelAndView;
        }
        return null;
    }

}
