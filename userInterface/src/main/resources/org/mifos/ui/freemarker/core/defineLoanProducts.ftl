[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
<head>
<script type="text/javascript">
//list boxes code
function SecListBox(ListBox,text,value){
	try{
		var option=document.createElement("OPTION");
		option.value=value;option.text=text;
		ListBox.options.add(option)
		}
	catch(er){alert(er)}
}
function FirstListBox(){
	try{
		var count=document.getElementById("lstBox").options.length;
		for(i=0;i<count;i++){
			if(document.getElementById("lstBox").options[i].selected){
				SecListBox(document.getElementById("ListBox1"),document.getElementById("lstBox").options[i].value,document.getElementById("lstBox").options[i].value);
				document.getElementById("lstBox").remove(i);
				break
			}
		}
	}
	catch(er){alert(er)}
}
function SortAllItems(){
	var arr=new Array();
	for(i=0;i<document.getElementById("lstBox").options.length;i++){
		arr[i]=document.getElementById("lstBox").options[i].value
	}
	arr.sort();
	RemoveAll();
	for(i=0;i<arr.length;i++){
		SecListBox(document.getElementById("lstBox"),arr[i],arr[i])
	}
}
function RemoveAll(){
	try{
		document.getElementById("lstBox").options.length=0
	}
	catch(er){alert(er)}
}
function SecondListBox(){
	try{
		var count=document.getElementById("ListBox1").options.length;
		for(i=0;i<count;i++){
			if(document.getElementById("ListBox1").options[i].selected){
				SecListBox(document.getElementById("lstBox"),document.getElementById("ListBox1").options[i].value,document.getElementById("ListBox1").options[i].value);
				document.getElementById("ListBox1").remove(i);
				break
			}
		}
		SortAllItems()
	}catch(er){alert(er)}
}
//next part

function enableMonth()
{
	document.getElementById("month").style.display="block";
	document.getElementById("week").style.display="none";
}
function enableWeek()
{
	document.getElementById("week").style.display="block";
	document.getElementById("month").style.display="none";

}
function enableAllLoans()
{
	document.getElementById("1").style.display="block";
	document.getElementById("2").style.display="none";
	document.getElementById("3").style.display="none";
}
function enableLoanAmount()
{
	document.getElementById("2").style.display="block";
	document.getElementById("1").style.display="none";
	document.getElementById("3").style.display="none";

}
function enableLoanCycle()
{
	document.getElementById("3").style.display="block";
	document.getElementById("2").style.display="none";
	document.getElementById("1").style.display="none";

}
function enableAllLoans1()
{
	document.getElementById("11").style.display="block";
	document.getElementById("22").style.display="none";
	document.getElementById("33").style.display="none";
}
function enableLoanAmount1()
{
	document.getElementById("22").style.display="block";
	document.getElementById("11").style.display="none";
	document.getElementById("33").style.display="none";

}
function enableLoanCycle1()
{
	document.getElementById("33").style.display="block";
	document.getElementById("22").style.display="none";
	document.getElementById("11").style.display="none";

}
function disable()
{
	document.getElementById("month").style.display="none";
	document.getElementById("22").style.display="none";
	document.getElementById("33").style.display="none";
	document.getElementById("2").style.display="none";
	document.getElementById("3").style.display="none";
}
</script>
</head>
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
   <!--  Main Content Begins-->
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-17 arrowIMG orangeheading">[@spring.message "loanproductinformation" /]</p>
        <p class="span-3 arrowIMG1 orangeheading last">[@spring.message "review&Submit" /]</p>
      </div>
      <div class="subcontent ">
        <form method="" action="" name="formname">
          <p class="font15"><span class="fontBold" id="createLoanProduct.heading">[@spring.message "addanewLoanproduct" /]</span>&nbsp;--&nbsp;<span class="orangeheading">[@spring.message "enterLoanproductinformation" /]</span></p>
          <div>[@spring.message "completethefieldsbelow.ThenclickPreview.ClickCanceltoreturn" /]</div>
          <div><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired" /] </div>
          <p class="error" id="error1"></p>
          <p class="fontBold">[@spring.message "loanproductdetails" /]</p>
          <div class="prepend-2  span-24 last">
            <div class="span-23 "><span class="pull-3 span-8 rightAlign" id="createLoanProduct.label.prdOfferingName"><span class="red">* </span>[@spring.message "productinstancename" /]&nbsp;</span><span class="span-4">
              <input type="text"  id="createLoanProduct.input.prdOffering"/>
              </span> </div>
            <div class="span-23 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "shortname" /]&nbsp;</span><span class="span-4">
              <input type="text" size="3" id="createLoanProduct.input.prdOfferingShortName" />
              </span> </div>
            <div class="span-23 "><span class="pull-3 span-8 rightAlign">[@spring.message "description" /]&nbsp;</span><span class="span-4">
              <textarea rows="7" cols="55" id="createLoanProduct.input.description"></textarea>
              </span> </div>
            <div class="span-23 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "productcategory" /]&nbsp;</span><span class="span-4">
              <select name="select">
                <option >[@spring.message "--Select--" /]</option>
                <option >[@spring.message "other" /]</option>
              </select>
              </span> </div>
            <div class="span-23 last"> <span class="pull-3 span-8 rightAlign"><span class="red"> * </span>[@spring.message "startDate" /]</span> <span class="span-2">
              <input type="text" size="1" maxlength="2" id="startDateDD"/>
              [@spring.message "DD" /]</span> <span>
              <input type="text" size="1" maxlength="2" id="startDateMM"/>
              [@spring.message "MM" /]</span> <span>
              <input type="text" size="2" maxlength="4" id="startDateYY"/>
              [@spring.message "YYYY" /]</span> </div>
            <div class="span-23 last"> <span class="pull-3 span-8 rightAlign">[@spring.message "endDate" /]</span><span class="span-2">
              <input type="text" size="1" maxlength="2" id="endDateDD"/>
              [@spring.message "DD" /]</span> <span class="span-2">
              <input type="text" size="1" maxlength="2" id="endDateMM"/>
             [@spring.message "MM" /]</span> <span class="span-3">
              <input type="text" size="2" maxlength="4" id="endDateYY"/>
              [@spring.message "YYYY" /]</span> </div>
            <div class="span-23 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "applicablefor" /]&nbsp;</span><span class="span-4">
              <select name="select">
                <option >[@spring.message "--Select--" /]</option>
                <option >[@spring.message "clients" /]</option>
                <option >[@spring.message "groups" /]</option>
              </select>
              </span> </div>
            <div class="span-23 "><span class="pull-3 span-8 rightAlign">[@spring.message "includeinLoancyclecounter" /]&nbsp;</span><span class="span-4">
              <input type="checkbox"  id="createLoanProduct.checkbox.loanCounter"/>
              </span> </div>
          </div>
          <div class="clear">&nbsp;</div>
          
          <p class="fontBold">[@spring.message "loanAmount" /]</p>
          <div class="prepend-2  span-23 last">
            <div class="span-23"><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "calculateLoanAmountas" /]&nbsp;</span>
            	<div class="span-17">
                	<span class="span-4"><input type="radio" name="loanAmount" id="createLoanProduct.radio.calcLoanAmountsameForAllLoans" value="SameforallLoans" checked="checked" onclick="enableAllLoans1();"/>[@spring.message "sameforallLoans" /]
                    </span>
                    <span class="span-4"><input type="radio" name="loanAmount" id="createLoanProduct.radio.calcLoanAmountByLastLoanAmount" value="BylastloanAmount" onclick="enableLoanAmount1();"/>[@spring.message "bylastloanAmount" /]</span>
                    <span class="span-4 last"><input type="radio" name="loanAmount" id="createLoanProduct.radio.calcLoanAmountByLoanCycle" value="ByloanCycle" onclick="enableLoanCycle1();"/>[@spring.message "byloanCycle" /]</span>
                </div>
                <div class="clear">&nbsp;</div>
                <div id="option0" class="span-17 prepend-4">
                	<div class="span-17 bluedivs fontBold paddingLeft" >
                    	<span class="span-4">[@spring.message "minloanamount" /]</span>
                        <span class="span-4">[@spring.message "maxloanamount" /]</span>
                        <span class="span-5 last">[@spring.message "defaultamount" /]</span>
                    </div>
                    <div class="clear">&nbsp;</div>
                    <div class="span-17 paddingLeft">
                    	<span class="span-4"><input type="text" /></span>
                        <span class="span-4"><input type="text" /></span>
                        <span class="span-5 last"><input type="text" /></span>
                    </div>
                    <div>&nbsp;</div>
                </div>
                
                               
               
            </div>
          </div>
          <div class="clear">&nbsp;</div>
          
          <p class="fontBold">[@spring.message "interestrate" /] </p>
          <div class="prepend-2  span-21 last">
            <div class="span-23 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "interestratetype" /]&nbsp;</span><span class="span-6">
              <select name="select">
                <option >[@spring.message "--Select--" /]</option>
                <option >[@spring.message "flat" /]</option>
                <option >[@spring.message "decliningBalance" /]</option>
                <option >[@spring.message "decliningBalance-EqualPrincipalInstallment" /]</option>
              </select>
              </span> </div>
            <div class="span-23"><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "maxInterestrate" /]&nbsp;</span><span class="span-6">
              <input type="text" id="createLoanProduct.input.maxInterestRate"/>
              &nbsp;(0 - 999)%</span>
            </div>
            <div class="span-23"><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "minInterestrate" /]&nbsp;</span><span class="span-6">
              <input type="text" id="createLoanProduct.input.minInterestRate"/>
              &nbsp;(0 - 999)%</span>
            </div>
            <div class="span-23"><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "defaultInterestrate" /]&nbsp;</span><span class="span-6">
              <input type="text" id="createLoanProduct.input.defInterestRate"/>
              &nbsp;(0 - 999)%</span>
            </div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "repaymentschedule" /]</p>
          <div class="prepend-2  span-23 last">
          	<div class="span-23 ">
            	<span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "frequencyofinstallments" /]&nbsp;</span>
                <div class="span-15 borders">
                	<div class="borderbtm span-15">
                    	<span class="span-8"><input type="radio" name="time" value="week" checked="checked" onclick="enableWeek();" id="createLoanProduct.radio.freqOfInstallmentsWeeks"/>[@spring.message "weekly" /]</span>
        				<span class="span-3 last"><input type="radio" name="time" value="month" onclick="enableMonth();" id="createLoanProduct.radio.freqOfInstallmentsMonths"/>[@spring.message "monthly" /]</span>	
                    </div>
                    <div id="week" class="paddingLeft" id="weekDIV" >
                		<span>[@spring.message "ifweeks,specifythefollowing" /]</span><br />
                			[@spring.message "recurevery" /]<input type="text" name="weeks" size="3" id="week"/>[@spring.message "week(s)" /]
                	</div>
                    <div id="month" class="paddingLeft">
                		<span>[@spring.message "ifmonths,specifythefollowing" /]</span><br />
                			[@spring.message "recurevery" /] <input type="text" name="weeks" size="3" id="month"/>[@spring.message "month(s)" /]
                	</div>
                </div>
            </div>
            <div class="span-23"><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "calculateofInstallmentsas" /]&nbsp;</span>
            	<div class="span-17">
                	<span class="span-4"><input type="radio" name="loan" id="createLoanProduct.radio.calcInstallmentSameForAll" value="SameforallLoans" checked="checked" onclick="enableAllLoans();"/>[@spring.message "sameforallLoans" /]
                    </span>
                    <span class="span-4"><input type="radio" name="loan" id="createLoanProduct.radio.calcInstallmentByLastLoanAmount" value="BylastloanAmount" onclick="enableLoanAmount();"/>[@spring.message "bylastloanAmount" /]</span>
                    <span class="span-4 last"><input type="radio" name="loan" id="createLoanProduct.radio.calcInstallmentByLoanCycle" value="ByloanCycle" onclick="enableLoanCycle();"/> [@spring.message "byloanCycle" /]</span>
                </div>
                <div class="clear">&nbsp;</div>
                <div id="install0"  class="span-17 prepend-4">
                	<div class="span-17 bluedivs fontBold paddingLeft">
                    	<span class="span-4">[@spring.message "minofinstallments" /]</span>
                        <span class="span-4">[@spring.message "maxofinstallments" /]</span>
                        <span class="span-5 last">[@spring.message "defaultofinstallments" /]</span>
                    </div>
                    <div class="clear">&nbsp;</div>
                    <div class="span-17 paddingLeft">
                    	<span class="span-4 "><input type="text" /></span>
                        <span class="span-4 "><input type="text" /></span>
                        <span class="span-5 last"><input type="text" /></span>
                    </div>
                    <div>&nbsp;</div>
                </div>
                
               
            </div>
            <div class="span-23 ">
            	<span class="pull-3 span-8 rightAlign" id="gracepertype">[@spring.message "graceperiodtype" /]&nbsp;</span>
                <span class="span-15 last">
                	<select name="select">
                		<option >[@spring.message "--Select--" /]</option>
                		<option >[@spring.message "none" /]</option>
                		<option >[@spring.message "graceonallrepayments" /]</option>
                		<option >[@spring.message "principalonlygrace" /]</option>
              		</select>
                </span>
            </div>
            <div class="span-23 ">
            	<span class="pull-3 span-8 rightAlign" id="graceperdur">[@spring.message "graceperiodduration" /]&nbsp;</span>
                <span class="span-15 last"><input type="text" />&nbsp;[@spring.message "installments" /]</span>
            </div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "fees" /] </p>
          <div class="prepend-2  span-23 last">
          	<div class="span-23"><span class="pull-3 span-8 rightAlign">[@spring.message "attachfeetypes" /]&nbsp;</span>
            	<span class="span-12 ">
                	<span class="span-8">[@spring.message "clickonafeetype" /]</span>
                    <span class="span-4">
            			<select name="lstBox" id="lstBox" multiple="multiple" class="listSize">
      						
						</select></span>
                    <span class="span-3"><br /><input class="buttn2" name="add" type="button" id="LoanFeesList.button.add"  value="Add >>" /><br /><br />
