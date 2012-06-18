package org.mifos.interceptors;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.ui.core.controller.BreadCrumbsLinks;

@SessionAttributes("accessDeniedBreadcrumbs")
public class BackUrlInterceptor extends HandlerInterceptorAdapter {
    
    protected List<BreadCrumbsLinks> breadcrumbs = new LinkedList<BreadCrumbsLinks>();
    
    @Override
	public void postHandle(HttpServletRequest request,
	        HttpServletResponse response, Object handler,ModelAndView modelAndView) {
        try {
            if (!modelAndView.getViewName().equals("login")) {
                breadcrumbs = (List<BreadCrumbsLinks>) request.getSession().getAttribute("accessDeniedBreadcrumbs");
                if (modelAndView.getViewName().equals("home") || modelAndView.getViewName().equals("clientsAndAccounts")) {
                    breadcrumbs = new LinkedList<BreadCrumbsLinks>();
                }
                if (exist(modelAndView.getViewName())) {
                    while (!breadcrumbs.get(breadcrumbs.size() - 1).getMessage().equals(modelAndView.getViewName())) {
                        breadcrumbs.remove(breadcrumbs.size() - 1);
                    }
                    breadcrumbs.remove(breadcrumbs.size() - 1);
                }
                BreadCrumbsLinks root = new BreadCrumbsLinks();
                root.setMessage(modelAndView.getViewName());
                root.setLink(request.getRequestURI());
                breadcrumbs.add(root);
                modelAndView.getModel().put("accessDeniedBreadcrumbs", breadcrumbs);
            }
        } catch (RuntimeException e) {
            breadcrumbs = new LinkedList<BreadCrumbsLinks>();
        }
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) {
        String urlToBackPage = "";
        try {
            urlToBackPage = request.getHeader("Referer").replaceFirst(".+/", "");
        } catch (Exception NullPointerException) {
            urlToBackPage = "";
        }
        request.setAttribute(Constants.URLTOBACKPAGE, urlToBackPage);
        return true;
    }
    
    public boolean exist(String existTitle) {
        boolean exist = false;
        for (BreadCrumbsLinks breadcrumb : breadcrumbs) {
            if (breadcrumb.getMessage().equals(existTitle)) {
                exist = true;
                break;
            }
        }
        return exist;
    }
}
