[#ftl]
[#--
* Copyright (c) 2005-2010 Grameen Foundation USA
*  All rights reserved.
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
*  implied. See the License for the specific language governing
*  permissions and limitations under the License.
*
*  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
*  explanation of the license and how it is applied.
--]
[#include "layout.ftl"]
[@adminLeftPaneLayout]
[#assign mifostag=JspTaglibs["/tags/mifos-html"]]
 <script type="text/javascript">
 function addOption(root, text, value)
{
  var newOpt = new Option(text, value);
  var rootLength = root.length;
  root.options[rootLength] = newOpt;
}

function deleteOption(root, index)
{ 
  var rootLength= root.length;
  if(rootLength>0)
  {
    root.options[index] = null;
  }
}

function moveOptions(root, destination)
{
  
  var rootLength= root.length;
  var rootText = new Array();
  var rootValues = new Array();
  var rootCount = 0;
  
  var i; 
  for(i=rootLength-1; i>=0; i--)
  {
    if(root.options[i].selected)
    {
      rootText[rootCount] = root.options[i].text;
      rootValues[rootCount] = root.options[i].value;
      deleteOption(root, i);
      rootCount++;
    }
  }  
  for(i=rootCount-1; i>=0; i--)
  {
    addOption(destination, rootText[i], rootValues[i]);
  }  
}

function selectAllOptions(outSel)
{
    if(null != outSel) {
         var selLength =outSel.length;
        outSel.multiple=true;
        for(i=selLength-1; i>=0; i--)
        {
            outSel.options[i].selected=true;
        }
    }
}    
</script>

   <!--  Main Content Begins-->  
  <div class="content">
  [@mifos.crumb "OrganizationPreferences.defineAcceptedPaymentTypes"/]
  
      <form method="post" action="defineAcceptedPaymentTypes.ftl" name="defineAcceptedPaymentTypes">
          
      <div class="span-24">
        <div class="clear">&nbsp;</div>
        <p class="font15"><span class="orangeheading">[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes" /]</span></p>
        <p>&nbsp;&nbsp;</p>
        <div>[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.specifytheacceptedpaymenttypesbelow.Clickonapaymenttypeintheleftboxtoselect.ThenclickAdd" /]</div>
        <p>[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.toremove,clickonapaymenttypeontherightboxtoselect.Thenclickremove" /]</p>
        <p>&nbsp;&nbsp;</p>
        <p class="fontBold">[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.clients/Groups/Centers" /]</p>
        
        <div class="span-22 last"> 
            <div class="span-21 prepend-3 ">
                <span class="span-4 rightAlign">[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.fees" /]</span>
                <span class="span-4">
                [@spring.formMultiSelect "formBean.chosenNonAcceptedFees", formBean.nonAcceptedFeePaymentTypes, "class=listSize" /]
                [@spring.showErrors "<br/>"/]     
                </span>
                
                <span class="span-3"><br />
                <input class="buttn2" name="add" type="button" value="[@spring.message "add"/] >>" onclick="moveOptions(this.form.chosenNonAcceptedFees, this.form.chosenAcceptedFees);" />
                <br /><br />
                <input class="buttn2" name="remove" type="button" value="<<[@spring.message "remove"/]" onclick="moveOptions(this.form.chosenAcceptedFees, this.form.chosenNonAcceptedFees);" />
                </span>
                
                <span class="span-4">
                [@spring.formMultiSelect "formBean.chosenAcceptedFees", formBean.acceptedFeePaymentTypes, "class=listSize" /]
                </span>
            </div>
        </div>       
        
        <p class="span-21 fontBold">[@spring.message"OrganizationPreferences.defineAcceptedPaymentTypes.loans"/]</p>
        <div class="span-22 last"> 
            <div class="span-21 prepend-3 ">
                <span class="span-4 rightAlign">[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.disbursements" /]</span>
                <span class="span-4">
                [@spring.formMultiSelect "formBean.chosenNonAcceptedLoanDisbursements", formBean.nonAcceptedLoanDisbursementPaymentTypes, "class=listSize" /]
                [@spring.showErrors "<br/>"/]     
                </span>
                
                <span class="span-3"><br />
                <input class="buttn2" name="add" type="button" value="[@spring.message "add"/] >>" onclick="moveOptions(this.form.chosenNonAcceptedLoanDisbursements, this.form.chosenAcceptedLoanDisbursements);" />
                <br /><br />
                <input class="buttn2" name="remove" type="button" value="<<[@spring.message "remove"/]" onclick="moveOptions(this.form.chosenAcceptedLoanDisbursements, this.form.chosenNonAcceptedLoanDisbursements);" />
                </span>
                
                <span class="span-4">
                [@spring.formMultiSelect "formBean.chosenAcceptedLoanDisbursements", formBean.acceptedLoanDisbursementPaymentTypes, "class=listSize" /]
                </span>
            </div>
            
            <div class="span-21 prepend-3 ">
                <span class="span-4 rightAlign">[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.repayments"/]</span>
                <span class="span-4">
                [@spring.formMultiSelect "formBean.chosenNonAcceptedLoanRepayments", formBean.nonAcceptedLoanRepaymentPaymentTypes, "class=listSize" /]
                [@spring.showErrors "<br/>"/]     
                </span>
                
                <span class="span-3"><br />
                <input class="buttn2" name="add" type="button" value="[@spring.message "add"/] >>" onclick="moveOptions(this.form.chosenNonAcceptedLoanRepayments, this.form.chosenAcceptedLoanRepayments);" />
                <br /><br />
                <input class="buttn2" name="remove" type="button" value="<<[@spring.message "remove"/]" onclick="moveOptions(this.form.chosenAcceptedLoanRepayments, this.form.chosenNonAcceptedLoanRepayments);" />
                </span>
                
                <span class="span-4">
                [@spring.formMultiSelect "formBean.chosenAcceptedLoanRepayments", formBean.acceptedLoanRepaymentPaymentTypes, "class=listSize" /]
                </span>
            </div>
        </div>
        
        <p class="span-21 fontBold">[@spring.message"OrganizationPreferences.defineAcceptedPaymentTypes.savings"/]</p>
        <div class="span-22 last"> 
            <div class="span-21 prepend-3 ">
                <span class="span-4 rightAlign">[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.withdrawals"/]</span>
                <span class="span-4">
                [@spring.formMultiSelect "formBean.chosenNonAcceptedSavingWithdrawals", formBean.nonAcceptedSavingWithdrawalPaymentTypes, "class=listSize" /]
                [@spring.showErrors "<br/>"/]     
                </span>
                
                <span class="span-3"><br />
                <input class="buttn2" name="add" type="button" value="[@spring.message "add"/] >>" onclick="moveOptions(this.form.chosenNonAcceptedSavingWithdrawals, this.form.chosenAcceptedSavingWithdrawals);" />
                <br /><br />
                <input class="buttn2" name="remove" type="button" value="<<[@spring.message "remove"/]" onclick="moveOptions(this.form.chosenAcceptedSavingWithdrawals, this.form.chosenNonAcceptedSavingWithdrawals);" />
                </span>
                
                <span class="span-4">
                [@spring.formMultiSelect "formBean.chosenAcceptedSavingWithdrawals", formBean.acceptedSavingWithdrawalPaymentTypes, "class=listSize" /]
                </span>
            </div>
            <div class="span-21 prepend-3 ">
                <span class="span-4 rightAlign">[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.deposits"/]</span>
                <span class="span-4">
                [@spring.formMultiSelect "formBean.chosenNonAcceptedSavingDeposits", formBean.nonAcceptedSavingDepositsPaymentTypes, "class=listSize" /]
                [@spring.showErrors "<br/>"/]     
                </span>
                
                <span class="span-3"><br />
                <input class="buttn2" name="add" type="button" value="[@spring.message "add"/] >>" onclick="moveOptions(this.form.chosenNonAcceptedSavingDeposits, this.form.chosenAcceptedSavingDeposits);" />
                <br /><br />
                <input class="buttn2" name="remove" type="button" value="<<[@spring.message "remove"/]" onclick="moveOptions(this.form.chosenAcceptedSavingDeposits, this.form.chosenNonAcceptedSavingDeposits);" />
                </span>
                
                <span class="span-4">
                [@spring.formMultiSelect "formBean.chosenAcceptedSavingDeposits", formBean.acceptedSavingDepositsPaymentTypes, "class=listSize" /]
                </span>
            </div>
        </div>
        <hr />
        <div class="clear">&nbsp;</div>
        
        <div class="prepend-10">
            <input class="buttn"  type="submit" name="submit" value="[@spring.message "submit"/]" onclick="selectAllOptions(this.form.chosenAcceptedFees);selectAllOptions(this.form.chosenAcceptedLoanDisbursements);selectAllOptions(this.form.chosenAcceptedLoanRepayments);selectAllOptions(this.form.chosenAcceptedSavingWithdrawals);selectAllOptions(this.form.chosenAcceptedSavingDeposits);"/>
            <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]"/>
        </div>
      </div>
       </form> 
  </div>
  <!--Main content ends-->
  [/@adminLeftPaneLayout]