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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
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
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"RV_RETURN_VALUE_IGNORED_BAD_PRACTICE", "SE_BAD_FIELD"})
public class UploadLogoController {

	public static final float MAX_WIDTH = 200;
	public static final float MAX_HEIGHT = 70;
	public static final String LOGO_DIRECTORY = "logo";
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private ServletContext servletContext;
	
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
    			scaleAndSaveLogo(logoUpload.getFile());
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
        InputStream in = getLogoStream();
        byte[] logoContent = IOUtils.toByteArray(in);
        in.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<byte[]>(logoContent, headers, HttpStatus.OK);
    }
    
    private InputStream getLogoStream() throws IOException {
        ConfigurationLocator configurationLocator = new ConfigurationLocator();
        Resource customized = configurationLocator.getUploadedMifosLogo(LOGO_DIRECTORY + File.separator + "logo.png");
        InputStream inputStream;
        if (customized.exists()) {
            inputStream = customized.getInputStream();
        } else {
            inputStream = servletContext.getResourceAsStream("/pages/framework/images/logo.jpg");
        }
        return inputStream;
    }

    private void scaleAndSaveLogo(CommonsMultipartFile logo) throws IOException { 
    	BufferedImage bufferedImage = ImageIO.read(logo.getInputStream());
    	BufferedImage finalImage = null;
    	if (bufferedImage.getWidth() > MAX_WIDTH || bufferedImage.getHeight() > MAX_HEIGHT) {
    		float wRatio, hRatio;
    		if (bufferedImage.getWidth() >= bufferedImage.getHeight()) {
    			wRatio = MAX_WIDTH / bufferedImage.getWidth();
    			hRatio = MAX_HEIGHT / bufferedImage.getHeight();
    		} else {
    			wRatio = MAX_HEIGHT / bufferedImage.getWidth();
    			hRatio = MAX_WIDTH / bufferedImage.getHeight();
    		}
    		float resizeRatio = Math.min(wRatio, hRatio);
    		float newHeight = bufferedImage.getHeight() * resizeRatio;
    		float newWidth = bufferedImage.getWidth() * resizeRatio;
    		finalImage = new BufferedImage((int) newWidth, (int) newHeight, bufferedImage.getType());  
    		Graphics2D g = finalImage.createGraphics();  
    		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
    		g.drawImage(bufferedImage, 0, 0, (int) newWidth, (int) newHeight, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);  
    		g.dispose();  
    	} else {
    		finalImage = bufferedImage;
    	}
    	ConfigurationLocator configurationLocator = new ConfigurationLocator();
    	File dir = new File(configurationLocator.getConfigurationDirectory() + File.separator + LOGO_DIRECTORY + File.separator);
    	dir.mkdirs();
    	File file = new File(dir, "logo.png");
		ImageIO.write(finalImage, "png", file);
    }
}
