<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
<tiles:put name="body" type="string">
<script language="javascript">
  function goToCancelPage(){
	customerHistoricalDataActionForm.action="CustomerHistoricalDataAction.do?method=cancel";
	customerHistoricalDataActionForm.submit();
  }
</script>
<html-el:form action="CustomerHistoricalDataAction.do?method=get">

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
<c:out value="${sessionScope.linkValues.customerName}"/>  - </span>
<mifos:mifoslabel name="label.historicaldata" bundle="CustomerUIResources"></mifos:mifoslabel></td>
                  <td width="42%" align="right" class="fontnormal">
                  
	<html-el:link action="CustomerHistoricalDataAction.do?method=load">
		<mifos:mifoslabel name="label.add_edit_hd" bundle="CustomerUIResources"></mifos:mifoslabel>
	</html-el:link>	
	</td>
                </tr>
            <tr>
	   		<td colspan="2">
   				<font class="fontnormalRedBold"><html-el:errors bundle="CustomerUIResources"/></font>
			</td>
			</tr>
                
              </table>
              <br>
              <table width="95%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="left" valign="top" class="fontnormalbold"><span class="fontnormal"></span>

		<mifos:mifoslabel name="label.MFIjoiningdate" bundle="CustomerUIResources"></mifos:mifoslabel>		
		<span class="fontnormal"> 
		<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.CustomerHistoricalDataVO.mfiJoiningDate)}" />
		 <br></span>
		<mifos:mifoslabel name="${ConfigurationConstants.LOAN}"/>
		<mifos:mifoslabel name="label.loancyclenumber" bundle="CustomerUIResources"></mifos:mifoslabel>			
		<span class="fontnormal"> <c:out value="${requestScope.CustomerHistoricalDataVO.loanCycleNumber}"/><br></span>

		<mifos:mifoslabel name="label.productname" bundle="CustomerUIResources"></mifos:mifoslabel>			
		<span class="fontnormal"> <c:out value="${requestScope.CustomerHistoricalDataVO.productName}"/><br></span>

		<mifos:mifoslabel name="label.amountof" bundle="CustomerUIResources"/><mifos:mifoslabel name="${ConfigurationConstants.LOAN}"/><mifos:mifoslabel name="label.colon" bundle="CustomerUIResources"/>			
		<span class="fontnormal"> <c:out value="${requestScope.CustomerHistoricalDataVO.loanAmount}"/><br></span>

		<mifos:mifoslabel name="label.totalamountpaidLabel" bundle="CustomerUIResources"/><mifos:mifoslabel name="label.colon" bundle="CustomerUIResources"/>
		<span class="fontnormal"><c:out value="${requestScope.CustomerHistoricalDataVO.totalAmountPaid}"/><br></span>
		<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}"/>
		<mifos:mifoslabel name="label.interestpaidLabel" bundle="CustomerUIResources"/><mifos:mifoslabel name="label.colon" bundle="CustomerUIResources"/>			
		<span class="fontnormal"> <c:out value="${requestScope.CustomerHistoricalDataVO.interestPaid}"/><br></span>

		<mifos:mifoslabel name="label.numberofmissedpayments" bundle="CustomerUIResources"/><mifos:mifoslabel name="label.colon" bundle="CustomerUIResources"/>			
		<span class="fontnormal"> <c:out value="${requestScope.CustomerHistoricalDataVO.missedPaymentsCount}"/><br></span>

		<mifos:mifoslabel name="label.totalnumberofpayments" bundle="CustomerUIResources"/><mifos:mifoslabel name="label.colon" bundle="CustomerUIResources"/>			
		<span class="fontnormal"><c:out value="${requestScope.CustomerHistoricalDataVO.totalPaymentsCount}"/><br> <br></span>

		<mifos:mifoslabel name="label.notes" bundle="CustomerUIResources"></mifos:mifoslabel>:			
		<span class="fontnormal"><c:out value="${requestScope.CustomerHistoricalDataVO.notes}"/><br></span>
                  </td>
                </tr>
              </table>
              <table width="96%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center" class="blueline">&nbsp; </td>
                </tr>
              </table>
              <br>
              <table width="96%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center">
                  
		<html-el:button property="btn" styleClass="buttn" style="width:135px" onclick="goToCancelPage()">
			<mifos:mifoslabel name="label.backtodetailspage" bundle="CustomerUIResources"></mifos:mifoslabel>	
		</html-el:button >
                  </td>
                </tr>
              </table>              <br>
              <br>
              <br>
          </td>
        </tr>
      </table>
<mifos:SecurityParam property="${sessionScope.customerHistoricalDataActionForm.input}"></mifos:SecurityParam>
</html-el:form>
</tiles:put>
</tiles:insert>