<input class="buttn2" name="remove" type="button" value="<< Remove" /></span>
					<span class="span-4">
            		<select name="ListBox1" id="ListBox1" multiple="multiple" class="listSize">
						
					</select></span>
               	</span>
            </div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "accounting" /] </p>
          <div class="prepend-2  span-23 last">
          	<div class="span-23"><span class="pull-3 span-8 rightAlign">[@spring.message "sourcesoffunds" /]&nbsp;</span>
            	<span class="span-12 ">
                	<span class="span-8">[@spring.message "clickonafund" /]</span>
                    <span class="span-4">
            			<select name="lstBox" id="lstBox" multiple="multiple" class="listSize">
      						<option >[@spring.message "nonDonor" /]</option>
                        	<option >[@spring.message "fundingOrgA" /]</option>
                            <option >[@spring.message "fundingOrgB" /]</option>
                            <option >[@spring.message "fundingOrgC" /]</option>
                            <option >[@spring.message "fundingOrgD" /]</option>
						</select></span>
                    <span class="span-3"><br /><input class="buttn2" name="add" type="button"  id="SrcFundsList.button.add"  value="Add >>" onclick="FirstListBox();" /><br /><br />
<input class="buttn2" name="remove" type="button" value="<< Remove" id="SrcFundsList.button.remove" onclick="SecondListBox();" /></span>
					<span class="span-4">
            		<select name="ListBox1" id="ListBox1" multiple="multiple" class="listSize">
						
					</select></span>
               	</span>
            </div>
            <div class="span-21"><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "productGLcode" /]&nbsp;</span>
            	<span class="span-6 "><span class="span-2">[@spring.message "interest" /]&nbsp;</span><span class="span-3">
              		<select name="select">
                		<option >[@spring.message "--Select--" /]</option>
                		<option >31102</option>
                		<option >5001</option>
                		<option >31101</option>
              		</select></span>
                
                <span  class="span-2">[@spring.message "principal" /]&nbsp;</span> <span class="span-3">
              		<select name="select">
                		<option >[@spring.message "--Select--" /]</option>
                		<option >1507</option>
                		<option >1506</option>
                		<option >1509</option>
                		<option >1508</option>
                		<option >13101</option>
                		<option >1501</option>
                		<option >1502</option>
                		<option >1503</option>
                		<option >1504</option>
                        <option >1505</option>
              		</select></span> 
               </span>
            </div>
          </div>
          <div class="clear">&nbsp;</div>
          <hr />
          <div class="prepend-9">
            <input class="buttn" type="button" id="createLoanProduct.button.preview" name="preview" value="Preview" onclick="#"/>
            <input class="buttn2" type="button" id="createLoanProduct.button.cancel" name="cancel" value="Cancel" onclick="window.location='admin.ftl'"/>
          </div>
          <div class="clear">&nbsp;</div>
        </form>
      </div>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]
  