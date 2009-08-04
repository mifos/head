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
<span id="page.id" title="report_designer_params_view" />

<html-el:form action="/reportsParamsAction.do?method=createParams">
<html-el:hidden property="parameterId" value='<%=request.getParameter("parameterId")%>'/>
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
					           	<mifos:mifoslabel name="reports.viewreportsparams" bundle="reportsUIResources"/>						           	
					        </span>
				        </td>
				      </tr>				      
				     </table>
				     
				     
				      <table width="95%" border="0" cellpadding="0" cellspacing="0">
					  
				        <tr>
				          <td align="left" valign="top" class="paddingL15T15">
				          <table width="95%" border="0" cellpadding="3" cellspacing="0">
						  	<tr>
							<table width="95%" border="0" cellpadding="3" cellspacing="0">
							<tr>
					                <td width="63%" class="headingorange">				  
										<span class="fontnormal">
										<html-el:link action="reportsParamsAction.do?method=loadList">
						              	<mifos:mifoslabel name="reports.listparams" bundle="reportsUIResources"/>          			               		              	
						              	</html-el:link>         			               		              	
							          </span> 
									       
									 </td>
				              	</tr> 
				              <tr>
				                <td width="63%" class="headingorange">
				                <span class="headingorange">
				                <mifos:mifoslabel name="reports.viewreportsparams" bundle="reportsUIResources"/>				                	
				                </span>
				                </td>
				              </tr>
				            </table>
								</tr>
								
						
						  		<tr>
									<td height="30" class="blueline">
									<span class="fontnormal">
									<table width="95%" border="0" cellpadding="3" cellspacing="0" align='center'>
										<tr width="100%">
											<td align="left" valign="top" colspan='2'>												
											<font class="fontnormalRedBold">
										<html-el:errors	bundle="reportsUIResources" /> 
										</font>						
									</td>
									</tr>
									<c:forEach var="reportParams" items="${sessionScope.viewParams}" varStatus="loop" begin='0'>
										<tr class="fontnormal">
							                <td  nowrap align="left">
							                <mifos:mifoslabel name="reports.paramname" bundle="reportsUIResources"/>							                
							                </td>
						    	            <td>
						    	            <c:out value="${reportParams.name}"/>
						    	            </td>
					    	            </tr>
										
										<tr class="fontnormal">
							                <td  nowrap align="left">
							                <mifos:mifoslabel name="reports.description" bundle="reportsUIResources"/>							                
							                </td>
						    	            <td>
						    	           <c:out value="${reportParams.description}"/>
						    	            </td>
					    	            </tr>
										
										<tr class="fontnormal">
							                <td  nowrap align="left">
							                <mifos:mifoslabel name="reports.classname" bundle="reportsUIResources"/>							                
							                </td>
						    	            <td>
						    	             <c:out value="${reportParams.classname}"/>			    	            
						    	            </td>
					    	            </tr>
										
										<tr class="fontnormal">
							                <td  nowrap align="left">
							                <mifos:mifoslabel name="reports.type" bundle="reportsUIResources"/>							                
							                </td>
						    	            <td>
						    	            <c:out value="${reportParams.type}"/>
						    	            </td>
					    	            </tr>
										
										
										
										
										<!--<tr class="fontnormal">
							                <td  nowrap align="left">
							                <mifos:mifoslabel name="reports.data" bundle="reportsUIResources"/>							                
							                </td>
						    	            <td>
											<c:out value="${reportParams.data}"/>
						    	            
						    	            </td>
					    	            </tr>
										
										
										
										
										<tr class="fontnormal">
										  <td  nowrap align="left">
										  <mifos:mifoslabel name="reports.dsname" bundle="reportsUIResources"/>										  
										  </td>
										  <td>
										  
										 
										  </td>
										</tr>-->
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
			      </td>
			  </tr>
		</table>
	<br>
	</html-el:form>
	</tiles:put>
</tiles:insert>
