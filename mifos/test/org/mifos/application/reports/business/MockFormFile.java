package org.mifos.application.reports.business;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.struts.upload.FormFile;

public class MockFormFile implements FormFile {
	
	private String fileName;
	
	public MockFormFile(String name) {
		setFileName(name);
	}
	
	public void destroy() {
		
	}

	public String getContentType() {
		throw new RuntimeException("not implemented");
	}

	public byte[] getFileData() throws FileNotFoundException, IOException {
		throw new RuntimeException("not implemented");
	}

	public String getFileName() {
		return fileName;
	}

	public int getFileSize() {
		throw new RuntimeException("not implemented");
	}

	public InputStream getInputStream() throws FileNotFoundException,
			IOException {
		return new ByteArrayInputStream(new byte[0]);
	}

	public void setContentType(String arg0) {
		throw new RuntimeException("not implemented");
	}

	public void setFileName(String name) {
		this.fileName = name;
	}

	public void setFileSize(int arg0) {
		throw new RuntimeException("not implemented");
	}

}
