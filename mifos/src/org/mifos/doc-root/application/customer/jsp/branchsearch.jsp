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
            <td width="70%" height="24" align="left" valign="top" class="paddingL10">
            <table width="96%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td class="headingorange">
 <c:out value='${requestScope.Context.businessResults["Office"]}'/>                 
                  <br>
                  </span><span class="fontnormalbold">
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
                      <td width="40%" align="left" valign="top" class="fontnormal"><table width="100%" border="0" cellspacing="0" cellpadding="4">
                        <tr class="fontnormal">
                          <td width="100%" colspan="2" class="bglightblue"><span class="heading">
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
                      </table></td>
                      <td width="12%" align="center" valign="top" class="headingorange">
                      <mifos:mifoslabel name="CustomerSearch.or"/>&nbsp;
                      </td>
                      <td width="48%" align="left" valign="top" class="fontnormal">
                      <table width="100%" border="0" cellspacing="0" cellpadding="4">
                        <tr class="fontnormal">
                          <td width="100%" colspan="2" class="bglightblue"><span class="heading">
							<mifos:mifoslabel name="CustomerSearch.select"/>&nbsp;
						 loan officer</span></td>
                        </tr>
                      </table>    
<html-el:hidden property="method" value="search"/>
<html-el:hidden property="loanOfficerId" value=""/> 
<html-el:hidden property="officeName" value='${requestScope.Context.businessResults["Office"]}'/>													
<c:choose>
<c:when test='${sessionScope.UserContext.officeLevelId==5}'>
<html-el:hidden property="officeId" value="${sessionScope.UserContext.branchId}"/>
<html-el:hidden property="searchNode(search_officeId)" value="${sessionScope.UserContext.branchId}"/> 
</c:when>
<c:otherwise>
<html-el:hidden property="searchNode(search_officeId)" value="0"/> 
</c:otherwise>
</c:choose>	                                                           
                        <div id="Layer2" style="border: 1px solid #CECECE; height:100px; width:100%; overflow: auto; padding:6px; margin-top:5px;"> 
<c:forEach items='${requestScope.Context.businessResults["LoanOfficerslist"]}' var="loanOfficer">
<html-el:link href="javascript:fnGet(${loanOfficer.personnelId})" >
<c:out value="${loanOfficer.displayName}"/>
</html-el:link><br>
</c:forEach>                  
 </div></td>
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