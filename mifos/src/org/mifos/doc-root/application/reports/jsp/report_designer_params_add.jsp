<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<tiles:insert definition=".create">
<tiles:put name="body" type="string">

<html-el:form action="/reportsParamsAction.do?method=createParams">
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
					           	<mifos:mifoslabel name="reports.addreportsparams" bundle="reportsUIResources"/>						           	
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
				                <mifos:mifoslabel name="reports.addreportsparams" bundle="reportsUIResources"/>				                	
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
										<font class="fontnormalRedBold">
							<%=session.getAttribute("addError")==null?"":session.getAttribute("addError")%>
							</font>								
									</td>
									</tr>
										<tr class="fontnormal">
							                <td  nowrap align="left">
							                <mifos:mifoslabel name="reports.paramname" bundle="reportsUIResources"/>*							                
							                </td>
						    	            <td>
						    	            <mifos:mifosalphanumtext property="name" value=""/>						    	            
						    	            </td>
					    	            </tr>
										
										<tr class="fontnormal">
							                <td  nowrap align="left">
							                <mifos:mifoslabel name="reports.description" bundle="reportsUIResources"/>*							                
							                </td>
						    	            <td>
						    	            <mifos:mifosalphanumtext property="description" value=""/>
						    	            </td>
					    	            </tr>
										
										<tr class="fontnormal">
							                <td  nowrap align="left">
							                <mifos:mifoslabel name="reports.classname" bundle="reportsUIResources"/>							                
							                </td>
						    	            <td>
						    	             <mifos:select property="classname" value="null">
										    <option value="java.lang.String">java.lang.String</option>
											<option value="java.lang.Double">java.lang.Double</option>
											<option value="java.lang.Integer">java.lang.Integer</option>
											<option value="java.util.Date">java.util.Date</option>
				                        	</mifos:select>					    	            
						    	            </td>
					    	            </tr>
										
										<tr class="fontnormal">
							                <td  nowrap align="left">
							                <mifos:mifoslabel name="reports.type" bundle="reportsUIResources"/>							                
							                </td>
						    	            <td>
						    	            <mifos:select property="type" value="null">
										    <option value="Text">Text</option>
											<option value="Date">Date</option>
											<option value="Query">Query</option>
				                        	</mifos:select>	
						    	            </td>
					    	            </tr>
										
										
										
										
										<tr class="fontnormal">
							                <td  nowrap align="left">
							                <mifos:mifoslabel name="reports.data" bundle="reportsUIResources"/>							                
							                </td>
						    	            <td>
											<html-el:textarea property="data" rows="3" cols="20" value="">
					    	              </html-el:textarea>
						    	            
						    	            </td>
					    	            </tr>
										
										
										
										
										<tr class="fontnormal">
										  <td  nowrap align="left">
										  <mifos:mifoslabel name="reports.dsname" bundle="reportsUIResources"/>										  
										  </td>
										  <td>
										  
										  <mifos:select property="datasourceId" value="null">
										    <c:forEach var="reportDataSource" items="${sessionScope.listOfReportsDataSource}" varStatus="loop" begin='0'>			            	  			            	  	
						            	  	<option value="${reportDataSource.datasourceId}">${reportDataSource.name}</option>
				                        	  </c:forEach>
										   </mifos:select> 
										  </td>
										</tr>
										 
										  <tr>
									  		<td colspan='2' align='center'>
									  		<a href='javascript:document.forms[0].submit()'>
									  		<mifos:mifoslabel name="reports.add" bundle="reportsUIResources"/>								  		
									  		</a></td>
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
			      </td>
			  </tr>
		</table>
	<br>
	</html-el:form>
	</tiles:put>
</tiles:insert>
<%session.removeAttribute("addError");%>