[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
<head>
<script type="text/javascript" language="javascript" >
function chkBox(){
	 checkbox=document.getElementsByName("id");
	if(document.getElementById("chk1").checked == true){
	for (i = 0; i < checkbox.length; i++) {
		checkbox[i].checked = true;
	}
	}
	if(document.getElementById("chk1").checked == false){
	 checkbox=document.getElementsByName("id");
		for (i = 0; i < checkbox.length; i++) {
			checkbox[i].checked = false;
		}
	}
}
</script>
</head>
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Left Sidebar Begins-->
  <div class="sidebar ht5000">
  [#include "adminLeftPane.ftl" ]
  </div> 
  <!--  Left Sidebar Ends-->
   <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  	<form method="" action="" name="formname">
    <div class="span-24 marginLeft30">
  		<div class="span-22 bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<a href="manageRolesAndPermissions.ftl">[@spring.message "rolesandpermissions"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "admin"/]</span></div>
        <div class="clear">&nbsp;</div>
        <p class="font15"><span class=" orangeheading">[@spring.message "modifyRole"/]</span></p>
        <div>[@spring.message "Checktheboxesbelowtoallowpermissionsforthespecifiedrole."/]<br />[@spring.message "roles&Permissionswithoutsavingchanges"/]</div>
        <div class="error"></div>
        <div><span>[@spring.message "roleName"/] </span><span><input type="text" name="roleName" /></span></div>
        <div class="clear">&nbsp;</div>
        <div class="span-22 borders">
        	<div class="span-22 bluediv">
            	<span><input type="checkbox" id="0" /></span><span>[@spring.message "organizationManagement"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="0_0"  /></span><span class="span-10 borderrt">[@spring.message "funds"/]</span>
                <span class="span-8">[@spring.message "funds"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="0_0_0"/></span><span class="span-9 borderrt">[@spring.message "cancreatefunds"/]</span>
                <span class="span-8">[@spring.message "cancreatefunds"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="0_0_1"/></span><span class="span-9 borderrt">[@spring.message "canmodifyfunds"/]</span>
                <span class="span-8">[@spring.message "canmodifyfunds"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="0_1"/></span><span class="span-10 borderrt">[@spring.message "fees"/]</span>
                <span class="span-8">[@spring.message "fees"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox"  id="0_1_0" /></span><span class="span-9 borderrt">[@spring.message "candefinenewfeetype"/]</span>
                <span class="span-8">[@spring.message "candefinenewfeetype"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox"  id="0_1_1" /></span><span class="span-9 borderrt">[@spring.message "canmodifyfeeinformation"/]</span>
                <span class="span-8">[@spring.message "canmodifyfeeinformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="0_2"/></span><span class="span-10 borderrt">[@spring.message "checklists"/]</span>
                <span class="span-8">[@spring.message "checklists"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox"  id="0_2_0"/></span><span class="span-9 borderrt">[@spring.message "candefinenewchecklisttype"/]</span>
                <span class="span-8">[@spring.message "candefinenewchecklisttype"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="0_2_1"/></span><span class="span-9 borderrt">[@spring.message "canmodifychecklistinformation"/]</span>
                <span class="span-8">[@spring.message "canmodifychecklistinformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="0_3" /></span><span class="span-10 borderrt">[@spring.message "candefineAcceptedPaymentType"/]</span>
                <span class="span-8">[@spring.message "candefineAcceptedPaymentType"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="0_4" /></span><span class="span-10 borderrt">[@spring.message "candefineHoliday"/]</span>
                <span class="span-8">[@spring.message "candefineHoliday"/]</span>
            </div>
        </div>
        <div class="clear">&nbsp;</div>
		<!--Second Table Starts-->
        <div class="span-22 borders">
        	<div class="span-22 bluediv">
            	<span><input type="checkbox" id="1"/></span><span>[@spring.message "officemanagement"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox"  id="1_0"/></span><span class="span-10 borderrt">[@spring.message "offices"/]</span>
                <span class="span-8">[@spring.message "offices"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="1_0_0"/></span><span class="span-9 borderrt">[@spring.message "cancreatenewoffice"/]</span>
                <span class="span-8">[@spring.message "cancreatenewoffice"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="1_0_1"/></span><span class="span-9 borderrt">[@spring.message "canmodifyofficeinformation"/]</span>
                <span class="span-8">[@spring.message "canmodifyofficeinformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="1_1"/></span><span class="span-10 borderrt">[@spring.message "canmodifyofficehierarchy"/]</span>
                <span class="span-8">[@spring.message "canmodifyofficehierarchy"/]</span>
            </div></div>
        <!--Third Table Starts-->
        <div class="clear">&nbsp;</div>
        <div class="span-22 borders">
        	<div class="span-22 bluediv">
            	<span><input type="checkbox" id="2" /></span><span>[@spring.message "usermanagement"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="2_0"/></span><span class="span-10 borderrt">[@spring.message "personnel"/]</span>
                <span class="span-8">[@spring.message "personnel"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="2_0_0"/></span><span class="span-9 borderrt">[@spring.message "cancreatenewsystemusers"/]</span>
                <span class="span-8">[@spring.message "cancreatenewsystemusers"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox"  id="2_0_1"/></span><span class="span-9 borderrt">[@spring.message "canmodifyuserinformation"/]</span>
                <span class="span-8">[@spring.message "canmodifyuserinformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="2_0_2" /></span><span class="span-9 borderrt">[@spring.message "canunlockause"/]</span>
                <span class="span-8">[@spring.message "canunlockause"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="2_0_3"/></span><span class="span-9 borderrt">[@spring.message "caneditselfinformation"/]</span>
                <span class="span-8">[@spring.message "caneditselfinformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="2_0_4"/></span><span class="span-9 borderrt">[@spring.message "canAddNotestoPersonnel"/]</span>
                <span class="span-8">[@spring.message "canAddNotestoPersonnel"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="2_1"/></span><span class="span-10 borderrt">[@spring.message "roles"/]</span>
                <span class="span-8">[@spring.message "roles"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="2_1_0"/></span><span class="span-9 borderrt">[@spring.message "cancreatenewrole"/]</span>
                <span class="span-8">[@spring.message "cancreatenewrole"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="2_1_1"/></span><span class="span-9 borderrt">[@spring.message "canmodifyarole"/]</span>
                <span class="span-8">[@spring.message "canmodifyarole"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="2_1_2" /></span><span class="span-9 borderrt">[@spring.message "candeletearole"/]</span>
                <span class="span-8">[@spring.message "candeletearole"/]</span>
            </div>
        </div>
        <!--Fourth Table Starts-->
        <div class="clear">&nbsp;</div>
        <div class="span-22 borders">
        	<div class="span-22 bluediv">
            	<span><input type="checkbox" id="3"/></span><span>[@spring.message "clientManagement"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="3_0"/></span><span class="span-10 borderrt">[@spring.message "clients"/]</span>
                <span class="span-8">[@spring.message "clients"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_0_0"/></span><span class="span-9 borderrt">[@spring.message "cancreatenewClientinsaveforlaterstate"/]</span>
                <span class="span-8">[@spring.message "cancreatenewClientinsaveforlaterstate"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_0_1"/></span><span class="span-9 borderrt">[@spring.message "cancreatenewClientinsubmitforapprovalstate"/]</span>
                <span class="span-8">[@spring.message "cancreatenewClientinsubmitforapprovalstate"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_0_2"/></span><span class="span-9 borderrt">[@spring.message "canchangestatetopartialapplication"/]</span>
                <span class="span-8">[@spring.message "canchangestatetopartialapplication"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox"  id="3_0_3"/></span><span class="span-9 borderrt">[@spring.message "canchangestatetoactive"/]</span>
                <span class="span-8">[@spring.message "canchangestatetoactive"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox"  id="3_0_4"/></span><span class="span-9 borderrt">[@spring.message "canchangestatetoCancelled"/]</span>
                <span class="span-8">[@spring.message "canchangestatetoCancelled"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_0_5"/></span><span class="span-9 borderrt">[@spring.message "canchangestatetoOnHold"/]</span>
                <span class="span-8">[@spring.message "canchangestatetoOnHold"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_0_6"/></span><span class="span-9 borderrt">[@spring.message "canchangestatetoClosed"/]</span>
                <span class="span-8">[@spring.message "canchangestatetoClosed"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_0_6"/></span><span class="span-9 borderrt">[@spring.message "canchangestatetoapplicationpendingapproval"/]</span>
                <span class="span-8">[@spring.message "canchangestatetoapplicationpendingapproval"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_0_8"/></span><span class="span-9 borderrt">[@spring.message "canmakepaymentstoClientaccounts"/]</span>
                <span class="span-8">[@spring.message "canmakepaymentstoClientaccounts"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_0_9"/></span><span class="span-9 borderrt">[@spring.message "canmakeadjustmententriestoClientaccount"/]</span>
                <span class="span-8">[@spring.message "canmakeadjustmententriestoClientaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_0_10"/></span><span class="span-9 borderrt">[@spring.message "canwaiveadueamount"/]</span>
                <span class="span-8">[@spring.message "canwaiveadueamount"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_0_11"/></span><span class="span-9 borderrt">[@spring.message "canremovefeetypesfromClientaccount"/]</span>
                <span class="span-8">[@spring.message "canremovefeetypesfromClientaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_0_12"/></span><span class="span-9 borderrt">[@spring.message "canaddnotestoClient"/]</span>
                <span class="span-8">[@spring.message "canaddnotestoClient"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_0_13"/></span><span class="span-9 borderrt">[@spring.message "caneditMFIinformation"/]</span>
                <span class="span-8">[@spring.message "caneditMFIinformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_0_14"/></span><span class="span-9 borderrt">[@spring.message "caneditgroupmembership"/]</span>
                <span class="span-8">[@spring.message "caneditgroupmembership"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_0_15"/></span><span class="span-9 borderrt">[@spring.message "caneditofficemembership"/]</span>
                <span class="span-8">[@spring.message "caneditofficemembership"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_0_16"/></span><span class="span-9 borderrt">[@spring.message "caneditmeetingschedule"/]</span>
                <span class="span-8">[@spring.message "caneditmeetingschedule"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_0_17"/></span><span class="span-9 borderrt">[@spring.message "canadd/edithistoricadata"/]</span>
                <span class="span-8">[@spring.message "canadd/edithistoricadata"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_0_18"/></span><span class="span-9 borderrt">[@spring.message "caneditfeeamountattachedtotheaccount"/]</span>
                <span class="span-8">[@spring.message "caneditfeeamountattachedtotheaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_0_19"/></span><span class="span-9 borderrt">[@spring.message "canBlacklistaClient"/]</span>
                <span class="span-8">[@spring.message "canBlacklistaClient"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_0_20"/></span><span class="span-9 borderrt">[@spring.message "canspecifymeetingschedule"/]</span>
                <span class="span-8">[@spring.message "canspecifymeetingschedule"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_0_21"/></span><span class="span-9 borderrt">[@spring.message "caneditpersonalinformation"/]</span>
                <span class="span-8">[@spring.message "caneditpersonalinformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_0_22"/></span><span class="span-9 borderrt">[@spring.message "canapplychargestoClientaccounts"/]</span>
                <span class="span-8">[@spring.message "canapplychargestoClientaccounts"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_0_23"/></span><span class="span-9 borderrt">[@spring.message "canremoveclientsfromgroups"/]</span>
                <span class="span-8">[@spring.message "canremoveclientsfromgroups"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_0_24"/></span><span class="span-9 borderrt">[@spring.message "canaddanexistingclienttoagroup"/]</span>
                <span class="span-8">[@spring.message "canaddanexistingclienttoagroup"/]</span>
            </div>
            
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox"  id="3_1"/></span><span class="span-10 borderrt">[@spring.message "groups"/]</span>
                <span class="span-8">[@spring.message "groups"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_1_0"/></span><span class="span-9 borderrt">[@spring.message "cancreatenewgroupinsaveforlaterstate"/]</span>
                <span class="span-8">[@spring.message "cancreatenewgroupinsaveforlaterstate"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_1_`"/></span><span class="span-9 borderrt">[@spring.message "cancreatenewgroupinsubmitforapprovalstate"/]</span>
                <span class="span-8">[@spring.message "cancreatenewgroupinsubmitforapprovalstate"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_1_2"/></span><span class="span-9 borderrt">[@spring.message "canchangestatetopartialapplication"/]</span>
                <span class="span-8">[@spring.message "canchangestatetopartialapplication"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_1_3"/></span><span class="span-9 borderrt">[@spring.message "canchangestatetoactive"/]</span>
                <span class="span-8">[@spring.message "canchangestatetoactive"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_1_4"/></span><span class="span-9 borderrt">[@spring.message "canchangestatetoCancelled"/]</span>
                <span class="span-8">[@spring.message "canchangestatetoCancelled"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_1_5"/></span><span class="span-9 borderrt">[@spring.message "canchangestatetoOnHold"/]</span>
                <span class="span-8">[@spring.message "canchangestatetoOnHold"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_1_6"/></span><span class="span-9 borderrt">[@spring.message "canchangestatetoClosed"/]</span>
                <span class="span-8">[@spring.message "canchangestatetoClosed"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_1_7"/></span><span class="span-9 borderrt">[@spring.message "canchangestatetoapplicationpendingapproval"/]</span>
                <span class="span-8">[@spring.message "canchangestatetoapplicationpendingapproval"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_1_8"/></span><span class="span-9 borderrt">[@spring.message "canmakepaymentstogroupaccounts"/]</span>
                <span class="span-8">[@spring.message "canmakepaymentstogroupaccounts"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_1_9"/></span><span class="span-9 borderrt">[@spring.message "canmakeadjustmententriestogroupaccount"/]</span>
                <span class="span-8">[@spring.message "canmakeadjustmententriestogroupaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_1_10"/></span><span class="span-9 borderrt">[@spring.message "canwaiveadueamount"/]</span>
                <span class="span-8">[@spring.message "canwaiveadueamount"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_1_11"/></span><span class="span-9 borderrt">[@spring.message "canremovefeetypesfromgroupaccount"/]</span>
                <span class="span-8">[@spring.message "canremovefeetypesfromgroupaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_1_12"/></span><span class="span-9 borderrt">[@spring.message "canaddnotestogroup"/]</span>
                <span class="span-8">[@spring.message "canaddnotestogroup"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_1_13"/></span><span class="span-9 borderrt">[@spring.message "caneditGroupinformation"/]</span>
                <span class="span-8">[@spring.message "caneditGroupinformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_1_14"/></span><span class="span-9 borderrt">[@spring.message "caneditCenterClientship"/]</span>
                <span class="span-8">[@spring.message "caneditCenterClientship"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_1_15"/></span><span class="span-9 borderrt">[@spring.message "caneditofficemembership"/]</span>
                <span class="span-8">[@spring.message "caneditofficemembership"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_1_16"/></span><span class="span-9 borderrt">[@spring.message "caneditmeetingschedule"/]</span>
                <span class="span-8">[@spring.message "caneditmeetingschedule"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_1_17"/></span><span class="span-9 borderrt">[@spring.message "canadd/edithistoricaldata"/]</span>
                <span class="span-8">[@spring.message "canadd/edithistoricaldata"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_1_18"/></span><span class="span-9 borderrt">[@spring.message "caneditfeeamountattachedtotheaccount"/]</span>
                <span class="span-8">[@spring.message "caneditfeeamountattachedtotheaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_1_19"/></span><span class="span-9 borderrt">[@spring.message "canBlacklistagroup"/]</span>
                <span class="span-8">[@spring.message "canBlacklistagroup"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_1_20"/></span><span class="span-9 borderrt">[@spring.message "canspecifymeetingschedule"/]</span>
                <span class="span-8">[@spring.message "canspecifymeetingschedule"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox"  id="3_1_21"/></span><span class="span-9 borderrt">[@spring.message "canapplychargestogroupaccounts"/]</span>
                <span class="span-8">[@spring.message "canapplychargestogroupaccounts"/]</span>
            </div>
            
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="3_2"/></span><span class="span-10 borderrt">[@spring.message "centers"/]</span>
                <span class="span-8">[@spring.message "centers"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_2_0"/></span><span class="span-9 borderrt">[@spring.message "cancreatenewCenter"/]</span>
                <span class="span-8">[@spring.message "cancreatenewCenter"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_2_1"/></span><span class="span-9 borderrt">[@spring.message "canmodifyCenterinformation"/]</span>
                <span class="span-8">[@spring.message "canmodifyCenterinformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_2_2"/></span><span class="span-9 borderrt">[@spring.message "caneditCenterstatus"/]</span>
                <span class="span-8">[@spring.message "caneditCenterstatus"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_2_3"/></span><span class="span-9 borderrt">[@spring.message "canmakepaymentstoCenteraccounts"/]</span>
                <span class="span-8">[@spring.message "canmakepaymentstoCenteraccounts"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_2_4"/></span><span class="span-9 borderrt">[@spring.message "canmakeadjustmententriestoCenteraccount"/]</span>
                <span class="span-8">[@spring.message "canmakeadjustmententriestoCenteraccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_2_5"/></span><span class="span-9 borderrt">[@spring.message "canwaiveadueamount"/]</span>
                <span class="span-8">[@spring.message "canwaiveadueamount"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_2_6"/></span><span class="span-9 borderrt">[@spring.message "canremovefeetypesfromCenteraccount"/]</span>
                <span class="span-8">[@spring.message "canremovefeetypesfromCenteraccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_2_7"/></span><span class="span-9 borderrt">[@spring.message "canaddnotestoCenterrecords"/]</span>
                <span class="span-8">[@spring.message "canaddnotestoCenterrecords"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_2_8"/></span><span class="span-9 borderrt">[@spring.message "caneditfeeamountattachedtotheaccount"/]</span>
                <span class="span-8">[@spring.message "caneditfeeamountattachedtotheaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_2_9"/></span><span class="span-9 borderrt">[@spring.message "caneditmeetingschedule"/]</span>
                <span class="span-8">[@spring.message "caneditmeetingschedule"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_2_10"/></span><span class="span-9 borderrt">[@spring.message "canspecifymeetingschedule"/]</span>
                <span class="span-8">[@spring.message "canspecifymeetingschedule"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="3_2_11"/></span><span class="span-9 borderrt">[@spring.message "canapplychargestoCenteraccounts"/]</span>
                <span class="span-8">[@spring.message "canapplychargestoCenteraccounts"/]</span>
            </div>
        </div>
        <div class="clear">&nbsp;</div>
		<!--Fifth Table Starts-->
        <div class="span-22 borders">
        	<div class="span-22 bluediv">
            	<span><input type="checkbox" id="4"/></span><span>[@spring.message "productdefinition"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="4_0"/></span><span class="span-10 borderrt">[@spring.message "productCategories"/]</span>
                <span class="span-8">[@spring.message "productCategories"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="4_0_0"/></span><span class="span-9 borderrt">[@spring.message "candefinenewproductcategories"/]</span>
                <span class="span-8">[@spring.message "candefinenewproductcategories"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="4_0_1"/></span><span class="span-9 borderrt">[@spring.message "caneditproductcategoryinformation"/]</span>
                <span class="span-8">[@spring.message "caneditproductcategoryinformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox"  id="4_0_2"/></span><span class="span-9 borderrt">[@spring.message "canmodifylateness/dormancydefinition"/]</span>
                <span class="span-8">[@spring.message "canmodifylateness/dormancydefinition"/]</span>
            </div>
            
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="4_1"/></span><span class="span-10 borderrt">[@spring.message "loanProducts"/]</span>
                <span class="span-8">[@spring.message "loanProducts"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="4_1_0"/></span><span class="span-9 borderrt">[@spring.message "candefinenewloanproductinstance"/]</span>
                <span class="span-8">[@spring.message "candefinenewloanproductinstance"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="4_1_1" /></span><span class="span-9 borderrt">[@spring.message "caneditloanproductinstances"/]</span>
                <span class="span-8">[@spring.message "caneditloanproductinstances"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="4_2"/></span><span class="span-10 borderrt">[@spring.message "savingsProducts"/]</span>
                <span class="span-8">[@spring.message "savingsProducts"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="4_2_0"/></span><span class="span-9 borderrt">[@spring.message "candefinenewSavingsproductinstance"/]</span>
                <span class="span-8">[@spring.message "candefinenewSavingsproductinstance"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="4_2_1"/></span><span class="span-9 borderrt">[@spring.message "caneditSavingsproductinstances"/]</span>
                <span class="span-8">[@spring.message "caneditSavingsproductinstances"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="4_3"/></span><span class="span-10 borderrt">[@spring.message "productMix"/]</span>
                <span class="span-8">[@spring.message "productMix"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="4_3_0"/></span><span class="span-9 borderrt">[@spring.message "candefineproductmix"/]</span>
                <span class="span-8">[@spring.message "candefineproductmix"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="4_3_1"/></span><span class="span-9 borderrt">[@spring.message "caneditproductmix"/]</span>
                <span class="span-8">[@spring.message "caneditproductmix"/]</span>
            </div>
            
        </div>
        <div class="clear">&nbsp;</div>
		<!--Sixth Table Starts-->
        <div class="span-22 borders">
        	<div class="span-22 bluediv">
            	<span><input type="checkbox"  id="5"/></span><span>[@spring.message "loanmanagement"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox"   id="5_0"/></span><span class="span-10 borderrt">[@spring.message "loanProcessing"/]</span>
                <span class="span-8">[@spring.message "loanProcessing"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="5_0_0" /></span><span class="span-9 borderrt">[@spring.message "cancreatenewloanaccountinSaveforlaterstate"/]</span>
                <span class="span-8">[@spring.message "cancreatenewloanaccountinSaveforlaterstate"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="5_0_1" /></span><span class="span-9 borderrt">[@spring.message "cancreatenewloanaccountinSubmitforapprovalstate"/]</span>
                <span class="span-9">[@spring.message "cancreatenewloanaccountinSubmitforapprovalstate"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="5_0_2" /></span><span class="span-9 borderrt">[@spring.message "canchangestatetopartialapplication"/]</span>
                <span class="span-8">[@spring.message "canchangestatetopartialapplication"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="5_0_3" /></span><span class="span-9 borderrt">[@spring.message "canchangestatetoApproved"/]</span>
                <span class="span-8">[@spring.message "canchangestatetoApproved"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="5_0_4" /></span><span class="span-9 borderrt">[@spring.message "canchangestatetoCancelled"/]</span>
                <span class="span-8">[@spring.message "canchangestatetoCancelled"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="5_0_5" /></span><span class="span-9 borderrt">[@spring.message "canchangestatetoDisbursedtoLO"/]</span>
                <span class="span-8">[@spring.message "canchangestatetoDisbursedtoLO"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="5_0_6" /></span><span class="span-9 borderrt">[@spring.message "canchangestatetopendingapproval"/]</span>
                <span class="span-8">[@spring.message "canchangestatetopendingapproval"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="5_0_7" /></span><span class="span-9 borderrt">[@spring.message "canchangestatetoClosed-Writtenoff"/]</span>
                <span class="span-8">[@spring.message "canchangestatetoClosed-Writtenoff"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="5_0_8" /></span><span class="span-9 borderrt">[@spring.message "canchangestatetoClosed-rescheduled"/]</span>
                <span class="span-8">[@spring.message "canchangestatetoClosed-rescheduled"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="5_0_9" /></span><span class="span-9 borderrt">[@spring.message "caneditLoanaccountinformation"/]</span>
                <span class="span-8">[@spring.message "caneditLoanaccountinformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="5_0_10" /></span><span class="span-9 borderrt">[@spring.message "canaddnotestoloanaccount"/]</span>
                <span class="span-8">[@spring.message "canaddnotestoloanaccount"/]</span>
            </div>
            
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="5_1"/></span><span class="span-10 borderrt">[@spring.message "loanTransactions"/]</span>
                <span class="span-8">[@spring.message "loanTransactions"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="5_1_0"/></span><span class="span-9 borderrt">[@spring.message "canmakepaymenttotheaccount"/]</span>
                <span class="span-8">[@spring.message "canmakepaymenttotheaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="5_1_1"/></span><span class="span-9 borderrt">[@spring.message "canmakeadjustmententrytotheaccount"/]</span>
                <span class="span-8">[@spring.message "canmakeadjustmententrytotheaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="5_1_2"/></span><span class="span-9 borderrt">[@spring.message "canwaivepenalty"/]</span>
                <span class="span-8">[@spring.message "canwaivepenalty"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="5_1_3"/></span><span class="span-9 borderrt">[@spring.message "canwaiveafeeinstallment"/]</span>
                <span class="span-8">[@spring.message "canwaiveafeeinstallment"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="5_1_4"/></span><span class="span-9 borderrt">[@spring.message "canremovefeetypesattachedtotheaccount"/]</span>
                <span class="span-8">[@spring.message "canremovefeetypesattachedtotheaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="5_1_5"/></span><span class="span-9 borderrt">[@spring.message "canapplychargestoloans"/]</span>
                <span class="span-8">[@spring.message "canapplychargestoloans"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="5_1_6"/></span><span class="span-9 borderrt">[@spring.message "canrepayloan"/]</span>
                <span class="span-8">[@spring.message "canrepayloan"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="5_1_7"/></span><span class="span-9 borderrt">[@spring.message "candisburseloan"/]</span>
                <span class="span-8">[@spring.message "candisburseloan"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="5_1_8"/></span><span class="span-9 borderrt">[@spring.message "canadjustpaymentwhen"/]</span>
                <span class="span-9">[@spring.message "canadjustpaymentwhen"/]</span>
            </div>
            
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="5_2"/></span><span class="span-10 borderrt">[@spring.message "canreverseLoandisbursals"/]</span>
                <span class="span-8">[@spring.message "canreverseLoandisbursals"/]</span>
            </div>

            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="5_3"/></span><span class="span-10 borderrt">[@spring.message "canredoLoandisbursals"/]</span>
                <span class="span-8">[@spring.message "canredoLoandisbursals"/]</span>
            </div>
        </div>
        <div class="clear">&nbsp;</div>
		<!--Seventh Table Starts-->
        <div class="span-22 borders">
        	<div class="span-22 bluediv">
            	<span><input type="checkbox" id="6"/></span><span>[@spring.message "savingsManagement"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="6_0"/></span><span class="span-10 borderrt">[@spring.message "cancreatenewSavingsaccountinSaveforlaterstate"/]</span>
                <span class="span-8">[@spring.message "cancreatenewSavingsaccountinSaveforlaterstate"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="6_1"/></span><span class="span-10 borderrt">[@spring.message "canupdateSavingsaccount"/]</span>
                <span class="span-8">[@spring.message "canupdateSavingsaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="6_2"/></span><span class="span-10 borderrt">[@spring.message "cancloseSavingsaccount"/]</span>
                <span class="span-8">[@spring.message "cancloseSavingsaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="6_3"/></span><span class="span-10 borderrt">[@spring.message "canchangestatetopartialapplication"/]</span>
                <span class="span-8">[@spring.message "canchangestatetopartialapplication"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="6_4"/></span><span class="span-10 borderrt">[@spring.message "canchangestatetopendingapproval"/]</span>
                <span class="span-8">[@spring.message "canchangestatetopendingapproval"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="6_5"/></span><span class="span-10 borderrt">[@spring.message "canchangestatetocancel"/]</span>
                <span class="span-8">[@spring.message "canchangestatetocancel"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="6_6"/></span><span class="span-10 borderrt">[@spring.message "canchangestatetoactive"/]</span>
                <span class="span-8">[@spring.message "canchangestatetoactive"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="6_7"/></span><span class="span-10 borderrt">[@spring.message "canchangestatetoinactive"/]</span>
                <span class="span-8">[@spring.message "canchangestatetoinactive"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="6_8"/></span><span class="span-10 borderrt">[@spring.message "canblacklistSavingsaccount"/]</span>
                <span class="span-8">[@spring.message "canblacklistSavingsaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="6_9"/></span><span class="span-10 borderrt">[@spring.message "cancreatenewSavingsaccountinsubmitforapprovalstate"/]</span>
                <span class="span-9">[@spring.message "cancreatenewSavingsaccountinsubmitforapprovalstate"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="6_10"/></span><span class="span-10 borderrt">[@spring.message "candoadjustmentsforsavingsaccount"/]</span>
                <span class="span-8">[@spring.message "candoadjustmentsforsavingsaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="6_11"/></span><span class="span-10 borderrt">[@spring.message "canwaiveduedepositsforSavingsaccount"/]</span>
                <span class="span-8">[@spring.message "canwaiveduedepositsforSavingsaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="6_12"/></span><span class="span-10 borderrt">[@spring.message "canwaiveoverduedepositsforSavingsaccount"/]</span>
                <span class="span-8">[@spring.message "canwaiveoverduedepositsforSavingsaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="6_13"/></span><span class="span-10 borderrt">[@spring.message "canmakedeposit/withdrawaltosavingsaccounta"/]</span>
                <span class="span-8">[@spring.message "canmakedeposit/withdrawaltosavingsaccounta"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="6_14"/></span><span class="span-10 borderrt">[@spring.message "canaddnotestoSavingsaccount"/]</span>
                <span class="span-8">[@spring.message "canaddnotestoSavingsaccount"/]</span>
            </div>
        </div>
        <div class="clear">&nbsp;</div>
		<!--Eight Table Starts-->
        <div class="span-22 borders">
        	<div class="span-22 bluediv">
            	<span><input type="checkbox" id="7"/></span><span>[@spring.message "reportsManagement"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="7_0"/></span><span class="span-10 borderrt">[@spring.message "clientDetail"/]</span>
                <span class="span-8">[@spring.message "clientDetail"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="7_0_0"/></span><span class="span-9 borderrt">[@spring.message "canviewCollectionSheetReport"/]</span>
                <span class="span-8">[@spring.message "canviewCollectionSheetReport"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox"  id="7_1"/></span><span class="span-10 borderrt">[@spring.message "center"/]</span>
                <span class="span-8">[@spring.message "center"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox"  id="7_2"/></span><span class="span-10 borderrt">[@spring.message "status"/]</span>
                <span class="span-8">[@spring.message "status"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox"  id="7_3"/></span><span class="span-10 borderrt">[@spring.message "performance"/]</span>
                <span class="span-8">[@spring.message "performance"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox"  id="7_4"/></span><span class="span-10 borderrt">[@spring.message "loanProductDetail"/]</span>
                <span class="span-8">[@spring.message "loanProductDetail"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox"  id="7_5"/></span><span class="span-10 borderrt">[@spring.message "analysis"/]</span>
                <span class="span-8">[@spring.message "analysis"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="7_5_0"/></span><span class="span-9 borderrt">[@spring.message "permissions-CanViewBranchCashConfirmationReport"/]</span>
                <span class="span-8">[@spring.message "permissions-CanViewBranchCashConfirmationReport"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="7_5_1"/></span><span class="span-9 borderrt">[@spring.message "permissions-CanViewBranchProgressReport"/]</span>
                <span class="span-8">[@spring.message "permissions-CanViewBranchProgressReport"/]</span>
            </div>
             <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="7_5_2"/></span><span class="span-9 borderrt">[@spring.message "canViewDetailedAgingOfPortfolioAtRisk"/]</span>
                <span class="span-8">[@spring.message "canViewDetailedAgingOfPortfolioAtRisk"/]</span>
            </div>
             <div class="span-22 borderbtm">
            	<span class="span-3 rightAlign"><input type="checkbox" id="7_5_3"/></span><span class="span-9 borderrt">[@spring.message "canViewGeneralLedgerReport"/]</span>
                <span class="span-8">[@spring.message "canViewGeneralLedgerReport"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="7_6"/></span><span class="span-10 borderrt">[@spring.message "miscellaneous"/]</span>
                <span class="span-8">[@spring.message "miscellaneous"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="7_7"/></span><span class="span-10 borderrt">[@spring.message "canuploadreporttemplate"/]</span>
                <span class="span-8">[@spring.message "canuploadreporttemplate"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="7_8"/></span><span class="span-10 borderrt">[@spring.message "canviewreports"/]</span>
                <span class="span-8">[@spring.message "canviewreports"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="7_9"/></span><span class="span-10 borderrt">[@spring.message "caneditreportinformation"/]</span>
                <span class="span-8">[@spring.message "caneditreportinformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="7_10"/></span><span class="span-10 borderrt">[@spring.message "candefinenewreportcategory"/]</span>
                <span class="span-8">[@spring.message "candefinenewreportcategory"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="7_11"/></span><span class="span-10 borderrt">[@spring.message "canviewreportcategory"/]</span>
                <span class="span-8">[@spring.message "canviewreportcategory"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="7_12"/></span><span class="span-10 borderrt">[@spring.message "candeletereportcategory"/]</span>
                <span class="span-8">[@spring.message "candeletereportcategory"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="7_13"/></span><span class="span-10 borderrt">[@spring.message "candownloadreporttemplate"/]</span>
                <span class="span-8">[@spring.message "candownloadreporttemplate"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="7_14"/></span><span class="span-10 borderrt">[@spring.message "canuploadadmindocuments"/]</span>
                <span class="span-8">[@spring.message "canuploadadmindocuments"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="7_15"/></span><span class="span-10 borderrt">[@spring.message "canviewadmindocuments"/]</span>
                <span class="span-8">[@spring.message "canviewadmindocuments"/]</span>
            </div>
        </div>
        <div class="clear">&nbsp;</div>
		<!--Ninth Table Starts-->
        <div class="span-22 borders">
        	<div class="span-22 bluediv">
            	<span><input type="checkbox" id="8"/></span><span>[@spring.message "bulk"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="8_0"/></span><span class="span-10 borderrt">[@spring.message "canapproveloansinbulk"/]</span>
                <span class="span-8">[@spring.message "canapproveloansinbulk"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="8_1"/></span><span class="span-10 borderrt">[@spring.message "canEnterCollectionSheetData"/]</span>
                <span class="span-8">[@spring.message "canEnterCollectionSheetData"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="8_2"/></span><span class="span-10 borderrt">[@spring.message "cancreatemultipleLoanaccounts"/]</span>
                <span class="span-8">[@spring.message "cancreatemultipleLoanaccounts"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="8_3"/></span><span class="span-10 borderrt">[@spring.message "canimporttransaction"/]</span>
                <span class="span-8">[@spring.message "canimporttransaction"/]</span>
            </div>
        </div>
        <div class="clear">&nbsp;</div>
		<!--Tenth Table Starts-->
        <div class="span-22 borders">
        	<div class="span-22 bluediv">
            	<span><input type="checkbox" id="9"/></span><span>[@spring.message "configurationManagement"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="9_0"/></span><span class="span-10 borderrt">[@spring.message "candefinelabels"/]</span>
                <span class="span-8">[@spring.message "candefinelabels"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="9_1"/></span><span class="span-10 borderrt">[@spring.message "candefinehidden/mandatoryfields"/]</span>
                <span class="span-8">[@spring.message "candefinehidden/mandatoryfields"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="9_2"/></span><span class="span-10 borderrt">[@spring.message "canDefineLookupValues"/]</span>
                <span class="span-8">[@spring.message "canDefineLookupValues"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="9_3"/></span><span class="span-10 borderrt">[@spring.message "candefinecustomfields"/]</span>
                <span class="span-8">[@spring.message "candefinecustomfields"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox" id="9_4"/></span><span class="span-10 borderrt">[@spring.message "canvieworganizationsettings"/]</span>
                <span class="span-8">[@spring.message "canvieworganizationsettings"/]</span>
            </div>
        </div>
        <div class="clear">&nbsp;</div>
		<!--Eleventh Table Starts-->
        <div class="span-22 borders">
        	<div class="span-22 bluediv">
            	<span><input type="checkbox"  id="10"/></span><span>[@spring.message "systeminformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox"  id="10_0"/></span><span class="span-10 borderrt">[@spring.message "canviewsysteminformation"/]</span>
                <span class="span-8">[@spring.message "canviewsysteminformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
            	<span class="span-2 rightAlign"><input type="checkbox"  id="10_1"/></span><span class="span-10 borderrt">[@spring.message "canshutdownMifos"/]</span>
                <span class="span-8">[@spring.message "canshutdownMifos"/]</span>
            </div>
        </div>
        <div class="clear">&nbsp;</div>
        <div class="prepend-9">
            <input class="buttn " type="button" name="submit" value="Submit" id="managerole.button.submit" onclick="#"/>
            <input class="buttn2" type="button" name="cancel" value="Cancel" id="managerole.button.cancel" onclick="#"/>
        </div>
		<!--End of All Tables-->
	</div>
   	</form> 
  </div><!--Main Content Ends-->
  [@mifos.footer/]