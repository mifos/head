<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
 <tiles:put name="body" type="string">
<html-el:form action="notesAction.do">
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
							<c:out value="${sessionScope.customerNotesActionForm.customerName}"/> &nbsp;- 
						</span>
						<mifos:mifoslabel name="Customer.addnoteheading"></mifos:mifoslabel>
					</td>
              		<td width="17%" align="right" class="fontnormal">
						<a href="customerNotesAction.do?method=load&customerId=<c:out value="${sessionScope.customerNotesActionForm.customerId}"/>">
						<mifos:mifoslabel name="Customer.addnoteheading" ></mifos:mifoslabel></a>
				 	</td>
            	</tr>
            	<tr>
					<logic:messagesPresent>
					<td><br><font class="fontnormalRedBold"><html-el:errors
							bundle="accountsUIResources" /></font></td>
						</logic:messagesPresent>
				</tr>
            	
          	</table>
            <br>
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td>
                	<mifos:mifostabletagdata name="customerNote" key="allnotes" type="single" 
       					width="95%" border="0" cellspacing="0" cellpadding="0"/>
                </td>
              </tr>
            </table>
            
            <br>
          </td>
        </tr>
      </table>
      <br>
	  <mifos:SecurityParam property="${param.securityParamInput}" />
	  <html-el:hidden property="securityParamInput" value="${param.securityParamInput}" />
    </html-el:form>
</tiles:put>
</tiles:insert>
