<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<table width="95%" border="0" cellpadding="0" cellspacing="0">
	      <tr>
	        <td class="bluetablehead05">
	        <span class="fontnormal8pt">
				<html-el:link href="AdminAction.do?method=load">
					<mifos:mifoslabel name="product.admin" bundle="ProductDefUIResources" />
				</html-el:link>
				/
			</span>
			<span class="fontnormal8ptbold">
				<mifos:mifoslabel name="admin.viewreports" />
			</span>
			</td>
	      </tr>
	    </table>

      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td align="left" valign="top" class="paddingL15T15">
          	<table width="95%" border="0" cellpadding="3" cellspacing="0">
            	<tr>
	              	<td class="headingorange">
	              		<span class="headingorange">
	              			<mifos:mifoslabel name="admin.viewreports" />
	              		</span>
	              	</td>
            	</tr>
            	<tr>
              		<td valign="top" class="fontnormalbold"> 
              			<span class="fontnormal">
				 			<mifos:mifoslabel name="admin.infoclickonlabel" />
				 			<mifos:mifoslabel name="reports.labelinfo" bundle="reportsUIResources" /> 
							<html-el:link href="birtReportsUploadAction.do?method=getBirtReportsUploadPage&viewPath=administerreports_path">
								<mifos:mifoslabel name="reports.linkinfo" bundle="reportsUIResources" />
							</html-el:link>
		              		<br />
		          			<br />
		            	</span>
		            </td>
              		<table width="95%" border="0" cellpadding="3" cellspacing="0">
              		<tr>
              			<td>
								<font class="fontnormalRedBold">
									<html-el:errors	bundle="adminUIResources" />	
								</font>
						</td>
					</tr>	
					</table>
             		 	<table width="75%" border="0" cellpadding="3" cellspacing="0">
	                 		<c:forEach var="reportCategory" items="${sessionScope.listOfReports}" varStatus="loop" begin='0'>
	    		              <tr>
    	        		        <td height="30" colspan="2" class="blueline">
        	    		        	<strong>
  										<c:out value="${reportCategory.reportCategoryName}" />
									</strong>
								</td>
		                	  </tr>

								<c:forEach var="report" items="${reportCategory.reportsSet}">
		                  			<c:choose>
										<c:when test="${report.reportsJasperMap.reportJasper != null}">
		                  				<tr>
				                	 	   <td width="70%" height="30" class="blueline">
				                    			<span class="fontnormal"> 
	    			                				<c:out value="${report.reportName}"/>
														<c:if test="${report.isActive == '0'}">
															&nbsp;&nbsp;<img src="pages/framework/images/status_closedblack.gif" width="8" height="9" />inactive
                                						</c:if>
	                    						</span>
		                    				</td>
		                    
											
					      					<td width="30%" class="blueline"> 
												<a href="birtReportsUploadAction.do?method=edit&reportId=<c:out value="${report.reportId}" />">
													<mifos:mifoslabel name = "reports.edit" bundle="reportsUIResources" />
												</a>
									      		|
												<a href="birtReportsUploadAction.do?method=downloadBirtReport&reportId=<c:out value="${report.reportId}" />">
													<mifos:mifoslabel name = "reports.download" bundle="reportsUIResources" />
												</a>
											</td>
		    	            	  		</tr>
		    	            	  		</c:when>
				             		</c:choose>
								</c:forEach>
    	            	  	</c:forEach>
	               		</table>               
    	            </td>
            	</tr>
        	  </table>
        	</td>
        </tr>
      </table>
      <br />

	</tiles:put>
</tiles:insert>
