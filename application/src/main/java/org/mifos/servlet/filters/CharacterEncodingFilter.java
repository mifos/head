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

package org.mifos.servlet.filters;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang.CharEncoding;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Servlet Filter which defaults the response (and request form parameter) encoding to UTF-8.
 * 
 * @see http://mifosforge.jira.com/browse/MIFOS-4789 why this was needed
 * @see https://jira.springsource.org/browse/SPR-8019 re. why Spring's CharacterEncodingFilter couldn't be used
 * 
 * @author Michael Vorburger
 */
public class CharacterEncodingFilter extends GenericFilterBean /* NOT OncePerRequestFilter !!! */ {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding(CharEncoding.UTF_8);
        response.setCharacterEncoding(CharEncoding.UTF_8);
        if (!CharEncoding.UTF_8.equals(response.getCharacterEncoding())) {
            // MIFOS-5435 - the character encoding was not set because the connection is in including state
            try {
                Method getResponse = response.getClass().getMethod("getResponse");
                ServletResponse servletResponse = (ServletResponse) getResponse.invoke(response);
                getResponse = servletResponse.getClass().getMethod("getResponse");
                ServletResponse jettyResponse = (ServletResponse) getResponse.invoke(servletResponse);
                Field _characterEncoding = jettyResponse.getClass().getDeclaredField("_characterEncoding");
                _characterEncoding.setAccessible(true);
                _characterEncoding.set(jettyResponse, CharEncoding.UTF_8);
            } catch (NoSuchFieldException e) {
                logger.debug(e);
            } catch (IllegalAccessException e) {
                logger.debug(e);
            } catch (NoSuchMethodException e) {
                logger.debug(e);
            } catch (InvocationTargetException e) {
                logger.debug(e);
            }
        }
        chain.doFilter(request, response);
    }

}
