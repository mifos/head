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
<script language="JavaScript" src="<%=request.getContextPath()%>/pages/application/reports/jsp/date-picker.js"></script>
<FORM name='data_form' method='POST' action='<%=request.getContextPath()%>/reportsUserParamsAction.do?method=processReport'>

	
	<html-el:hidden property="reportId" value='<%=request.getParameter("reportId")%>'/>
	<html-el:hidden property="applPath" value='<%=application.getRealPath("/")+"pages/application/reports/uploads/"%>'/>
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
					           	<mifos:mifoslabel name="reports.listuserparam" bundle="reportsUIResources"/>						           	
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
							<%=session.getAttribute("paramerror")==null?"":session.getAttribute("paramerror")%>
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
					                	Enter Parameters for report 
					                </span>
					                </td>
				              	</tr>
				              	
				            </table>
				              <br>				  
							  
				
				            <table width="95%" border="0" cellpadding="3" cellspacing="0">
							 
				                <tr>
				                  <td height="30" class="blueline">
								  <html-el:link action="reportsAction.do?method=getAdminReportPage&viewPath=administerreports_path">  
		              	<mifos:mifoslabel name="reports.administerreports" bundle="reportsUIResources"/>          			               		              	
		              	</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;
								  <span class="fontnormal">
 
								 </span></td>
				                </tr>
											
				             <%int i=1;%>   
				                <tr>
				                 <td height="30" class="blueline" >
								 	<Table width="100%"   cellpadding="3" cellspacing="0">
									 <tr>
									 <td width="30%"><span class="fontnormalbold"><mifos:mifoslabel name="reports.paramname" bundle="reportsUIResources"/></span></td>
									 <td>&nbsp;</td>
									 </tr>
									 <c:forEach var="reportParamsMapRep" items="${sessionScope.listOfAllParametersForReportId}" varStatus="loop" begin='0'>			            	  			            	  	
            	  				  <tr>
								      <td width="30%"><span class="fontnormal"><%=i%>.</span><span class="fontnormalbold"><c:out value="${reportParamsMapRep.reportsParams.description}"/></span></td>
									 <td nowrap>
									  <c:choose>		            	  	
					  						<c:when test="${reportParamsMapRep.reportsParams.type== 'Text'}">
									 <mifos:mifosalphanumtext property="${reportParamsMapRep.reportsParams.name}" value=""/>
									  	</c:when>
									</c:choose>	
									 <c:choose>		            	  	
					  						<c:when test="${reportParamsMapRep.reportsParams.type== 'Date'}">
									 <mifos:mifosalphanumtext property="${reportParamsMapRep.reportsParams.name}" value=""/>
									 <a href="javascript:show_calendar('forms[0].${reportParamsMapRep.reportsParams.name}');"
           							onmouseover="window.status='Date Picker';return true;"
           							onmouseout="window.status='';return true;">CALENDER</a>

									  	</c:when>
									</c:choose>	
									 <c:choose>		            	  	
					  						<c:when test="${reportParamsMapRep.reportsParams.type== 'Query'}">
											  <mifos:select property="${reportParamsMapRep.reportsParams.name}" value="null">
										    		<%java.util.List l = (java.util.List)session.getAttribute("para"+i);
													Object[] o = l==null?null:l.toArray();
													if(o!=null && o.length>0)
													{
													for(int j=0;j<o.length;j++){
														org.mifos.application.reports.business.ReportsParamQuery rpq = (org.mifos.application.reports.business.ReportsParamQuery) o[j]; %>
														<option value='<%=rpq.getValue1()%>'><%=rpq.getValue2()%></option>
													<%}
													}else{%>
														<mifos:mifosalphanumtext property="${reportParamsMapRep.reportsParams.name}" value=""/>
													<%}%>
												
										   </mifos:select> 
									 	</c:when>
									</c:choose>	
									</td>
									</tr>
									
								<%i++;%>	
						 </c:forEach>
						 </table>
             </td>
			 </tr>
			 			 <br>
						<tr>
		                <td  nowrap class="fontnormalbold" colspan="2">
						 <span class="fontnormal"><mifos:mifoslabel name="reports.exportformat" bundle="reportsUIResources"/> </span>
		                  </td>
						  
						  </tr>
						 
				<tr class="fontnormal">
				   <td><input type='radio' name='expFormat' value='pdf'>
				  PDF </td></tr>
				  <tr class="fontnormal">
				   <td><input type='radio' name='expFormat' value='html'>
				  Html </td></tr>
				
                <tr class="fontnormal">
                  <td width="30%">&nbsp; </td>
                  <td width="70%"><br>
                      <input name="Button2" type="submit" class="buttn" value="Process">

                  </td>
                </tr>

					
				              </table>             
			              
			              </td>
			        </tr>
			        
			      </table>
			      <br>
			      </td>
			  </tr>
		</table>
	<br>
	</form>
	</tiles:put>
</tiles:insert>
<%session.setAttribute("paramerror","");%>