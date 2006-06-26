<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
 <tiles:put name="body" type="string">
 <SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
<script language="javascript">
  function goToEditPage(){
	customerNoteActionForm.action="CustomerNoteAction.do?method=previous";
	customerNoteActionForm.submit();
  }
  
  function goToCancelPage(){
	customerNoteActionForm.action="CustomerNoteAction.do?method=cancel";
	customerNoteActionForm.submit();
  }
</script>

<html-el:form action="CustomerNoteAction.do?method=create" onsubmit="func_disableSubmitBtn('submitBtn')"> 
      <c:choose>
  	<c:when test="${sessionScope.customerNoteActionForm.input eq 'Client'}">
	  <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td class="bluetablehead05">
	            <span class="fontnormal8pt">
	            	<a href="CustomerSearchAction.do?method=getOfficeHomePage&officeId=<c:out value="${sessionScope.linkValues.customerOfficeId}"/>&officeName=<c:out value="${sessionScope.linkValues.customerOfficeName}"/>&loanOfficerId=<c:out value="${requestScope.Context.userContext.id}"/>">
	            	<c:out value="${sessionScope.linkValues.customerOfficeName}"/></a> 
					 /
	            </span>
	            <c:if test="${!empty sessionScope.linkValues.customerCenterName}">
	               <span class="fontnormal8pt">
	               	<a href="centerAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.customerCenterGCNum}"/>">
				       	<c:out value="${sessionScope.linkValues.customerCenterName}"/>
			       	</a>  /  </span>
		    	</c:if>
	           <c:if test="${!empty sessionScope.linkValues.customerParentName}">
	               <span class="fontnormal8pt">
	               	<a href="GroupAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.customerParentGCNum}"/>">
				       	<c:out value="${sessionScope.linkValues.customerParentName}"/>
			       	</a>  /  </span>
		    	</c:if>
	            <!-- Name of the client -->
	            <span class="fontnormal8pt">
	            	<a href="clientCreationAction.do?method=get&customerId=<c:out value="${sessionScope.linkValues.customerId}"/>">
	            	<c:out value="${sessionScope.linkValues.customerName}"/>
	            	</a>
	            </span>
            </td>
          </tr>
        </table>
	  </c:when>
	  <c:when test="${sessionScope.customerNoteActionForm.input eq 'Center'}">
	  <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td class="bluetablehead05">
	            <span class="fontnormal8pt">
	            	<a href="CustomerSearchAction.do?method=getOfficeHomePage&officeId=<c:out value="${sessionScope.linkValues.customerOfficeId}"/>&officeName=<c:out value="${sessionScope.linkValues.customerOfficeName}"/>&loanOfficerId=<c:out value="${requestScope.Context.userContext.id}"/>">
	            	<c:out value="${sessionScope.linkValues.customerOfficeName}"/></a> 
					 /
	            </span>
	            
	               <span class="fontnormal8pt">
	               	<a href="centerAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.globalCustNum}"/>">
				       	<c:out value="${sessionScope.linkValues.customerName}"/>
			       	</a> 
	            </span>
            </td>
          </tr>
        </table>
	  </c:when>
	  <c:otherwise>
	  <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead05">
          <span class="fontnormal8pt">
          <a href="CustomerSearchAction.do?method=getOfficeHomePage&officeId=<c:out value="${sessionScope.linkValues.customerOfficeId}"/>&officeName=<c:out value="${sessionScope.linkValues.customerOfficeName}"/>&loanOfficerId=<c:out value="${requestScope.Context.userContext.id}"/>">
	           <c:out value="${sessionScope.linkValues.customerOfficeName}"/>            	
           </a> /
           	<c:if test="${!empty sessionScope.linkValues.customerParentName}">
               	<a href="centerAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.customerParentGCNum}"/>">
			       	<c:out value="${sessionScope.linkValues.customerParentName}"/>
		       	</a> /  
		    </c:if>
          <a href="GroupAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.globalCustNum}"/>">
          	<c:out value="${sessionScope.linkValues.customerName}"/>
           </a>
           </span></td>
        </tr>
      </table>
	  </c:otherwise>
	  </c:choose>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td align="left" valign="top" class="paddingL15T15" >              
          <table width="95%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td width="83%" class="headingorange"><span class="heading">
                  <c:out value="${sessionScope.linkValues.customerName}"/>
                   - </span> 
                  <mifos:mifoslabel name="label.previewnote" bundle="CustomerUIResources"></mifos:mifoslabel>
                  
                  </td>
                </tr>
              </table>
              <table width="95%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td><br>
                      <span class="fontnormal">
                      <mifos:mifoslabel name="label.noteMsg1" bundle="CustomerUIResources"></mifos:mifoslabel>
                      <mifos:mifoslabel name="label.noteMsg2" bundle="CustomerUIResources"></mifos:mifoslabel>
                      <mifos:mifoslabel name="label.noteMsg3" bundle="CustomerUIResources"></mifos:mifoslabel>
                      <mifos:mifoslabel name="label.noteMsg4" bundle="CustomerUIResources"></mifos:mifoslabel>
                      </span></td>
                </tr>
                <tr>
		   		<td>
   					<font class="fontnormalRedBold"><html-el:errors bundle="CustomerUIResources"/></font>
				</td>
				</tr>
                <tr>
                  <td class="blueline"><img src="pages/framework/images/trans.gif" width="10" height="12"></td>
                </tr>
              </table>              <br>
              <table width="95%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="left" valign="top" width="50%">
                  <span class="fontnormalbold">
                  	<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.CustomerNoteVO.commentDate)}"/>
                  </span><br>
				  <span class="fontnormal">
				  	<c:out value="${requestScope.CustomerNoteVO.comment}"/>
                		- <em><c:out value="${requestScope.CustomerNoteVO.personnelName}"/></em>
                  </span>
                  </td>
                </tr>
                <tr>
                  <td align="left" valign="top"><img src="pages/framework/images/trans.gif" width="10" height="6"></td>
                </tr>
                <tr>
                  <td align="left" valign="top">
                 	<html-el:button property="btn" style="width:65px;" styleClass="insidebuttn" onclick="goToEditPage()">Edit</html-el:button>
                  </td>
                </tr>
              </table>
              <table width="95%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td height="20" align="center" class="blueline">&nbsp;</td>
                </tr>
              </table>
              <br>
              <table width="95%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center">
				 <html-el:submit styleClass="buttn" property="submitBtn" style="width:70px;" >
		               	<mifos:mifoslabel name="button.submit" bundle="CustomerUIResources"></mifos:mifoslabel>
               	 </html-el:submit>
                    &nbsp;&nbsp;
                 <html-el:button property="cancelBtn"  styleClass="cancelbuttn" style="width:70px" onclick="goToCancelPage()">
	                    <mifos:mifoslabel name="button.cancel" bundle="CustomerUIResources"></mifos:mifoslabel>
                 </html-el:button>
			              
				  </td>
				</tr>
              </table>
              <br>
              <br>
          </td>
        </tr>
      </table>
 
</html-el:form>
</tiles:put>
</tiles:insert>
