package org.mifos.ui.core.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UncaughtExceptionController {

    private static final Logger logger = LoggerFactory.getLogger(UncaughtExceptionController.class);

    @RequestMapping("/uncaughtException.ftl")
    public ModelAndView handleException(HttpServletRequest request) {

        // TODO remove test code
        if (request.getParameter("test") != null) {
            Throwable test = new IllegalArgumentException("Test!");
            request.setAttribute("javax.servlet.error.exception", test);
        }
        // end test code

        // http://java.sun.com/developer/technicalArticles/Servlets/servletapi2.3
        Object obj = request.getAttribute("javax.servlet.error.exception");
        Throwable cause = null;
        if (obj != null) {
            cause = (Throwable) obj;
        }

        obj = request.getAttribute("javax.servlet.error.request_uri");
        String requestUri = null;
        if (obj != null) {
            requestUri = obj.toString();
        }

        logger.error("Uncaught exception while accessing '" + requestUri + "'", cause);

        ModelAndView mm = new ModelAndView();
        mm.addObject("uncaughtException", cause);
        mm.addObject("requestUri", requestUri);
        if (cause != null) {
            Writer result = new StringWriter();
            cause.printStackTrace(new PrintWriter(result));
            mm.addObject("stackString", result.toString());
        }
        return mm;
    }
}
