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

<%@ page session="true" import = "java.io.*,net.sf.jasperreports.engine.*,net.sf.jasperreports.engine.xml.*,net.sf.jasperreports.engine.design.*,net.sf.jasperreports.engine.util.JRSaver,net.sf.jasperreports.view.*,java.util.*"%>
<%
try{
			
			
		//		 First, load JasperDesign from XML and compile it into JasperReport
		JasperDesign jasperDesign = JRXmlLoader.load("C:\\jt5025\\webapps\\Mifos\\pages\\application\\reports\\uploads\\jasperreports_demo.jrxml");
		
		JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
		JRSaver.saveObject(jasperReport, "C:\\jt5025\\webapps\\Mifos\\pages\\application\\reports\\uploads\\jasperreports_demo.jasper");
		
//		 Second, create a map of parameters to pass to the report.
		Map parameters = new HashMap();
		java.sql.Connection con = null;
//		parameters.put("Title", "Basic JasperReport");
//		parameters.put("MaxSalary", new Double(25000.00));
 
System.out.println("in 0");
//		 Fourth, create JasperPrint using fillReport() method
//		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,  parameters, con);
JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
		
//		 You can use JasperPrint to create PDF
		System.out.println("in 1");
		JasperExportManager.exportReportToPdfFile(jasperPrint, "C:\\jt5025\\webapps\\Mifos\\pages\\application\\reports\\uploads\\jasperreports_demo.pdf");
System.out.println("in 2");
//		 Or to view report in the JasperViewer
		JasperViewer.viewReport(jasperPrint);
		System.out.println("in 3");
		}
		catch (JRException e)
		{
			e.getMessage();
		}
	
	%>