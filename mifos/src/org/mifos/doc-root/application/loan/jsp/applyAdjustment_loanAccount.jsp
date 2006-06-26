<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/tags/date" prefix="date"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
	<script>
			function fun_cancel(form)
						{
							form.method.value="cancel";
							form.action="loanAction.do";
							form.submit();
						}
			function fun_submit(form)
					{
						form.method.value="";
						form.action="loanAction.do";
						form.submit();
					}
						
						
		</script>
	
<html-el:form method="post" action="/loanAction.do">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="470" align="left" valign="top" bgcolor="#FFFFFF">
					<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="heading">&nbsp;</td>
						</tr>
					</table>
					<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
						<tr>
							<td class="bluetablehead">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td width="25%">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/tick.gif" width="17" height="17"></td>
											<td class="timelineboldgray">
												<mifos:mifoslabel name="loan.select_client/group" />
											</td>
										</tr>
									</table>
									</td>
									<td width="25%" align="center">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17"></td>
											<td class="timelineboldorange">
												<mifos:mifoslabel name="loan.acc_info" />
											</td>
										</tr>
									</table>
									</td>
									<td width="25%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17"></td>
											<td class="timelineboldorangelight">
												<mifos:mifoslabel name="loan.review/edit_ins" />
											</td>
										</tr>
									</table>
									</td>
									<td width="25%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17"></td>
											<td class="timelineboldorangelight">
												<mifos:mifoslabel name="loan.review&submit" /></td>
										</tr>
									</table>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="70%" height="24" align="left" valign="top" class="paddingL15T15"><table width="96%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td width="70%" class="headingorange"><span class="heading">
                <mifos:mifoslabel name="loan.edu_loan"  />
                 </span><mifos:mifoslabel name="loan.apply_adjustment"  /></td>
                </tr>
            </table>
            <br>
            <table width="93%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td colspan="2" class="fontnormal">
                <mifos:mifoslabel name="loan.lastpaymentdescription"  />
                
                <br>                  
                  <br>
				<mifos:mifoslabel name="loan.lastpaymentamount"  />
                  <br>
                  <br>
              <%--    <html-el:checkbox property="lastPayment">
                  <mifos:mifoslabel name="loan.checkboxpaymentvalue"  />
                  </html-el:checkbox>  --%> 
                  <br>
<br></td>
              </tr>
              <tr>
                <td width="5%" valign="top" class="fontnormal">
                
                
                <mifos:mifoslabel name="loan.notes"  />:
                
                 <br>
                  </td>
                <td width="95%" class="fontnormal">
                
      <%--          <html-el:textarea property="paymentNotes" style="width:320px; height:110px;"></html-el:textarea>--%></td> 
              </tr>
            </table>
            <table width="750" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center" class="blueline">&nbsp;                </td>
              </tr>
            </table>            <br>
            <table width="95%" border="0" cellspacing="0" cellpadding="1">
              <tr>
                <td align="center">
                
                
                <html-el:submit styleClass="buttn" style="width:75px;" onclick="javascript:fun_submit(this.form)">
											<mifos:mifoslabel name="loan.submit" />
										</html-el:submit> &nbsp; 
										
										<html-el:cancel styleClass="cancelbuttn" style="width:70px;" onclick="javascript:fun_cancel(this.form)">
											<mifos:mifoslabel name="loan.cancel" />
										</html-el:cancel>
                     
                
                
                </td>
              </tr>
            </table></td>
        </tr>
      </table>      <br></td>
  </tr>
</table>
<br>
</html-el:form>
</tiles:put>
</tiles:insert>
