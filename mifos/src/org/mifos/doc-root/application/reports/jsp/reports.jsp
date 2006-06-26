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
		              	<mifos:mifoslabel name="reports.or" bundle="reportsUIResources"/> 
		              	<html-el:link action="reportsAction.do?method=getReportPage&viewPath=administerreports_path">  
		              	<mifos:mifoslabel name="reports.administerreports" bundle="reportsUIResources"/>          			               		              	
		              	</html-el:link> 
		              	<font class="fontnormalRedBold">	
		              	<br><br>              		
						<html-el:errors	bundle="reportsUIResources" /> 
						</font>        			               		              			              	
			          </span>         
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
							  	<table width="90%" border="0" cellspacing="0" cellpadding="0">
								  	<tr class="fontnormal">
					                  <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
					                  <td width="97%">
					                  	<c:choose>
					                  	<c:when test="${report.reportIdentifier != null}">
						                  <a href="reportsAction.do?method=getReportPage&viewPath=${report.reportIdentifier}">
						                  	<c:out value="${report.reportName}"/>
						                  </a>
					                    </c:when>
					                    <c:otherwise>
					                      <a href="#">
						                  	<c:out value="${report.reportName}"/>
						                  </a>
					                    </c:otherwise>
					                    </c:choose>
					                  </td>
					                </tr>
				                </table>
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
					  			<table width="98%" border="0" cellspacing="0" cellpadding="0">
					                <tr class="fontnormal">
					                  <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
					                  <td width="97%">
						                <c:choose>
					                  	<c:when test="${report.reportIdentifier != null}">
						                  <a href="reportsAction.do?method=getReportPage&viewPath=${report.reportIdentifier}">
						                  	<c:out value="${report.reportName}"/>
						                  </a>
					                    </c:when>
					                    <c:otherwise>
					                      <a href="#">
						                  	<c:out value="${report.reportName}"/>
						                  </a>
					                    </c:otherwise>
					                    </c:choose>
					                  </td>
					                </tr>
				                </table>
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
