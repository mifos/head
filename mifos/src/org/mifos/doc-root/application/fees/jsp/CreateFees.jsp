<!--
/**

* CreateFees.jsp    version: 1.0



* Copyright (c) 2005-2006 Grameen Foundation USA

* 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

* All rights reserved.



* Apache License
* Copyright (c) 2005-2006 Grameen Foundation USA
*

* Licensed under the Apache License, Version 2.0 (the "License"); you may
* not use this file except in compliance with the License. You may obtain
* a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*

* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and limitations under the

* License.
*
* See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

* and how it is applied.

*

*/
 -->



<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el" %>

<tiles:insert definition=".create">
<tiles:put name="body" type="string">


<script language="JavaScript"  type="text/javascript">
function meetingpopup(){
	window.open("schedulemeetingpopup.htm",null,"height=400,width=800,status=yes,scrollbars=yes,toolbar=no,menubar=no,location=no");
}
function showMeetingFrequency()
{
			
		if (document.forms[0].frequency[0].checked == true)
		{			
			document.getElementById("weekDIV").style.display = "block";
			document.getElementById("monthDIV").style.display = "none";	
		}		 
		else if(document.forms[0].frequency[1].checked == true)
		{			
			document.getElementById("weekDIV").style.display = "none";
			document.getElementById("monthDIV").style.display = "block";		
		}
}
function showSchedule()
{	
		if (document.forms[0].typeId[0].checked == true ){	
			
			document.getElementById("scheduleDIV").style.display = "block";
			document.getElementById("timeofchargeDiv").style.display = "none";
		}	
		else if (document.forms[0].typeId[1].checked == true)
		{	
			document.getElementById("scheduleDIV").style.display = "none";
			document.getElementById("timeofchargeDiv").style.display = "block";		
		}
}

function showRate()
{	
		if (document.getElementById("categoryId").value == 5){				
			document.getElementById("rateDiv").style.display = "block";
			document.getElementById("rateDivHeading").style.display = "block";
		}
		else{
			document.forms[0].rate.value="";
            document.forms[0].formulaId.value="";
			document.getElementById("rateDiv").style.display = "none";
			document.getElementById("rateDivHeading").style.display = "none";
		}
}

function showPayment()
{	
		if (document.getElementById("categoryId").value == 5){				
			document.getElementById("loanCategoryDiv").style.display = "block";
			document.getElementById("otherCategoryDiv").style.display = "none";
			document.forms[0].otherPaymentId.value="";
		}
		else{
			document.getElementById("loanCategoryDiv").style.display = "none";
			document.getElementById("otherCategoryDiv").style.display = "block";
			document.forms[0].loanPaymentId.value="";
		}
}

function showAdminBox(){
		if (document.getElementById("categoryId").value == 5){				
			document.forms[0].adminCheck.disabled=true;
		}
		else{
			document.forms[0].adminCheck.disabled=false;
		}
}


function fnCancel(form)
{
	form.method.value="load";
	form.action="AdminAction.do";
	form.submit();
}
function fnSubmit(form)
{
	form.method.value="preview";
    form.input.value="createFees";
	form.action="feesAction.do";
	form.submit();
}

</script>

<html-el:form action="/feesAction.do?method=preview&input=createFees" focus="feeName">



