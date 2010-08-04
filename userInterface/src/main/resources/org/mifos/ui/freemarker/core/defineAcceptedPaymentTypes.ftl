
[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[#assign mifostag=JspTaglibs["/tags/mifos-html"]]
 <script type="text/javascript">
 var leftIdArray=new  Array();
 leftIdArray[0]="feesLeft";
 leftIdArray[1]="disbursementsLeft";
 leftIdArray[2]="repaymentsLeft";
 leftIdArray[3]="withdrawalsLeft";
 leftIdArray[4]="depositsLeft";
 
 var rightIdArray=new  Array();
 rightIdArray[0]="feesRight";
 rightIdArray[1]="disbursementsRight";
 rightIdArray[2]="repaymentsRight";
 rightIdArray[3]="withdrawalsRight";
 rightIdArray[4]="depositsRight";
function SecListBox(ListBox,text,value){
	try{
		var option=document.createElement("OPTION");
		option.value=value;option.text=text;
		ListBox.options.add(option)
		}
	catch(er){alert(er)}
}
function FirstListBox(num){
	try{
		var count=document.getElementById(leftIdArray[num]).options.length;
		for(i=0;i<count;i++){
			if(document.getElementById(leftIdArray[num]).options[i].selected){
				SecListBox(document.getElementById(rightIdArray[num]),document.getElementById(leftIdArray[num]).options[i].value,document.getElementById(leftIdArray[num]).options[i].value);
				document.getElementById(leftIdArray[num]).remove(i);
				break
			}
		}
	}
	catch(er){alert(er)}
}
function SortAllItems(num){
	var arr=new Array();
	for(i=0;i<document.getElementById(leftIdArray[num]).options.length;i++){
		arr[i]=document.getElementById(leftIdArray[num]).options[i].value
	}
	arr.sort();
	RemoveAll(num);
	for(i=0;i<arr.length;i++){
		SecListBox(document.getElementById(leftIdArray[num]),arr[i],arr[i])
	}
}
function RemoveAll(num){
	try{
		document.getElementById(leftIdArray[num]).options.length=0
	}
	catch(er){alert(er)}
}
function SecondListBox(num){
	try{
		var count=document.getElementById(rightIdArray[num]).options.length;
		for(i=0;i<count;i++){
			if(document.getElementById(rightIdArray[num]).options[i].selected){
				SecListBox(document.getElementById(leftIdArray[num]),document.getElementById(rightIdArray[num]).options[i].value,document.getElementById(rightIdArray[num]).options[i].value);
				document.getElementById(rightIdArray[num]).remove(i);
				break
			}
		}
		SortAllItems(num)
	}catch(er){alert(er)}
}

function selectAllOptions()
{
for(var j=0;j<rightIdArray.length;j++){
	var selObj = document.getElementById(rightIdArray[j]);
	for (var i=0; i<selObj.options.length; i++) {
		selObj.options[i].selected = true;
	}
	}
}
</script>
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Left Sidebar Begins-->
  <div class="sidebar ht750">
  [#include "adminLeftPane.ftl" ]
  </div> 
   <!--  Left Sidebar Ends-->
 
   <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
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
        	<div class="span-21 prepend-3 "><span class="span-4 rightAlign">[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.fees" /] </span>        	
        	<span class="span-4">
                 	<select name="feesLeft" id="feesLeft" multiple="multiple" class="listSize">
                 	</select></span>
                    <span class="span-3"><br /><input class="buttn2" name="add" type="button" value="Add >> " onclick="FirstListBox(0);" /><br /><br />
					<input class="buttn2" name="remove" type="button" value="<<Remove" onclick="SecondListBox(0);" /></span>
					<span class="span-4">
            		<select name="feesRight" id="feesRight" multiple="multiple" class="listSize">
						<option >[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.cash" /]</option>
                        <option >[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.voucher" /]</option>
                        <option >[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.cheque" /]</option>
					</select></span>
            </div>
    	</div>       
        <p class="span-21 fontBold">[@spring.message"OrganizationPreferences.defineAcceptedPaymentTypes.loans"/]:</p>
        <div class="span-22 last"> 
        	<div class="span-21 prepend-3 "><span class="span-4 rightAlign">[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.disbursements" /]: </span><span class="span-4">
            	<select name="disbursementsLeft" id="disbursementsLeft" multiple="multiple" class="listSize">
      					
					</select></span>
                    <span class="span-3"><br /><input class="buttn2" name="add" type="button" value="Add >>" onclick="FirstListBox(1);" /><br /><br />
<input class="buttn2" name="remove" type="button" value="<< Remove" onclick="SecondListBox(1);" /></span>
					<span class="span-4">
            		<select name="disbursementsRight" id="disbursementsRight" multiple="multiple" class="listSize">
						<option >[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.cash" /]</option>
                        <option >[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.voucher" /]</option>
                        <option >[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.cheque" /]</option>
					</select></span>
            </div>
            <div class="span-21 prepend-3 "><span class="span-4 rightAlign">[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.repayments"/] : </span><span class="span-4">
            	<select name="repaymentsLeft" id="repaymentsLeft" multiple="multiple" class="listSize">
                
					</select></span>
                    <span class="span-3"><br /><input class="buttn2" name="add" type="button" value="Add >>" onclick="FirstListBox(2);" /><br /><br />
<input class="buttn2" name="remove" type="button" value="<< Remove" onclick="SecondListBox(2);" /></span>
					<span class="span-4">
            		<select name="repaymentsRight" id="repaymentsRight" multiple="multiple" class="listSize">
						<option >[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.cash" /]</option>
                        <option >[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.voucher" /]</option>
                        <option >[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.cheque" /]</option>
					</select></span>
            </div>
    	</div>
        <p class="span-21 fontBold">Savings :</p>
        <div class="span-22 last"> 
        	<div class="span-21 prepend-3 "><span class="span-4 rightAlign">[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.withdrawals"/] : </span><span class="span-4">
            	<select name="withdrawalsLeft" id="withdrawalsLeft" multiple="multiple" class="listSize">
      					<option >[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.voucher" /]</option>
                        <option >[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.cheque" /]</option>
					</select></span>
                    <span class="span-3"><br /><input class="buttn2" name="add" type="button" value="Add >>" onclick="FirstListBox(3);" /><br /><br />
<input class="buttn2" name="remove" type="button" value="<< Remove" onclick="SecondListBox(3);" /></span>
					<span class="span-4">
            		<select name="withdrawalsRight" id="withdrawalsRight" multiple="multiple" class="listSize">
						<option >[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.cash" /]</option>
					</select></span>
            </div>
            <div class="span-21 prepend-3 "><span class="span-4 rightAlign">[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.deposits"/] : </span><span class="span-4">
            	<select name="depositsLeft" id="depositsLeft" multiple="multiple" class="listSize">
      					
					</select></span>
                    <span class="span-3"><br /><input class="buttn2" name="add" type="button" value="Add >>" onclick="FirstListBox(4);" /><br /><br />
<input class="buttn2" name="remove" type="button" value="<< Remove" onclick="SecondListBox(4);" /></span>
					<span class="span-4">
            		<select name="depositsRight" id="depositsRight" multiple="multiple" class="listSize">
						<option >[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.cash" /]</option>
                        <option >[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.voucher" /]</option>
                        <option >[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.cheque" /]</option>
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