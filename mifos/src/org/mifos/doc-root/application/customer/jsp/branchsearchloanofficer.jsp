<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insert definition=".clientsacclayoutmenu">
 <tiles:put name="body" type="string">
 
 <script>
 	function fnGet(loanOfficerId) {
 		customerSearchActionForm.method.value="get";
 		customerSearchActionForm.loanOfficerId.value=loanOfficerId;
 		customerSearchActionForm.action="CustomerSearchAction.do";
 		customerSearchActionForm.submit();
 	}
 </script>
<html-el:form action="CustomerSearchAction.do" >
  <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td width="70%" height="24" align="left" valign="top" class="paddingL10"><table width="96%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td class="headingorange"><c:out value='${requestScope.Context.businessResults["Office"]}'/>
                	<br>
                  <span class="fontnormalbold">
                	<mifos:mifoslabel name="CustomerSearch.toreview"/>
                	 <mifos:mifoslabel name="CustomerSearch.or"/>
	                  <mifos:mifoslabel name="CustomerSearch.edit"/>
	                  <mifos:mifoslabel name="CustomerSearch.a"/>
	                  <mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"/>,
	                  <c:choose>
	                 	 <c:when test="${sessionScope.isCenterHeirarchyExists==Constants.YES}">
	                 	 	<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />,&nbsp;
	                  		<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />,
	                  	</c:when>
	                  	<c:otherwise>
	                  		<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />,
	                  		</c:otherwise>
	                  </c:choose>
	                  <mifos:mifoslabel name="CustomerSearch.or"/>
	                  <mifos:mifoslabel name="CustomerSearch.account"/>
                </span></td>
              </tr>
              </table>
            <br>
            <table width="90%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="left" valign="top" class="fontnormalbold"><table width="100%" border="0" cellspacing="0" cellpadding="0" style="float:left;">
                    <tr>
                      <td width="40%" align="left" valign="top" class="fontnormal">
                      <table width="100%" border="0" cellspacing="0" cellpadding="4">
                          <tr class="fontnormal">
                            <td width="100%" colspan="2" class="bglightblue">
                            <span class="heading">
                            	<mifos:mifoslabel name="CustomerSearch.search"/>
                            </span></td>
                          </tr>
                          <tr class="fontnormal">
                            <td height="26" colspan="2">
                              <mifos:mifoslabel name="CustomerSearch.tosearch"/>,&nbsp;
			                  <mifos:mifoslabel name="CustomerSearch.enter"/>&nbsp;
			                  <mifos:mifoslabel name="CustomerSearch.a"/>&nbsp;
			                  <mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"/>,&nbsp;
			                  <c:choose>
			                 	 <c:when test="${sessionScope.isCenterHeirarchyExists==Constants.YES}">
			                 	 	<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />,&nbsp;
			                  		<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />,
			                  	</c:when>
			                  	<c:otherwise>
			                  		<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />,
	                  			</c:otherwise>
	                 		 </c:choose>
			                  <mifos:mifoslabel name="CustomerSearch.searchnamesysid"/>&nbsp;
                            </td>
                          </tr>
                          <font class="fontnormalRedBold"><html-el:errors bundle="CustomerSearchUIResources"/> </font>                
                          <tr class="fontnormal">
                            <td height="26" colspan="2">
									<html-el:text property="searchNode(searchString)" maxlength="200"/>
									<html-el:hidden property="searchNode(search_name)" value="CustomerSearchResults"/>
                                <html-el:submit style="width:60px;" styleClass="buttn">
                      	<mifos:mifoslabel name="CustomerSearch.search"/>
                      </html-el:submit>
                            </td>
                          </tr>
                        </table>
                      </td>
                      <td width="12%" align="center" valign="top" class="headingorange">
                      	<mifos:mifoslabel name="CustomerSearch.or"/>&nbsp;
                      </td>

                      <td width="48%" align="left" valign="top" class="fontnormal" id="hideforloanofficer" >
		                	
		                	 <div id="centerBlock1" style="display:none;">
                      	
		                      <table width="100%" border="0" cellspacing="0" cellpadding="4">
		                        <tr class="fontnormal">
		                          <td width="100%" colspan="2" class="bglightblue">
		                          <span class="heading"><mifos:mifoslabel name="CustomerSearch.select"/>&nbsp;
		                          <c:choose>
		                          	<c:when test='${requestScope.Context.businessResults["GrpHierExists"] eq true}'>
		                          	<mifos:mifoslabel name="${ConfigurationConstants.CENTER}"/>
		                          	</c:when>
									
									<c:otherwise>
									<mifos:mifoslabel name="${ConfigurationConstants.GROUP}"/>
									</c:otherwise>
									</c:choose>
		                          </span></td>
		                        </tr>
		                      </table>
		                       <div id="Layer2" style="border: 1px solid #CECECE; height:100px; width:100%; overflow: auto; padding:6px; margin-top:5px;"> 
		                        <span class="fontnormal">
									<c:forEach items="${requestScope.customerList}" var="customer">
									<c:choose>
									<c:when test='${requestScope.Context.businessResults["GrpHierExists"] eq true}'>
										<html-el:link href='centerCustAction.do?method=get&customerId=${customer.customerId}&searchId=${customer.searchId}&globalCustNum=${customer.globalCustNum}&recordLoanOfficerId=${recordLoanOfficerId}&recordOfficeId=${recordOfficeId}' >
									<c:out value="${customer.displayName}"/>
									</html-el:link><br>
										</c:when>
									<c:otherwise>
									<html-el:link href='groupCustAction.do?method=get&customerId=${customer.customerId}&searchId=${customer.searchId}&globalCustNum=${customer.globalCustNum}&recordLoanOfficerId=${recordLoanOfficerId}&recordOfficeId=${recordOfficeId}' >
									<c:out value="${customer.displayName}"/>
									</html-el:link><br>
									</c:otherwise>
									</c:choose>
									</c:forEach>                            
		                        </span></div>
                        
                        </div> 
		                
		                  <div id="loanOfficerBlock" style="display:block;">
		                      <table width="100%" border="0" cellspacing="0" cellpadding="4" >
		                          <tr class="fontnormal">
		                            <td width="100%" colspan="2" class="bglightblue"><span class="heading">
		                            <mifos:mifoslabel name="CustomerSearch.select"/>&nbsp;loan officer</span></td>
		                          </tr>
		                        </table>
							<html-el:hidden property="method" value="search"/>
							<html-el:hidden property="officeName" value='${requestScope.Context.businessResults["Office"]}'/>													
							<c:choose>
							<c:when test='${sessionScope.UserContext.officeLevelId==5}'>							
							<html-el:hidden property="searchNode(search_officeId)" value="${sessionScope.UserContext.branchId}"/> 
							</c:when>
							<c:otherwise>
							<html-el:hidden property="searchNode(search_officeId)" value="0"/> 
							</c:otherwise>
							</c:choose>	
							<html-el:hidden property="loanOfficerId" value=""/>
											
							      
							 <div id="Layer2" style="border: 1px solid #CECECE; height:100px; width:100%; overflow: auto; padding:6px; margin-top:5px;" > 
									<c:forEach items='${requestScope.Context.businessResults["LoanOfficerslist"]}' var="loanOfficer">
									<html-el:link href="javascript:fnGet(${loanOfficer.personnelId})">
									<c:out value="${loanOfficer.displayName}"/>
									</html-el:link><br>
									</c:forEach>                           
							 </div>
					 
					 
					 
							                         
                      </div>
                      
                      
                      
                     
                      </td>
                      
                    </tr>
                <tr>
                    <td align="left" valign="top" class="fontnormal">&nbsp;</td>
                      <td align="center" valign="top" class="headingorange">&nbsp;</td>
                      <td align="left" valign="top" class="fontnormal">&nbsp;</td>
                    </tr>
                    <tr>
                      <td align="left" valign="top" class="fontnormal">&nbsp;</td>
                      <td align="center" valign="top" class="headingorange">&nbsp;</td>
                      <td align="left" valign="top" class="fontnormal">
                      
                      	<div id="centerBlock2" style="display:block;">
                      	
		                      <table width="100%" border="0" cellspacing="0" cellpadding="4">
		                        <tr class="fontnormal">
		                          <td width="100%" colspan="2" class="bglightblue">
		                          <span class="heading"><mifos:mifoslabel name="CustomerSearch.select"/>&nbsp;
		                          <c:choose>
		                          	<c:when test='${requestScope.Context.businessResults["GrpHierExists"] eq true}'>
		                          	<mifos:mifoslabel name="${ConfigurationConstants.CENTER}"/>
		                          	</c:when>
									
									<c:otherwise>
									<mifos:mifoslabel name="${ConfigurationConstants.GROUP}"/>
									</c:otherwise>
									</c:choose>
		                          </span></td>
		                        </tr>
		                      </table>
		                       <div id="Layer2" style="border: 1px solid #CECECE; height:100px; width:100%; overflow: auto; padding:6px; margin-top:5px;"> 
		                        <span class="fontnormal">
									<c:forEach items="${requestScope.customerList}" var="customer">
									<c:choose>
									<c:when test='${requestScope.Context.businessResults["GrpHierExists"] eq true}'>
										<html-el:link href='centerCustAction.do?method=get&customerId=${customer.customerId}&searchId=${customer.searchId}&globalCustNum=${customer.globalCustNum}&recordLoanOfficerId=${recordLoanOfficerId}&recordOfficeId=${recordOfficeId}' >
									<c:out value="${customer.displayName}"/>
									</html-el:link><br>
										</c:when>
									<c:otherwise>
									<html-el:link href='groupCustAction.do?method=get&customerId=${customer.customerId}&searchId=${customer.searchId}&globalCustNum=${customer.globalCustNum}&recordLoanOfficerId=${recordLoanOfficerId}&recordOfficeId=${recordOfficeId}' >
									<c:out value="${customer.displayName}"/>
									</html-el:link><br>
									</c:otherwise>
									</c:choose>
									</c:forEach>                            
		                        </span></div>
                        
                        </div> 
                        </td>
                        <c:set var="loadfordward" value='${requestScope.Context.businessResults["LoadForward"]}'/>
							<c:if test="${loadfordward  eq 1}">
							<c:set var="recordLoanOfficerId" value="${sessionScope.UserContext.id}"/>
							<c:set var="recordOfficeId" value="${sessionScope.UserContext.branchId}"/>
							<script>
							document.getElementById("centerBlock1").style.display = "block";
							document.getElementById("loanOfficerBlock").style.display = "none";	
							document.getElementById("centerBlock2").style.display = "none";									
							</script>
							</c:if>   
							<c:if test="${loadfordward  != 1}">
							<c:set var="recordLoanOfficerId" value="${requestScope.Context.valueObject.loanOfficerId}"/>
							<c:set var="recordOfficeId" value="${requestScope.Context.valueObject.officeId}"/>
							</c:if> 
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
              <br>
            <br></td>
          </tr>
        </table>
</html-el:form>
</tiles:put>
</tiles:insert>
