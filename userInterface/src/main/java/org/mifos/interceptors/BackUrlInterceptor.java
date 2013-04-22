package org.mifos.interceptors;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mifos.framework.util.helpers.Constants;
import org.mifos.ui.core.controller.BreadCrumbsLinks;
import org.mifos.ui.core.controller.BreadcrumbBuilder;
import org.mifos.ui.core.controller.util.helpers.UrlHelper;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@SessionAttributes({ "accessDeniedBreadcrumbs", "previousPageUrl", "configureDwDatabase", "accountingActivationStatus" })
public class BackUrlInterceptor extends HandlerInterceptorAdapter {

    protected List<BreadCrumbsLinks> breadcrumbs = new LinkedList<BreadCrumbsLinks>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String errorPage = request.getRequestURI().replaceFirst(".+/", "");
        String accountingActivationStatus = null;
        try {
        	accountingActivationStatus = request.getSession().getAttribute("accountingActivationStatus").toString();
        } catch (Exception e){
        	accountingActivationStatus = "false";
        }
        String urlToBackPage = (String) request.getSession().getAttribute("previousPageUrl");
        
        if ("pageNotFound.ftl".equals(errorPage)) {
            breadcrumbs = new BreadcrumbBuilder().withLink("previousPage", urlToBackPage)
                    .withLink("pageNotFound", errorPage).build();
            request.setAttribute("accessDeniedBreadcrumbs", breadcrumbs);
        } else if ("viewPentahoReport.ftl".equals(errorPage)) {
            breadcrumbs = new BreadcrumbBuilder().withLink("previousPage", urlToBackPage)
                    .withLink("JNDIError", errorPage).build();
            request.setAttribute("accessDeniedBreadcrumbs", breadcrumbs);
        } else {
            breadcrumbs = new BreadcrumbBuilder().withLink("previousPage", urlToBackPage)
                    .withLink("accessDenied", errorPage).build(); 
            request.setAttribute("accessDeniedBreadcrumbs", breadcrumbs);
        }
        if (!"getMifosLogo.ftl?".equals(urlToBackPage)) {
            request.getSession().setAttribute(Constants.URLTOBACKPAGE, urlToBackPage);
        }
        request.setAttribute(Constants.URLTOBACKPAGE, urlToBackPage);
        request.setAttribute("accountingActivationStatus", Boolean.parseBoolean(accountingActivationStatus));
        request.getSession().setAttribute("previousPageUrl", UrlHelper.constructCurrentPageUrl(request));
        
        return true;
    }
}
