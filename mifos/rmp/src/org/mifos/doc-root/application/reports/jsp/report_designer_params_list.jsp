<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<tiles:insert definition=".create">
<tiles:put name="body" type="string">

<html-el:form action="/reportsAction.do">
<script>
function deleteMe(page,paramId)
{
	document.forms[0].parameterId.value = paramId;
	document.forms[0].action = page;
	document.forms[0].submit();
}

</script>
	<html-el:hidden property="parameterId" value=''/>
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
					           	<mifos:mifoslabel name="reports.listparams" bundle="reportsUIResources"/>						           	
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
					                	Reports Parameters List
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
										<html-el:link action="reportsParamsAction.do?method=load">
						              	<mifos:mifoslabel name="reports.addreportsparams" bundle="reportsUIResources"/>          			               		              	
						              	</html-el:link>         			               		              	
							          </span></td>
				                </tr>
								
				             <%int i=1;%>   
				                <tr>
				                 <td height="30" class="blueline" >
								 	<Table width="100%"   cellpadding="3" cellspacing="0">
									 <tr>
									 <td width="30%"><span class="fontnormalbold"><mifos:mifoslabel name="reports.paramname" bundle="reportsUIResources"/></span></td>
									 <td><span class="fontnormalbold"><mifos:mifoslabel name="reports.description" bundle="reportsUIResources"/></span></td>
									 </tr>
									 <c:forEach var="reportParams" items="${sessionScope.listOfReportsParams}" varStatus="loop" begin='0'>			            	  			            	  	
            	  				  	
								 <tr>
								      <td width="30%"><span class="fontnormal"><%=i++%>.</span><span class="fontnormalbold">
									  <html-el:link action="reportsParamsAction.do?method=loadView&parameterId=${reportParams.parameterId}">
									  <c:out value="${reportParams.name}"/>
									  </html-el:link>
									  </span></td>
									  <td nowrap><span class="fontnormal"><c:out value="${reportParams.description}"/></span></td>
									  <td nowrap><a href="javascript:deleteMe('reportsParamsAction.do?method=deleteParams',${reportParams.parameterId})">Delete</a></td>
									  
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
