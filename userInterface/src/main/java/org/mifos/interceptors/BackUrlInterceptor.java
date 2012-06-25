package org.mifos.interceptors;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.ui.core.controller.BreadCrumbsLinks;
import org.mifos.ui.core.controller.BreadcrumbBuilder;
import org.mifos.ui.core.controller.util.helpers.UrlHelper;

@SessionAttributes({ "accessDeniedBreadcrumbs", "previousPageUrl" })
public class BackUrlInterceptor extends HandlerInterceptorAdapter {

    protected List<BreadCrumbsLinks> breadcrumbs = new LinkedList<BreadCrumbsLinks>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String currentPage = request.getRequestURI().replaceFirst(".+/", "");
        String urlToBackPage = (String) request.getSession().getAttribute("previousPageUrl");
        if ("pageNotFound.ftl".equals(currentPage)) {
            breadcrumbs = new BreadcrumbBuilder().withLink("previousPage", urlToBackPage)
                    .withLink("pageNotFound", currentPage).build();
            request.setAttribute("accessDeniedBreadcrumbs", breadcrumbs);
        } else if ("accessDenied.ftl".equals(currentPage)) {
            breadcrumbs = new BreadcrumbBuilder().withLink("previousPage", urlToBackPage)
                    .withLink("accessDenied", currentPage).build(); 
            request.setAttribute("accessDeniedBreadcrumbs", breadcrumbs);
        }
        
        request.setAttribute(Constants.URLTOBACKPAGE, urlToBackPage);
        request.getSession().setAttribute("previousPageUrl", UrlHelper.constructCurrentPageUrl(request));
        return true;
    }
}
