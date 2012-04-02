package org.mifos.ui.core.controller.util.helpers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.UrlPathHelper;

public class UrlHelper {
    
    private final static UrlPathHelper urlPathHelper = new UrlPathHelper();

    public static String constructCurrentPageEncodedUrl(HttpServletRequest request){
        String originatingServletPath = urlPathHelper.getOriginatingServletPath(request);
        String originatingQueryString = urlPathHelper.getOriginatingQueryString(request);
        if ( originatingQueryString == null ){
            originatingQueryString = "";
        }
        StringBuilder url = new StringBuilder(originatingServletPath).append("?").append(originatingQueryString).deleteCharAt(originatingServletPath.indexOf('/'));
        String encodedUrl;
        try {
            encodedUrl = URLEncoder.encode(url.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            encodedUrl = URLEncoder.encode(url.toString());
        }
        
        return removeSitePreferenceParameterFromUrl(encodedUrl);
    }
    
    public static String constructCurrentPageUrl(HttpServletRequest request){
        String originatingServletPath = urlPathHelper.getOriginatingServletPath(request);
        String originatingQueryString = urlPathHelper.getOriginatingQueryString(request);
        if ( originatingQueryString == null ){
            originatingQueryString = "";
        }
        StringBuilder url = new StringBuilder(originatingServletPath).append("?").append(originatingQueryString).deleteCharAt(originatingServletPath.indexOf('/'));

        return removeSitePreferenceParameterFromUrl(url.toString());
    }
    
    private static String removeSitePreferenceParameterFromUrl(String url){
        return url.replaceFirst("&site_preference=normal", "").replaceFirst("&site_preference=mobile", "");
    }
    
}
