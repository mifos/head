/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */

package org.mifos.ui.core.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.mifos.config.servicefacade.LogoServiceFacade;
import org.mifos.framework.util.ConfigurationLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"RV_RETURN_VALUE_IGNORED_BAD_PRACTICE", "SE_BAD_FIELD"})
public class UploadLogoController {
    
    public static final String LOGO_DIRECTORY = "logo";

	@Autowired
	private LogoServiceFacade logoServiceFacade;
	
	@Autowired
	private MessageSource messageSource;
	
	@ModelAttribute("breadcrumbs")
	public List<BreadCrumbsLinks> showBreadCrumbs() {
		return new AdminBreadcrumbBuilder().withLink("admin.uploadNewLogo", "uploadNewLogo.ftl").build();
	}
	
	@RequestMapping(value="/uploadNewLogo.ftl", method=RequestMethod.GET)
    public String showUploadNewLogoPage(Model model) {
		model.addAttribute(new LogoUpload());
        return "uploadNewLogo";
    }
	
    @RequestMapping(value="/uploadNewLogo.ftl", method=RequestMethod.POST)
    public String uploadNewLogo(@ModelAttribute LogoUpload logoUpload, BindingResult result, Model model, HttpServletRequest request) {
    	String[] availableContentTypes = {"image/png", "image/gif", "image/jpeg", "image/pjpeg"};
    	if (Arrays.asList(availableContentTypes).contains(logoUpload.getFile().getContentType())) {
    		try {
    			logoServiceFacade.uploadNewLogo(logoUpload.getFile());
    			model.addAttribute("success", true);
    		} catch (IOException e) {
    			result.addError(new ObjectError("logoUpload", messageSource.getMessage("admin.uploadLogo.ioexception", null, RequestContextUtils.getLocale(request))));
    		}
    	} else {
    		result.addError(new ObjectError("logoUpload", messageSource.getMessage("admin.uploadLogo.badType", null, RequestContextUtils.getLocale(request))));
    	}
    	return "uploadNewLogo";
    }
    
    @RequestMapping("/getMifosLogo")
    public ResponseEntity<byte[]> getMifosLogo() throws IOException {
        ConfigurationLocator configurationLocator = new ConfigurationLocator();
        Resource logo = configurationLocator.getUploadedMifosLogo();
        InputStream in = logo.getInputStream();
        byte[] logoContent = IOUtils.toByteArray(in);
        in.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<byte[]>(logoContent, headers, HttpStatus.OK);
    }

}
