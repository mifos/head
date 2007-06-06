<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<tiles:insert definition=".create">
<tiles:put name="body" type="string">


<FORM name='data_form' method='POST' action='<c:url value="/pages/application/reports/jsp/reportsUpload.jsp" />' ENCTYPE='Multipart/Form-Data'>
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
					           	<mifos:mifoslabel name="reports.listreports" bundle="reportsUIResources"/>						           	
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
				                <span class="headingorange">
				                <mifos:mifoslabel name="reports.upload_report" bundle="reportsUIResources"/>				                	
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
							  
				            </table>
								</tr>
								
						
						  		<tr>
									<td height="30" class="blueline">
									<span class="fontnormal">
									<table width="95%" border="0" cellpadding="3" cellspacing="0" align='center'>
										
					    	            
										<tr class="fontnormal">
										 <td colspan=2>
								 <mifos:select property="reportId" value="null">
									     <c:forEach var="reportCategory" items="${sessionScope.listOfReports}" varStatus="loop" begin='0'>			            	  			            	  	
            	  							<c:forEach var="report" items="${reportCategory.reportsSet}" >	
						            	  	<option value="${report.reportId}">${report.reportName}</option>
				                        	  </c:forEach>
										</c:forEach> 
										   </mifos:select>
										</td>
										</tr>
										  
										  <tr class="fontnormal">
							               <td  nowrap align="left">
							                  	<mifos:mifoslabel name="reports.upload" bundle="reportsUIResources"/>							                  
							                  </td>
											  <td>
						    	              	<html-el:file property="file" value=""/>
						    	              </td>
					    	              </tr>
									  
									  	  <tr>
									  		<td colspan='2' align='center'>
									  		<a href='javascript:document.data_form.submit()'>
									  		<mifos:mifoslabel name="reports.add" bundle="reportsUIResources"/>								  		
									  		</a></td>
									  	  </tr>
									  
									  </table>
									</td>
								</tr>	
								<tr>
					                <td width="63%" class="headingorange">				  
										<span class="fontnormal">
										<html-el:link action="reportsParamsAction.do?method=loadList">
						              	<mifos:mifoslabel name="reports.listparams" bundle="reportsUIResources"/>          			               		              	
						              	</html-el:link>         			               		              	
							          </span>   &nbsp;&nbsp;
									   <span class="fontnormal">
						              	<html-el:link action="reportsDataSourceAction.do?method=loadList">  
						              	<mifos:mifoslabel name="reports.listds" bundle="reportsUIResources"/>          			               		              	
						              	</html-el:link>       			               		              	
							          </span>         
									 </td>
				              	</tr> 
				              	<tr>
					                <td width="63%" class="headingorange">
					                <span class="headingorange">
					                	Report List
					                </span>
					                </td>
				              	</tr>
				              	
				            </table>
				              <br>				  
							  
				
				            <table width="95%" border="0" cellpadding="3" cellspacing="0">
				                <tr>
				                  <td height="30" class="blueline"><span class="fontnormal">To
				                      select, click on a report from the list below. </span></td>
				                </tr>
				             <%int i=1;%>   
				                <tr>
				                 <td height="30" class="blueline" >
								 	<Table width="100%"   cellpadding="3" cellspacing="0">
									 <c:forEach var="reportCategory" items="${sessionScope.listOfReports}" varStatus="loop" begin='0'>			            	  			            	  	
            	  		
					  			<c:forEach var="report" items="${reportCategory.reportsSet}">	
								  	<tr>
				                 <td height="30" class="blueline" >
								 <Table width="100%"  cellpadding="3" cellspacing="0">
								 <tr>
								      <td width="30%"><span class="fontnormal"><%=i++%>.</span><span class="fontnormalbold"><c:out value="${report.reportName}"/></span></td>
									  <td width="30%"><span class="fontnormalbold"><c:out value="${report.reportsJasperMap.reportJasper}"/></span></td>
									   	<c:choose>		            	  	
					  	                  <c:when test='${fn:endsWith(report.reportsJasperMap.reportJasper, ".rptdesign")}'>	
									        <td width="20%" nowrap></td>
									      </c:when>
									      <c:otherwise>
									        <td width="20%" nowrap><a href="reportsParamsMapAction.do?method=loadAddList&reportId=${report.reportId}">Parameters</a></td>  
									      </c:otherwise>
					                    </c:choose>
									  <td width="20%" nowrap><a href="reportsUserParamsAction.do?method=loadAddList&reportId=${report.reportId}">Preview</a></td>
					                </tr>
									
									</table></td></tr>
				                
				                </c:forEach>	
			                
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
</form>
	</tiles:put>
</tiles:insert>
