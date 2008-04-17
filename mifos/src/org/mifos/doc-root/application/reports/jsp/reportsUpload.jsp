<%-- 
Copyright (c) 2005-2008 Grameen Foundation USA
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing
permissions and limitations under the License.

See also http://www.apache.org/licenses/LICENSE-2.0.html for an
explanation of the license and how it is applied.
--%>

<%@ page session="true" import = "java.io.*"%>

<%
com.jspsmart.upload.SmartUpload	mySmartUpload=new com.jspsmart.upload.SmartUpload();
try{
mySmartUpload.initialize(getServletConfig(),request,response);
			mySmartUpload.upload();
			response.setContentType("text/plain");
			int iFileSize = 0;
			try{

				for (int iFileCounter=0;iFileCounter<mySmartUpload.getFiles().getCount();iFileCounter++)
				{
					
					com.jspsmart.upload.File reportUploadFile = mySmartUpload.getFiles().getFile(iFileCounter);
					String reportId=mySmartUpload.getRequest().getParameter("reportId");
					System.out.println("report:"+reportId);
					if(reportId==null) reportId="";
					String reportUploadFileName = reportUploadFile.getFilePathName();
					String reportFileName =reportUploadFile.getFileName();
					String fullFileName = "";
					if(reportFileName.toLowerCase().endsWith(".rptdesign")) {
						fullFileName = application.getRealPath("/")+"report/"+reportFileName;
					} else {
						fullFileName = application.getRealPath("/")+"pages/application/reports/uploads/"+reportFileName;
					}
					reportUploadFile.saveAs(fullFileName);
					response.sendRedirect(request.getContextPath()+"/reportsUploadAction.do?method=uploadReport&filename="+fullFileName+"&reportId="+reportId);
				}
		}catch(Exception e){
			e.printStackTrace();
		}
		}catch(Exception e){
			e.printStackTrace();
		}
%>