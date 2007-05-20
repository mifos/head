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