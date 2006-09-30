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