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
<span id="page.id" title="reports" />

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
