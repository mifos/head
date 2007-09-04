<%@ page import="org.mifos.framework.components.configuration.persistence.ConfigurationPersistence"%> 
<%@ page import="org.mifos.application.reports.business.ReportsBO"%> 
<%
	boolean isDisplay = (new ConfigurationPersistence().getConfigurationValueInteger(ConfigurationPersistence.CONFIGURATION_KEY_JASPER_REPORT_IS_HIDDEN) == 0);
%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<tiles:insert definition=".reportLayout">
<tiles:put name="body" type="string">

<html-el:form action="/reportsAction.do">

    	
   		<td align="left" valign="top" bgcolor="#FFFFFF" class="paddingL30T15" style="padding-left:8px; padding-top:10px;">
   		
		   		<table width="95%" border="0" cellpadding="0" cellspacing="0">
		   		
		          <tr>
		            <td colspan="2" align="left" valign="top">
		              <span class="headingorange">
		            	<mifos:mifoslabel name="reports.reports" bundle="reportsUIResources"/>		            	
		               </span><br>
		              <span class="fontnormal">
		              	<mifos:mifoslabel name="reports.instructions" bundle="reportsUIResources"/>   
		              	<%
						 if(isDisplay) {
					    %>
		              	<mifos:mifoslabel name="reports.or" bundle="reportsUIResources"/> 
		              	<html-el:link action="reportsAction.do?method=getAdminReportPage&viewPath=administerreports_path">  
		              	<mifos:mifoslabel name="reports.administerreports" bundle="reportsUIResources"/> 
		              	</html-el:link>         			               		              	
		              	<%
		              	}
		              	%>  
		              	.       			               		              	
			          </span>         

		            </td>		            
		          </tr>	          
		          
		          <tr width="100%">
					<td align="left" valign="top">												
						<font class="fontnormalRedBold">
							<html-el:errors	bundle="reportsUIResources" /> 
						</font>						
					</td>
					</tr>
		          
            	  <c:forEach var="reportCategory" items="${sessionScope.listOfReports}" varStatus="loop" begin='0'>			            	  			            	  	
            	  	<c:choose>		            	  	
					  	<c:when test="${loop.index %2 == 0}">							  		
					  		<tr>
					  		<td width="48%" align="left" valign="top" class="paddingleft">
					  			<span class="fontnormalbold">              
		              				<span class="fontnormalbold">				              				
		              				<c:out value="${reportCategory.reportCategoryName}"/>
		              			</span>
		              			<br>
					  			</span>
							
								<c:forEach var="report" items="${reportCategory.reportsSet}" >	
								<c:choose>
								<c:when test="${report.reportsJasperMap.reportJasper != null && report.isActive == 1}">
								<%
								if(isDisplay || !((ReportsBO)pageContext.getAttribute("report")).getReportsJasperMap().getReportJasper().endsWith(".jasper")) {
							    %>
							  	 <table width="90%" border="0" cellspacing="0" cellpadding="0">
								  	<tr class="fontnormal">
					                  <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
					                  <td width="97%">
						                  <a href="reportsUserParamsAction.do?method=loadAddList&reportId=${report.reportId}" />
						                  <c:out value="${report.reportName}" />
					                  </td>
					                </tr>
				                </table>
				                <% } %>
				             </c:when>
				             </c:choose>
				               </c:forEach>	
			                </td>
					  	</c:when>
					  	
		                <c:otherwise>
		                   	<td width="52%" align="left" valign="top" class="paddingleft">
		                   	<span class="fontnormalbold">              
	              				<span class="fontnormalbold">
	              				<c:out value="${reportCategory.reportCategoryName}"/>
	              			</span>
	              			<br>
				  			</span>
				  			<c:forEach var="report" items="${reportCategory.reportsSet}" >
				  			 <c:choose>
							 <c:when test="${report.reportsJasperMap.reportJasper != null && report.isActive == 1}">
							 <%
								if(isDisplay || !((ReportsBO)pageContext.getAttribute("report")).getReportsJasperMap().getReportJasper().endsWith(".jasper")) {
							  %>
					  			<table width="98%" border="0" cellspacing="0" cellpadding="0">
					                <tr class="fontnormal">
					                  <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
					                  <td width="97%">
						                  <a href="reportsUserParamsAction.do?method=loadAddList&reportId=${report.reportId}" />
						                  <c:out value="${report.reportName}" />
					                  </td>
					                </tr>
				                </table>
				                <% } %>
				               </c:when>
				              </c:choose>  
			                </c:forEach>	
			                </td>	
			                </tr>
			                
	                   	</c:otherwise>
		                </c:choose>
				  </c:forEach>
		  </table>   
        
      <br>
      </td>
  
	</html-el:form>
	</tiles:put>
</tiles:insert>
