package org.mifos.ui.core.controller;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class LogoUpload {
	
	private CommonsMultipartFile file;

	public CommonsMultipartFile getFile() {
		return file;
	}

	public void setFile(CommonsMultipartFile file) {
		this.file = file;
	}	 
}
