<%@ page session="true" import = "java.text.*,java.util.*,java.sql.*,java.io.*"%>

<%

com.jspsmart.upload.SmartUpload	mySmartUpload=new com.jspsmart.upload.SmartUpload();
try{
mySmartUpload.initialize(getServletConfig(),request,response);
System.out.println("herere");
			mySmartUpload.upload();
			System.out.println("herere11111111");
			response.setContentType("text/plain");
			int iFileSize = 0;
			try{

				for (int iFileCounter=0;iFileCounter<mySmartUpload.getFiles().getCount();iFileCounter++)
				{
					
					com.jspsmart.upload.File reportUploadFile = mySmartUpload.getFiles().getFile(iFileCounter);
					String reportUploadFileName = reportUploadFile.getFilePathName();
					String reportFileName =reportUploadFile.getFileName();
					System.out.println("FFF111---"+reportUploadFileName);		 
				}
		}catch(Exception e){
			System.out.println("Exception in Resource_post primting control name s" + e);
		}
		}catch(Exception e){
			System.out.println("Exception in Resource_post primting control name s" + e);
		}
		

%>

