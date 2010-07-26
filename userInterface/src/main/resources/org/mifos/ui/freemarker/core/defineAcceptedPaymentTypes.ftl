
[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
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
}</script>
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Left Sidebar Begins-->
  <div class="sidebar ht750">
  [#include "adminLeftPane.ftl" ]
  </div> 
   <!--  Left Sidebar Ends-->
 
   <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  [@mifos.crumb "defineAcceptedPaymentTypes"/]
  	<form method="post" action="defineAcceptedPaymentTypes.ftl" name="defineAcceptedPaymentTypes">  	
    
      <div class="span-24">
        <div class="clear">&nbsp;</div>
        <p class="font15"><span class="orangeheading">[@spring.message "defineAcceptedPaymentTypes" /]</span></p>
        <p>&nbsp;&nbsp;</p>
        <div>[@spring.message "specifytheacceptedpaymenttypesbelow.Clickonapaymenttypeintheleftboxtoselect.ThenclickAdd" /]</div>
        <p>[@spring.message "toremove,clickonapaymenttypeontherightboxtoselect.Thenclickremove" /]</p>
        <p>&nbsp;&nbsp;</p>
        <p class="fontBold">[@spring.message "clients/Groups/Centers" /]</p>
        <div class="span-22 last"> 
        	<div class="span-21 prepend-3 "><span class="span-4 rightAlign">[@spring.message "fees" /] </span>        	
        	<span class="span-4">
                 	<select name="infeesList" id="infeesList" multiple="multiple" class="listSize">
                 	</select></span>
                    <span class="span-3"><br /><input class="buttn2" name="add" type="button" value="Add >> " onclick="moveOptions(this.form.infeesList, this.form.outfeesList);" /><br /><br />
					<input class="buttn2" name="remove" type="button" value="<<Remove" onclick="moveOptions(this.form.outfeesList, this.form.infeesList);" /></span>
					<span class="span-4">
            		<select name="outfeesList" id="outfeesList" multiple="multiple" class="listSize">
						<option >[@spring.message "cash" /]</option>
                        <option >[@spring.message "voucher" /]</option>
                        <option >[@spring.message "cheque" /]</option>
					</select></span>
            </div>
    	</div>       
        <p class="span-21 fontBold">Loans :</p>
        <div class="span-22 last"> 
        	<div class="span-21 prepend-3 "><span class="span-4 rightAlign">[@spring.message "disbursements" /]: </span><span class="span-4">
            	<select name="indisbursementsList" id="indisbursementsList" multiple="multiple" class="listSize">
      					
					</select></span>
                    <span class="span-3"><br /><input class="buttn2" name="add" type="button" value="Add >>" onclick="moveOptions(this.form.indisbursementsList, this.form.outdisbursementsList);" /><br /><br />
<input class="buttn2" name="remove" type="button" value="<< Remove" onclick="moveOptions(this.form.outdisbursementsList, this.form.indisbursementsList);" /></span>
					<span class="span-4">
            		<select name="outdisbursementsList" id="outdisbursementsList" multiple="multiple" class="listSize">
						<option >[@spring.message "cash" /]</option>
                        <option >[@spring.message "voucher" /]</option>
                        <option >[@spring.message "cheque" /]</option>
					</select></span>
            </div>
            <div class="span-21 prepend-3 "><span class="span-4 rightAlign">[@spring.message "repayments"/] : </span><span class="span-4">
            	<select name="inrepaymentsList" id="inrepaymentsList" multiple="multiple" class="listSize">
                
					</select></span>
                    <span class="span-3"><br /><input class="buttn2" name="add" type="button" value="Add >>" onclick="moveOptions(this.form.inrepaymentsList, this.form.outrepaymentsList);" /><br /><br />
<input class="buttn2" name="remove" type="button" value="<< Remove" onclick="moveOptions(this.form.outrepaymentsList, this.form.inrepaymentsList);" /></span>
					<span class="span-4">
            		<select name="outrepaymentsList" id="outrepaymentsList" multiple="multiple" class="listSize">
						<option >[@spring.message "cash" /]</option>
                        <option >[@spring.message "voucher" /]</option>
                        <option >[@spring.message "cheque" /]</option>
					</select></span>
            </div>
    	</div>
        <p class="span-21 fontBold">Savings :</p>
        <div class="span-22 last"> 
        	<div class="span-21 prepend-3 "><span class="span-4 rightAlign">[@spring.message "withdrawals"/] : </span><span class="span-4">
            	<select name="inwithdrawalsList" id="inwithdrawalsList" multiple="multiple" class="listSize">
      					<option >[@spring.message "voucher" /]</option>
                        <option >[@spring.message "cheque" /]</option>
					</select></span>
                    <span class="span-3"><br /><input class="buttn2" name="add" type="button" value="Add >>" onclick="moveOptions(this.form.inwithdrawalsList, this.form.outwithdrawalsList);" /><br /><br />
<input class="buttn2" name="remove" type="button" value="<< Remove" onclick="moveOptions(this.form.outwithdrawalsList, this.form.inwithdrawalsList);" /></span>
					<span class="span-4">
            		<select name="outwithdrawalsList" id="outwithdrawalsList" multiple="multiple" class="listSize">
						<option >[@spring.message "cash" /]</option>
					</select></span>
            </div>
            <div class="span-21 prepend-3 "><span class="span-4 rightAlign">[@spring.message "deposits"/] : </span><span class="span-4">
            	<select name="indepositsList" id="indepositsList" multiple="multiple" class="listSize">
      					
					</select></span>
                    <span class="span-3"><br /><input class="buttn2" name="add" type="button" value="Add >>" onclick="moveOptions(this.form.indepositsList, this.form.outdepositsList);" /><br /><br />
<input class="buttn2" name="remove" type="button" value="<< Remove" onclick="moveOptions(this.form.outdepositsList, this.form.indepositsList);" /></span>
					<span class="span-4">
            		<select name="outdepositsList" id="outdepositsList" multiple="multiple" class="listSize">
						<option >[@spring.message "cash" /]</option>
                        <option >[@spring.message "voucher" /]</option>
                        <option >[@spring.message "cheque" /]</option>
					</select></span>
            </div>
    	</div>
        <hr />
        <div class="clear">&nbsp;</div>
        <div class="prepend-10">
            <input class="buttn" type="submit" name="submit" value="Submit" onclick="selectAllOptions()"/>
            <input class="buttn2" type="button" name="cancel" value="Cancel" onclick="location.href='admin.ftl'"/>
        </div>        
      </div>
   	</form> 
  </div>
  <!--Main content ends-->
  [@mifos.footer /]