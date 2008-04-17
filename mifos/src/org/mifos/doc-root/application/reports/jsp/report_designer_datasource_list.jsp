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

<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<tiles:insert definition=".create">
<tiles:put name="body" type="string">

<html-el:form action="/reportsAction.do">
<script>
function deleteMe(page,dsId)
{
	document.forms[0].datasourceId.value = dsId;
	document.forms[0].action = page;
	document.forms[0].submit();
}

</script>
	<html-el:hidden property="datasourceId" value=''/>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		  <tr>
			    <td width="174" height="300" align="left" valign="top" class="bgorangeleft">
				    <table width="100%" border="0" cellspacing="0" cellpadding="0">
				        <tr>
				          <td class="leftpanehead"></td>
				        </tr>
				        <tr>
				          <td class="leftpanelinks">
				          <table width="90%" border="0" cellspacing="0" cellpadding="0">           
				          </table>
				         </td>
				        </tr>
				    </table>
			    </td>
			    
			    <td align="left" valign="top" bgcolor="#FFFFFF" class="paddingleftmain">     
			     
				     <table width="95%" border="0" cellpadding="0" cellspacing="0">
				      <tr>
				        <td class="bluetablehead05">
					        <span class="fontnormal8pt">
					        </span>
					        <span class="fontnormal8ptbold">
					           	<mifos:mifoslabel name="reports.listds" bundle="reportsUIResources"/>						           	
					        </span>
				        </td>
				      </tr>				      
				     </table>
				     
				     
				      <table width="95%" border="0" cellpadding="0" cellspacing="0">
					   <tr width="100%">
					<td align="left" valign="top">												
						<font class="fontnormalRedBold">
							<html-el:errors	bundle="reportsUIResources" /> 
						</font>			
						<font class="fontnormalRedBold">
							<%=session.getAttribute("deleteError")==null?"":session.getAttribute("deleteError")%>
							</font>				
					</td>
					</tr>
				        <tr>
				          <td align="left" valign="top" class="paddingL15T15">
				          <table width="95%" border="0" cellpadding="3" cellspacing="0">
						  	<tr>
							
								</tr>
								 	<tr>
					                <td width="63%" class="headingorange">
					                <span class="headingorange">
					                	Reports Datasource List
					                </span>
					                </td>
				              	</tr>
				              	
				            </table>
				              <br>				  
							  
				
				            <table width="95%" border="0" cellpadding="3" cellspacing="0">
							 
				                <tr>
				                  <td height="30" class="blueline">
								  <span class="fontnormal">
										<html-el:link action="reportsAction.do?method=getAdminReportPage&viewPath=administerreports_path">  
		              	<mifos:mifoslabel name="reports.administerreports" bundle="reportsUIResources"/>          			               		              	
		              	</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;
										
										<html-el:link action="reportsDataSourceAction.do?method=load">
						              	<mifos:mifoslabel name="reports.addreportsds" bundle="reportsUIResources"/>          			               		              	
						              	</html-el:link> 
										        			               		              	
							          </span></td>
				                </tr>
								
				             <%int i=1;%>   
				                <tr>
				                 <td height="30" class="blueline" >
								 	<Table width="100%"   cellpadding="3" cellspacing="0">
									 <tr>
									 <td width="30%"><span class="fontnormalbold"><mifos:mifoslabel name="reports.dsname" bundle="reportsUIResources"/></span></td>
									 </tr>
									 <c:forEach var="reportDs" items="${sessionScope.listOfReportsDataSource}" varStatus="loop" begin='0'>			            	  			            	  	
            	  				  	
								 <tr>
								      <td width="30%"><span class="fontnormal"><%=i++%>.</span><span class="fontnormalbold">
									  <html-el:link action="reportsDataSourceAction.do?method=loadView&dataSourceId=${reportDs.datasourceId}">
									  <c:out value="${reportDs.name}"/>
									  </html-el:link>
									  </span></td>
									 
									  <td nowrap><a href="javascript:deleteMe('reportsDataSourceAction.do?method=deleteDataSource',${reportDs.datasourceId})">Delete</a></td>
									  
					                </tr>
									
									
						 </c:forEach>
             
				              </table>             
			              
			              </td>
			        </tr>
			        
			      </table>
			      <br>
			      </td>
			  </tr>
		</table>
	<br>
	</html-el:form>
	</tiles:put>
</tiles:insert>
<%session.removeAttribute("deleteError");%>