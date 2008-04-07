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
				<mifos:mifoslabel name="admin.view_reports_category" />
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
	              			<mifos:mifoslabel name="admin.view_reports_category" />
	              		</span>
	              	</td>
            	</tr>
            	<tr>
              		<td valign="top" class="fontnormalbold"> 
              			<span class="fontnormal">
				 			<mifos:mifoslabel name="admin.infoclickonlabel" />
				 			<mifos:mifoslabel name="reportsCategory.labelinfo" bundle="reportsCategoryUIResources" /> 
							<html-el:link href="reportsCategoryAction.do?method=loadDefineNewCategoryPage">
							<mifos:mifoslabel name="reportsCategory.linkinfo" bundle="reportsCategoryUIResources" />
							</html-el:link>
		              		<br />
		              		<br />
              			</span>
             		 	<table width="75%" border="0" cellpadding="3" cellspacing="0">
             		 		<tr width="100%">
								<td align="left" valign="top" colspan="2">
									<font class="fontnormalRedBold"><html-el:errors bundle="adminUIResources" /></font>
								</td>
			   				</tr>
	    		              <tr>
    	        		        <td height="30" colspan="2" class="blueline">
        	    		        	<strong>
  										<mifos:mifoslabel name="reportsCategory.name" bundle="reportsCategoryUIResources" />
									</strong>
								</td>
		                	  </tr>
								<c:forEach var="reportCategory" items="${sessionScope.listOfReportCategories}">
		                  				<tr>
				                	 	   <td width="70%" height="30" class="blueline">
				                    			<span class="fontnormal"> 
	    			                				<c:out value="${reportCategory.reportCategoryName}"/>
	                    						</span>
		                    				</td>
					                    	<td width="30%" class="blueline"> 
												<a href="reportsCategoryAction.do?method=edit&categoryId=<c:out value="${reportCategory.reportCategoryId}" />">
													<mifos:mifoslabel name = "reportsCategory.edit" bundle="reportsCategoryUIResources" />
												</a>
												|
												<a href="reportsCategoryAction.do?method=confirmDeleteReportsCategory&categoryId=<c:out value="${reportCategory.reportCategoryId}" />">
													<mifos:mifoslabel name = "reportsCategory.delete" bundle="reportsCategoryUIResources" />
												</a>
											</td>
		    	            	  		</tr>
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
