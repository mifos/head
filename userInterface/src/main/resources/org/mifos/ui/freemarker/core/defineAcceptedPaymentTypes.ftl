
[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[#assign mifostag=JspTaglibs["/tags/mifos-html"]]
 <script type="text/javascript">
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
                 	<select name="lstBox" id="lstBox" multiple="multiple" class="listSize">      					
					</select></span>
                    <span class="span-3"><br /><input class="buttn2" name="add" type="button" value="Add >> " onclick="FirstListBox();" /><br /><br />
					<input class="buttn2" name="remove" type="button" value="<<Remove" onclick="SecondListBox();" /></span>
					<span class="span-4">
            		<select name="ListBox1" id="ListBox1" multiple="multiple" class="listSize">
						<option >[@spring.message "cash" /]</option>
                        <option >[@spring.message "voucher" /]</option>
                        <option >[@spring.message "cheque" /]</option>
					</select></span>
            </div>
    	</div>       
        <p class="span-21 fontBold">Loans :</p>
        <div class="span-22 last"> 
        	<div class="span-21 prepend-3 "><span class="span-4 rightAlign">[@spring.message "disbursements" /]: </span><span class="span-4">
            	<select name="lstBox" id="lstBox" multiple="multiple" class="listSize">
      					
					</select></span>
                    <span class="span-3"><br /><input class="buttn2" name="add" type="button" value="Add >>" onclick="FirstListBox();" /><br /><br />
<input class="buttn2" name="remove" type="button" value="<< Remove" onclick="SecondListBox();" /></span>
					<span class="span-4">
            		<select name="ListBox1" id="ListBox1" multiple="multiple" class="listSize">
						<option >[@spring.message "cash" /]</option>
                        <option >[@spring.message "voucher" /]</option>
                        <option >[@spring.message "cheque" /]</option>
					</select></span>
            </div>
            <div class="span-21 prepend-3 "><span class="span-4 rightAlign">[@spring.message "repayments"/] : </span><span class="span-4">
            	<select name="lstBox" id="lstBox" multiple="multiple" class="listSize">
                
					</select></span>
                    <span class="span-3"><br /><input class="buttn2" name="add" type="button" value="Add >>" onclick="FirstListBox();" /><br /><br />
<input class="buttn2" name="remove" type="button" value="<< Remove" onclick="SecondListBox();" /></span>
					<span class="span-4">
            		<select name="ListBox1" id="ListBox1" multiple="multiple" class="listSize">
						<option >[@spring.message "cash" /]</option>
                        <option >[@spring.message "voucher" /]</option>
                        <option >[@spring.message "cheque" /]</option>
					</select></span>
            </div>
    	</div>
        <p class="span-21 fontBold">Savings :</p>
        <div class="span-22 last"> 
        	<div class="span-21 prepend-3 "><span class="span-4 rightAlign">[@spring.message "withdrawals"/] : </span><span class="span-4">
            	<select name="lstBox" id="lstBox" multiple="multiple" class="listSize">
      					<option >[@spring.message "voucher" /]</option>
                        <option >[@spring.message "cheque" /]</option>
					</select></span>
                    <span class="span-3"><br /><input class="buttn2" name="add" type="button" value="Add >>" onclick="FirstListBox();" /><br /><br />
<input class="buttn2" name="remove" type="button" value="<< Remove" onclick="SecondListBox();" /></span>
					<span class="span-4">
            		<select name="ListBox1" id="ListBox1" multiple="multiple" class="listSize">
						<option >[@spring.message "cash" /]</option>
					</select></span>
            </div>
            <div class="span-21 prepend-3 "><span class="span-4 rightAlign">[@spring.message "deposits"/] : </span><span class="span-4">
            	<select name="lstBox" id="lstBox" multiple="multiple" class="listSize">
      					
					</select></span>
                    <span class="span-3"><br /><input class="buttn2" name="add" type="button" value="Add >>" onclick="FirstListBox();" /><br /><br />
<input class="buttn2" name="remove" type="button" value="<< Remove" onclick="SecondListBox();" /></span>
					<span class="span-4">
            		<select name="ListBox1" id="ListBox1" multiple="multiple" class="listSize">
						<option >[@spring.message "cash" /]</option>
                        <option >[@spring.message "voucher" /]</option>
                        <option >[@spring.message "cheque" /]</option>
					</select></span>
            </div>
    	</div>
        <hr />
        <div class="clear">&nbsp;</div>
        <div class="prepend-10">
            <input class="buttn" type="button" name="submit" value="Submit" onclick="location.href='admin.ftl'"/>
            <input class="buttn2" type="button" name="cancel" value="Cancel" onclick="location.href='admin.ftl'"/>
        </div>        
      </div>
   	</form> 
  </div>
  <!--Main content ends-->
  [@mifos.footer /]