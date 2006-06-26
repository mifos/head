<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>

<tiles:insert definition=".create">
<tiles:put name="body" type="string">

<html-el:form action="/reportsAction.do">

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
				            </table>
								</tr>
								
						
						  		<tr>
									<td height="30" class="blueline">
									<span class="fontnormal">
									<table width="95%" border="0" cellpadding="3" cellspacing="0" align='center'>
										<tr class="fontnormal">
							                <td  nowrap align="left">
							                <mifos:mifoslabel name="reports.title" bundle="reportsUIResources"/>							                
							                </td>
						    	            <td>
						    	            <mifos:mifosalphanumtext property="title" value=""/>						    	            
						    	            </td>
					    	            </tr>
					    	            
										<tr class="fontnormal">
										  <td  nowrap align="left">
										  <mifos:mifoslabel name="reports.category" bundle="reportsUIResources"/>										  
										  </td>
										  <td>
										  
										  <mifos:select property="category" value="">
											<option value="">Select</option>
				                            <option value="">Performance Reports</option>
				                            <option value="">Financial Reports</option>
										   </mifos:select> 
										  </td>
										</tr>
										
										  <tr class="fontnormal">
							                  <td  nowrap align="left">
							                  	<mifos:mifoslabel name="reports.description" bundle="reportsUIResources"/>							                  
							                  </td>
						    	              <td>
							    	              <html-el:textarea property="text" rows="3" cols="20" value="">
							    	              </html-el:textarea>
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
									  		<a href=''>
									  		<mifos:mifoslabel name="reports.add" bundle="reportsUIResources"/>								  		
									  		</a></td>
									  	  </tr>
									  
									  </table>
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
				                
				                <tr>
				                 <td height="30" class="blueline" >
								 	<Table width="100%"   cellpadding="3" cellspacing="0">
									<tr>
									  <td width="30%"><span class="fontnormalbold"> Staff performance </span></td>	
						  		      <td width="20%" nowrap><a href="report_param.htm">Final</a></td>
									<td width="20%" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
									<td width="20%" nowrap><a href="reportaccess_log.htm">&nbsp;</a></td><td width="20%" nowrap><a href="reportaccess_log.htm">Access Log</a></td>
									<td width="20%" nowrap><a href="reportaudit_trail.htm">Audit Trail</a></td>
									  </tr></table>
								  </td>				
				                </tr>
				                
				               <tr>
				                 <td height="30" class="blueline" >
								 	<Table width="100%" cellpadding="3" cellspacing="0">
									<tr>
									  <td width="30%"><span class="fontnormalbold"> Overall performance </span></td>	
						  		      <td width="20%" nowrap><a href="report_param.htm">Preview</a></td>
									<td width="20%">&nbsp;</td>
									<td width="20%" nowrap><a href="#">Finalize</a></td><td width="20%" nowrap><a href="reportaccess_log.htm">Access Log</a></td>
									<td width="20%" nowrap><a href="reportaudit_trail.htm">Audit Trail</a></td>													
									  </tr></table>
								  </td>				
				                </tr>
				                
				           	   <tr>
				                 <td height="30" class="blueline" >
								 		<Table width="100%"  cellpadding="3" cellspacing="0">
									<tr><td width="30%">&nbsp;&nbsp;<span class="fontnormal">3.</span>
				                       <span class="fontnormalbold"> Report
				                      3</span></td>	
						  		    <td width="20%" nowrap><a href="report_param.htm">Final</a></td>
									  <td width="20%" nowrap><a href="report_param.htm">Preview</a></td>
									<td width="20%" nowrap><a href="#">Finalize</a></td><td width="20%" nowrap><a href="reportaccess_log.htm">Access Log</a></td>
									<td width="20%" nowrap><a href="reportaudit_trail.htm">Audit Trail</a></td>				
									  </tr></table>
								  </td>				
				                </tr>
				                
				   				<tr>
				                 <td height="30" class="blueline" >
								 	<Table width="100%"   cellpadding="3" cellspacing="0">
									<tr><td width="30%">&nbsp;&nbsp;<span class="fontnormal">4.</span>
				                       <span class="fontnormalbold"> Report
				                      4</span></td>	
						  		    <td width="20%" nowrap><a href="report_param.htm">Final</a></td>
									<td width="20%" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
									<td width="20%" nowrap><a href="reportaccess_log.htm">&nbsp;</a></td><td width="20%" nowrap><a href="reportaccess_log.htm">Access Log</a></td>
									<td width="20%" nowrap><a href="reportaudit_trail.htm">Audit Trail</a></td>				
									  </tr></table>
								  </td>				
				                </tr>
				                
				               <tr>
				                 <td height="30" class="blueline" >
								 	<Table width="100%" cellpadding="3" cellspacing="0">
									<tr><td width="30%">&nbsp;&nbsp;<span class="fontnormal">5.</span>
				                       <span class="fontnormalbold"> Report
				                      5</span></td>	
						  		    <td width="20%" nowrap><a href="report_param.htm">Preview</a></td>
									<td width="20%">&nbsp;</td>
									<td width="20%" nowrap><a href="#">Finalize</a></td><td width="20%" nowrap><a href="reportaccess_log.htm">Access Log</a></td>
									<td width="20%" nowrap><a href="reportaudit_trail.htm">Audit Trail</a></td>													
									  </tr></table>
								  </td>				
				               </tr>
				                
				              <tr>
				                 <td height="30" class="blueline" >
								 		<Table width="100%" border=0  cellpadding="3" cellspacing="0">
									<tr><td width="30%">&nbsp;&nbsp;<span class="fontnormal">6.</span>
				                       <span class="fontnormalbold"> Report
				                      6</span></td>	
						  		    <td width="20%" nowrap><a href="report_param.htm">Final</a></td>
									  <td width="20%" nowrap><a href="report_param.htm">Preview</a></td>
									<td width="20%" nowrap><a href="#">Finalize</a></td><td width="20%" nowrap><a href="reportaccess_log.htm">Access Log</a></td>
									<td width="20%" nowrap><a href="reportaudit_trail.htm">Audit Trail</a></td>				
									  </tr></table>
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
	</html-el:form>
	</tiles:put>
</tiles:insert>
