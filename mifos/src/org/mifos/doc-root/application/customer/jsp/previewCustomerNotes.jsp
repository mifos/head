<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
 <tiles:put name="body" type="string">
 <script language="javascript">
  function goToEditPage(form){
	form.action="customerNotesAction.do?method=previous";
	form.submit();
  }
  
 function goToCancelPage(form){
	form.action="customerNotesAction.do?method=cancel";
	form.submit();
  }
  </script>
<html-el:form action="customerNotesAction.do?method=create">


      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead05">
			  <span class="fontnormal8pt">
	          	<customtags:headerLink/> 
	          </span>               
          </td>
        </tr>
      </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="70%" align="left" valign="top" class="paddingL15T15">            
            <table width="95%" border="0" cellpadding="3" cellspacing="0">
            <tr>
              <td width="83%" class="headingorange">
		<span class="heading">
		<c:out value="${sessionScope.customerNotesActionForm.customerName}"/>&nbsp;-
		</span> 
		<mifos:mifoslabel name="Customer.PreviewNote"></mifos:mifoslabel></td>
            </tr>
          </table>
            <table width="95%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td><br>
                      <span class="fontnormal">
			<mifos:mifoslabel name="Customer.ReviewText" ></mifos:mifoslabel>
			<mifos:mifoslabel name="Customer.Submit" ></mifos:mifoslabel>
			<mifos:mifoslabel name="Customer.Edit" ></mifos:mifoslabel>
			<mifos:mifoslabel name="Customer.ClickCancel1" />
	            <c:if test="${sessionScope.customerNotesActionForm.levelId == CustomerLevel.CENTER.value}">
					<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
				</c:if>
				<c:if test="${sessionScope.customerNotesActionForm.levelId == CustomerLevel.GROUP.value}">
					<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />
				</c:if>
				<c:if test="${sessionScope.customerNotesActionForm.levelId == CustomerLevel.CLIENT.value}">
					<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />
				</c:if>
			 <mifos:mifoslabel name="Customer.ClickCancel2" /> 
			</span></td>
                </tr>
                <tr>
                  <td class="blueline"><img src="../images/trans.gif" width="10" height="5"></td>
                </tr>
                <tr>
		   		<td>
   					<font class="fontnormalRedBold"><html-el:errors bundle="CustomerUIResources"/></font>
				</td>
				</tr>
              </table>
              <table width="95%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td align="left" valign="top"><img src="../images/trans.gif" width="10" height="5"></td>
                </tr>
                <tr>
                  <td align="left" valign="top">
			<span class="fontnormalbold">
			<c:out value="${sessionScope.customerNotesActionForm.commentDate}"/>
			</span><br>
                      <span class="fontnormal">
			<c:out value="${sessionScope.customerNotesActionForm.comment}"/>	
                      - <em><c:out value="${sessionScope.UserContext.name}"/></em></span> </td>
                </tr>
                <tr>
                  <td align="left" valign="top">
                  <html-el:button property="btn" style="width:65px;" styleClass="insidebuttn" onclick="goToEditPage(this.form)">
                  <mifos:mifoslabel name="Customer.EditLabel"></mifos:mifoslabel>
                  </html-el:button>
                  </td>
                </tr>
              </table>
              <table width="95%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td height="17" align="center" class="blueline">&nbsp;</td>
                </tr>
              </table>
              <br>
              <table width="95%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center">
                  
				<html-el:submit styleClass="buttn" style="width:70px;">
					<mifos:mifoslabel name="Customer.submit" />
				</html-el:submit>
                    &nbsp;&nbsp;
                    <html-el:button property="cancelBtn"  styleClass="cancelbuttn" style="width:70px" onclick="goToCancelPage(this.form)">
	                    <mifos:mifoslabel name="Customer.cancel" />
                    </html-el:button>
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