<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td height="350" align="left" valign="top" bgcolor="#FFFFFF">
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
                  <td width="27%">
                    <table border="0" cellspacing="0" cellpadding="0">
                      <tr>
                        <td>
                        <img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17">
                        </td>
                        <td class="timelineboldorange">
                        <mifos:mifoslabel name="Fees.feeinformation" bundle="FeesUIResources"></mifos:mifoslabel>
                        </td>
                      </tr>
                    </table>
                  </td>

                  <td width="73%" align="right">
                    <table border="0" cellspacing="0" cellpadding="0">
                      <tr>
                        <td>
                        <img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17">
                        </td>
                        <td class="timelineboldorangelight">
                        <mifos:mifoslabel name="Fees.review" bundle="FeesUIResources"></mifos:mifoslabel>
                        &amp;
                        <mifos:mifoslabel name="Fees.submit" bundle="FeesUIResources"></mifos:mifoslabel>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table></td>
          </tr>
         

        </table>

        <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">

          <tr>
		    
            <td align="left" valign="top" class="paddingleftCreates">
			   
               <table width="93%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td class="headingorange">
                  	<span class="heading">
                  	<mifos:mifoslabel name="Fees.definenewfee" bundle="FeesUIResources"></mifos:mifoslabel> -
				 	</span>
				 	<mifos:mifoslabel name="Fees.enterfeeinformation" bundle="FeesUIResources"></mifos:mifoslabel>
				  </td>
                </tr>

                <tr>
                  <td class="fontnormal">
	                  <mifos:mifoslabel name="Fees.CreateFeesInstruction" bundle="FeesUIResources"></mifos:mifoslabel>
					  <mifos:mifoslabel name="Fees.CreateFeesCancelInstruction" bundle="FeesUIResources"></mifos:mifoslabel>
        	          <br><mifos:mifoslabel name="Fees.CreateFeesFieldInstruction" mandatory="yes" bundle="FeesUIResources"></mifos:mifoslabel>
                  </td>
                </tr>
              </table>
              <br>
              <table width="93%" border="0" cellpadding="3" cellspacing="0">
				<font class="fontnormalRedBold"><html-el:errors bundle="FeesUIResources" /> </font>
                <tr>
                  <td colspan="2" class="fontnormalbold">
                  	<mifos:mifoslabel name="Fees.feedetails" bundle="FeesUIResources"></mifos:mifoslabel>
                  	<br>
                  	<br>
                  </td>
                </tr>

                <tr class="fontnormal">
                  <td width="27%" align="right">
                  	
                  	<mifos:mifoslabel name="Fees.feename" mandatory="yes" bundle="FeesUIResources"></mifos:mifoslabel>
                  	
				  </td>

                  <td width="73%" valign="top">
				  
				  
                  
                  <mifos:mifosalphanumtext property="feeName"/>
                  </td>

                </tr>

                <tr class="fontnormal">
                  <td align="right">
                  		<mifos:mifoslabel name="Fees.feeappliesto" mandatory="yes" bundle="FeesUIResources"></mifos:mifoslabel>
                 	</td>
                  	<td valign="top">
						<c:set var="catagoryList" scope="request" value="${requestScope.catagory.lookUpMaster}" />
						<mifos:select  property="categoryId" style="width:136px;" onchange="showRate();showPayment();showAdminBox();">
					    	<html-el:options property="id"
											labelProperty="lookUpValue"
											collection="catagoryList"  />
						</mifos:select>
		  			</td>
            	</tr>
				<tr class="fontnormal">
                  <td align="right" valign="top"><mifos:mifoslabel name="Fees.defaultfees" bundle="FeesUIResources"></mifos:mifoslabel></td>
                  <td valign="top"><html-el:checkbox name="FeesActionForm" property="adminCheck"  value="Yes"/>
				  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right" valign="top">
           	      	 <mifos:mifoslabel name="Fees.frequency" bundle="FeesUIResources" mandatory="yes"></mifos:mifoslabel>
       	          	
                  </td>

                <td valign="top">

	                <html-el:radio name="FeesActionForm" property="typeId" value="1"  onclick="showSchedule();">
	                	<mifos:mifoslabel name="Fees.periodic" bundle="FeesUIResources"></mifos:mifoslabel>
        	        </html-el:radio>

                	<br>
					<html-el:radio  name="FeesActionForm" property="typeId" value="2"  onclick="showSchedule();">
						<mifos:mifoslabel name="Fees.onetime" bundle="FeesUIResources"></mifos:mifoslabel>
	                </html-el:radio>
                </td>
                </tr>
			
				

                <tr class="fontnormal">
                  <td align="right" valign="top">&nbsp;</td>
                  <td valign="top">

				  <div id="timeofchargeDiv" style="display:none">
				  	<mifos:mifoslabel name="Fees.selecttimeofcharge" bundle="FeesUIResources"></mifos:mifoslabel>
					<br>

					
				    <div id="otherCategoryDiv" style="display:none">
						<c:set var="paymentListLoan" scope="request" value='${requestScope.Context.businessResults["feePaymentForLoan"]}' />
						<mifos:select name="FeesActionForm" property="otherPaymentId"  style="width:180px;">
					    	<html-el:options property="feePaymentId"
											labelProperty="feePaymentName"
											collection="paymentListLoan" />
						</mifos:select>
				   </div>
				   <div id="loanCategoryDiv" style="display:none">
				   <c:set var="paymentList" scope="request" value="${requestScope.payment.lookUpMaster}" />
						<mifos:select name="FeesActionForm" property="loanPaymentId" style="width:180px;">
					    	<html-el:options property="id"
											labelProperty="lookUpValue"
											collection="paymentList" />
						</mifos:select>
					</div>

				  </div>
                  <div id="scheduleDIV" style="display:none;">
				    <table width="90%" border="0" cellpadding="3" cellspacing="0">
                      <tr class="fontnormal">
                        <td align="left" valign="top" style="border-top: 1px solid #CECECE; border-left: 1px solid #CECECE; border-right: 1px solid #CECECE;">
                        <table width="98%" border="0" cellspacing="0" cellpadding="2">
                            <tr class="fontnormal">

                                <td width="49%">
                               	<html-el:radio name="FeesActionForm" property="frequency"  value="1" onclick="showMeetingFrequency();" >
    	    	        		<mifos:mifoslabel name="Fees.weekly" bundle="FeesUIResources"></mifos:mifoslabel>
        	        			</html-el:radio>
        	        			</td>

								<td width="49%">
                               	<html-el:radio name="FeesActionForm" property="frequency"  value="2" onclick="showMeetingFrequency();" >
    	    	        		<mifos:mifoslabel name="Fees.monthly" bundle="FeesUIResources"></mifos:mifoslabel>
        	        			</html-el:radio>
        	        			</td>

                            </tr>
                          </table>
                        </td>
                      </tr>

                      <tr class="fontnormal">
                        <td width="59%" align="left" valign="top" style="border: 1px solid #CECECE;">

                          <div id="weekDIV" style="height:40px; width:380px; display:block;">
							<mifos:mifoslabel
										name="meeting.labelRecurWeeks" bundle="FeesUIResources" />
									<table border="0" cellspacing="0" cellpadding="2">
										<tr class="fontnormal">
											<td colspan="4"><mifos:mifoslabel
												name="meeting.labelRecurEvery" bundle="FeesUIResources" />


											<mifos:mifosnumbertext
														property="recurWeekDay" size="3" maxValue="32767" minValue="1"/> <mifos:mifoslabel
														name="meeting.labelWeeks" bundle="FeesUIResources" /></td>
										</tr>
									</table>
                            </div>

							
                            <div id="monthDIV" style="height:40px; width:380px; display:none;">
                            <mifos:mifoslabel
										name="meeting.labelRecurMonths" bundle="FeesUIResources" />
									<br>
									<table border="0" cellspacing="0" cellpadding="2">
												<tr class="fontnormal">
													<td><mifos:mifoslabel
												name="meeting.labelRecurEvery" bundle="FeesUIResources" /> <mifos:mifosnumbertext
														property="recurMonthDay" size="3" maxValue="32767" minValue="1"/><mifos:mifoslabel
												name="meeting.labelMonths" bundle="FeesUIResources" /></td>
												</tr>
									</table>
							</div>
                        </td>
                      </tr>
                    </table>
                  </div>
				  </td>
                </tr>
				
				
				
				
				<tr class="fontnormal">
                 <td colspan="2" valign="top" class="fontnormalbold">
					 <mifos:mifoslabel name="Fees.feecalculation" bundle="FeesUIResources"></mifos:mifoslabel>
                  	<br>
                  	<br>
                  </td>
                </tr>

                <tr class="fontnormal">

                  <td align="right">
                  	<mifos:mifoslabel name="Fees.enteramount" mandatory="yes" bundle="FeesUIResources"></mifos:mifoslabel>
                  </td>

                  <td valign="top">
                	  <mifos:mifosdecimalinput property="amount" ></mifos:mifosdecimalinput>
                  </td>

                </tr>

                <tr class="fontnormal">

                  <td align="right" valign="center" style="padding-top:25px;">
				    <div id="rateDivHeading" style="display:none">
                    <table border="0" cellspacing="0" cellpadding="0">  
					  <tr class="fontnormal">
						<td colspan="3">
                  			<mifos:mifoslabel name="Fees.calculatefeeas" mandatory="yes" bundle="FeesUIResources"></mifos:mifoslabel>
                        </td>
                      </tr>
                    </table>
					</div>
                  </td>

                  
                  <td valign="top">
				   <div id="rateDiv" style="display:none">
                    <table width="90%" border="0" cellspacing="0" cellpadding="0">
                      <tr class="fontnormal">
                        <td colspan="3">
                        <mifos:mifoslabel name="Fees.or" bundle="FeesUIResources"></mifos:mifoslabel>
                        </td>
                      </tr>

                      <tr class="fontnormal">
                        <td colspan="3">
						<img src="pages/framework/images/trans.gif" width="5" height="5">
						</td>
                      </tr>

                      <tr class="fontnormal">
                        <td width="16%">
                        <mifos:mifosdecimalinput property="rate" size="3" decimalFmt="10.5"></mifos:mifosdecimalinput>
                        <mifos:mifoslabel name="Fees.percentof" bundle="FeesUIResources"></mifos:mifoslabel>
                        <c:set var="formulaList" scope="request" value="${requestScope.formula.lookUpMaster}" />
						<mifos:select property="formulaId" style="width:136px;" >
					    	<html-el:options property="id"
											labelProperty="lookUpValue"
											collection="formulaList" />
						</mifos:select>
						
                        </td>

                        <td width="17%">&nbsp;</td>
                      </tr>
                  </table>
				  </div>
                 </td>
				 

                </tr>

                <tr class="fontnormal">
                  <td align="right">&nbsp;</td>
                  <td valign="top">&nbsp;</td>
                </tr>

                <tr class="fontnormal">
                  <td colspan="2" class="fontnormalbold">
                  	<mifos:mifoslabel name="Fees.accounting" bundle="FeesUIResources"></mifos:mifoslabel>
                  	<br>
                  	<br>
                  </td>
                </tr>

                <tr class="fontnormal">

                  <td align="right">
					<mifos:mifoslabel name="Fees.GLCode" mandatory="yes" bundle="FeesUIResources"></mifos:mifoslabel>
                  </td>

                  <td valign="top">
					<mifos:select name="FeesActionForm" property="glCodeEntity.glcodeId"  style="width:136px;">
						<html-el:options collection="glCodeList" property="glcodeId" labelProperty="glcode"></html-el:options> 
					</mifos:select>
                  </td>
               </tr>
              </table>

              <table width="93%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center" class="blueline">&nbsp;</td>
                </tr>
              </table>
              <br>

              <table width="93%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center">
                <html-el:submit property="preview" styleClass="buttn" style="width:70px">
	                    <mifos:mifoslabel name="Fees.preview" bundle="FeesUIResources"></mifos:mifoslabel> 
                   </html-el:submit>


                    &nbsp;
                    <html-el:button property="cancel" styleClass="cancelbuttn" style="width:70px" onclick="javascript:fnCancel(this.form)">
	                    <mifos:mifoslabel name="Fees.cancel" bundle="FeesUIResources"></mifos:mifoslabel>
                 </html-el:button>
                   </td>

                </tr>
            
			<script>
				 if(document.forms[0].typeId[0].checked==false &&  document.forms[0].typeId[1].checked==false){
					 document.forms[0].typeId[1].checked=true;
            	 }
				 if(document.forms[0].recurMonthDay.value=="" && document.forms[0].recurWeekDay.value!="")
				 {
					 document.forms[0].frequency[0].checked=true;
				 }
 				 if(document.forms[0].recurMonthDay.value!="" && document.forms[0].recurWeekDay.value=="")
				 {
					 document.forms[0].frequency[1].checked=true;
				 }


				  showSchedule();
				  showRate();
				  showMeetingFrequency();
				  showPayment();
				  showAdminBox();
			</script>
                

			<html-el:hidden property="method" value="" />
			<html-el:hidden property="input" value=""/>
			<html-el:hidden property="status" value="1"/>
            </table>
            <br>
          </td>
          </tr>
        </table>
      <br></td>
  </tr>
</table>
<br>
</html-el:form>
</tiles:put>
</tiles:insert>
