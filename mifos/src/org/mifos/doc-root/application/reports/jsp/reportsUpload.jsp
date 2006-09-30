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
					String fullfilename = application.getRealPath("/")+"pages/application/reports/uploads/"+reportFileName;
					reportUploadFile.saveAs(fullfilename);
					response.sendRedirect(request.getContextPath()+"/reportsUploadAction.do?method=uploadReport&filename="+fullfilename+"&reportId="+reportId);
				}
		}catch(Exception e){
			e.printStackTrace();
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		

%>

