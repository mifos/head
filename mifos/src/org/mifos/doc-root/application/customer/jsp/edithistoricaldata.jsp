<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@taglib uri="/tags/date" prefix="date"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
 <tiles:put name="body" type="string">
<script language="javascript" SRC="pages/framework/js/date.js"></script>
<script language="javascript">
  function goToCancelPage(){
	customerHistoricalDataActionForm.action="CustomerHistoricalDataAction.do?method=cancel";
	customerHistoricalDataActionForm.submit();
  }
 </script>


<html-el:form action="CustomerHistoricalDataAction.do?method=preview" onsubmit="return (validateMyForm(mfiJoiningDate,mfiJoiningDateFormat,mfiJoiningDateYY))">
      <c:choose>
  	<c:when test="${sessionScope.customerHistoricalDataActionForm.input eq 'Client'}">
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
	  <c:otherwise>
	  <table width="95%" border="0" cellpadding="0" cellspacing="0">
	        <tr>
	          <td class="bluetablehead05">
	          <span class="fontnormal8pt">
	           <a href="CustomerSearchAction.do?method=getOfficeHomePage&officeId=<c:out value="${sessionScope.linkValues.customerOfficeId}"/>&officeName=<c:out value="${sessionScope.linkValues.customerOfficeName}"/>&loanOfficerId=<c:out value="${requestScope.Context.userContext.id}"/>">
	            	<c:out value="${sessionScope.linkValues.customerOfficeName}"/></a> 
					 /
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
                  <td width="41%" class="headingorange"><span class="heading">
	<c:out value="${sessionScope.linkValues.customerName}"/>    - </span>
	<mifos:mifoslabel name="label.edithistoricaldata" bundle="CustomerUIResources"></mifos:mifoslabel></td>
                </tr>
               <tr>
	   		<td >
   				<font class="fontnormalRedBold"><html-el:errors bundle="CustomerUIResources"/></font>
			</td>
			</tr>
                
              </table>
              <br>
              <table width="95%" border="0" cellpadding="3" cellspacing="0">
                <tr class="fontnormal">
                  <td width="27%" align="right">
                  <mifos:mifoslabel name="label.MFIjoiningdate" bundle="CustomerUIResources"></mifos:mifoslabel></td>
                  <td width="73%"> 
                  <date:datetag property="mfiJoiningDate" name="CustomerHistoricalDataVO"/>
                  
		  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right"><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /><mifos:mifoslabel name="label.loancyclenumber" bundle="CustomerUIResources"></mifos:mifoslabel></td>
                  <td>
                  	<mifos:mifosnumbertext  name = "customerHistoricalDataActionForm" property="loanCycleNumber" value="${requestScope.CustomerHistoricalDataVO.loanCycleNumber}"/>
				  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right"><mifos:mifoslabel name="label.productname" bundle="CustomerUIResources"></mifos:mifoslabel></td>
                  <td>
                  	<mifos:mifosalphanumtext name = "customerHistoricalDataActionForm" property="productName" value="${requestScope.CustomerHistoricalDataVO.productName}"/>
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right"><mifos:mifoslabel name="label.amountof" bundle="CustomerUIResources"/>
                  <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
                  <mifos:mifoslabel name="label.colon" bundle="CustomerUIResources"/>
                  </td>
                  <td>
                  	<mifos:mifosdecimalinput  name = "customerHistoricalDataActionForm" property="loanAmount" value="${requestScope.CustomerHistoricalDataVO.loanAmount}"  />
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right"><mifos:mifoslabel name="label.totalamountpaid" bundle="CustomerUIResources"></mifos:mifoslabel></td>
                  <td>
                  	<mifos:mifosdecimalinput  name = "customerHistoricalDataActionForm" property="totalAmountPaid" value="${requestScope.CustomerHistoricalDataVO.totalAmountPaid}" />
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right"><mifos:mifoslabel name="${ConfigurationConstants.INTEREST}"/>
                  <mifos:mifoslabel name="label.interestpaid" bundle="CustomerUIResources"></mifos:mifoslabel></td>
                  <td>
                  	<mifos:mifosdecimalinput  name = "customerHistoricalDataActionForm" property="interestPaid" value="${requestScope.CustomerHistoricalDataVO.interestPaid}" />
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right"><mifos:mifoslabel name="label.numberofmissedpayments" bundle="CustomerUIResources"></mifos:mifoslabel></td>
                  <td>
                  	<mifos:mifosnumbertext  name = "customerHistoricalDataActionForm" property="missedPaymentsCount" value="${requestScope.CustomerHistoricalDataVO.missedPaymentsCount}"/>
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right"><mifos:mifoslabel name="label.totalnumberofpayments" bundle="CustomerUIResources"></mifos:mifoslabel></td>
                  <td>
                  	<mifos:mifosnumbertext  name = "customerHistoricalDataActionForm" property="totalPaymentsCount" value="${requestScope.CustomerHistoricalDataVO.totalPaymentsCount}"/>
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right" valign="top">
                  	<mifos:mifoslabel name="label.notes" bundle="CustomerUIResources"></mifos:mifoslabel>:</td>
                  <td>
                    <span class="fontnormal">
                  			<html-el:textarea property="notes" cols="37" style="width:320px; height:110px;" value="${requestScope.CustomerHistoricalDataVO.notes}"/>
 		 		    </span>
		 		  </td>
                </tr>
              </table>
              <table width="95%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center" class="blueline">&nbsp;</td>
                </tr>
              </table>
              <br>
              <table width="95%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center">
					<html-el:submit styleClass="buttn" style="width:70px;" >
	                  	<mifos:mifoslabel name="button.preview" bundle="CustomerUIResources"></mifos:mifoslabel>
                  	</html-el:submit>
                    &nbsp;&nbsp;
                    <html-el:button property="cancelBtn"  styleClass="cancelbuttn" style="width:70px" onclick="goToCancelPage()">
	                    <mifos:mifoslabel name="button.cancel" bundle="CustomerUIResources"></mifos:mifoslabel>
                    </html-el:button>
                  </td>
                </tr>
              </table>              <br>
            </td>
        </tr>
      </table>
<html-el:hidden property="customerId" value="${sessionScope.linkValues.customerId}"/>
<%--<html-el:hidden property="notes" value="${requestScope.CustomerHistoricalDataVO.notes}"/>--%>
<html-el:hidden property="historicalId" value="${requestScope.CustomerHistoricalDataVO.historicalId}"/>
<html-el:hidden property="versionNo" value="${requestScope.CustomerHistoricalDataVO.versionNo}"/>
</html-el:form>
</tiles:put>
</tiles:insert>
