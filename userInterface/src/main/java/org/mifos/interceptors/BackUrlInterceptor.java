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

@SessionAttributes("accessDeniedBreadcrumbs")
public class BackUrlInterceptor extends HandlerInterceptorAdapter {

    protected List<BreadCrumbsLinks> breadcrumbs = new LinkedList<BreadCrumbsLinks>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String urlToBackPage = "";
        try {
            urlToBackPage = request.getHeader("Referer").replaceFirst(".+/", "");
            breadcrumbs = new BreadcrumbBuilder().withLink("previousPage", urlToBackPage)
                    .withLink("accessDenied", "accessDenied.ftl").build();
            request.setAttribute("accessDeniedBreadcrumbs", breadcrumbs);
        } catch (RuntimeException e) {
            urlToBackPage = "AdminAction.do?method=load";
        }
        request.setAttribute(Constants.URLTOBACKPAGE, urlToBackPage);
        return true;
    }
}
