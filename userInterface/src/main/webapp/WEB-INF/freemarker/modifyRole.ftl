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
<head>
<script>
function doCheck(box)
{
var arrElements = document.getElementsByTagName("input");
var myname= box.id;
var mylength=myname.length;
var elementCache =new Array();
var allChecked=true;    
var indexof_ = myname.indexOf('_');
var topElementName=myname.substring(0,indexof_);
var index=0;
    if ( box.checked)
     {

        
        //we are iterating over the all the elements of type input on the page 
         for (var i=0; i<arrElements.length; i++) 
          {
                    //get pointer to current element: and get name and id of the current element
                    var name=arrElements[i].id;
                    var length= name.length;
                    
                   /*
                   We have assigned the id's in hierarichal manner ie if parent is 0 first child is 0_0
                   second child is 0_1 etc so if length of the current element is grater than the checked
                   element the it may be the child of the current child element
                   */
                   
                   
                   /* Bug 26428  it was matching the unwanted childs also
                    changed how we are getting the child
                   */
                   
                    if( length > mylength+1)
                        {
                             var substr=name.substring(0,mylength+1);
                             //if this element is starting with the name of the current selected 
                             //item then we need to check it as it is a child of the current seleted item
                            if ( substr == myname+"_")
                                  arrElements[i].checked=true;
                        }
                        
                     var idxof_ = name.indexOf('_');
                     
                    //if this element id name has _ then this is of our interest
                     if( idxof_ >0)
                     {
                           //store all the element in a cache so that later on we need not to iterate 
                           //over all element again
                            if( name.substring(0,idxof_) == topElementName)
                            {
                                elementCache[index++]=arrElements[i];    
                            }
                    }
                    
         }
          // time to see whether we have checked all current level nodes

        /*
        Next is logic that if we have selected all the current level elements then we need to 
        select the parent also and so on ...
        */         
           while(myname.length >= 3)
        {
            
            indexof_=myname.lastIndexOf('_'); 
            
            //get the parent of this element
            var parentName = myname.substring(0,indexof_);
            //make the regular expression to match all the element at current level
            var pattern= new RegExp("^"+parentName+"_.");
                 for (var i=0; i<elementCache.length; i++) 
                  {    
                        if( true == pattern.test(elementCache[i].id))
                            { 
                                if( elementCache[i].checked==false)
                                    {
                                        allChecked=false;
                                        break;
                                    }
                            }
                   }
                if (allChecked)
                    {
                        var parentElement = document.getElementById(parentName);
                        if ( null != parentElement)
                        {
                            parentElement.checked=true;
                        }
                        myname=parentName;
                    }
                    else
                    {
                        break;
                    }

                              
        }



     }
    else
   {
   
   // if user unchecks the checkbox we can uncheck all his childern
         for (var i=0; i<arrElements.length; i++) 
          {    //get pointer to current element:
                   var name=arrElements[i].id;
   
   
                    var length= name.length;
 
    //26505  fixed for unchecking ---
                     if( length > mylength+1)
                        {
                             var substr=name.substring(0,mylength+1);
                             //if this element is starting with the name of the current selected 
                             //item then we need to check it as it is a child of the current seleted item
                            if ( substr == myname+"_")
                                  arrElements[i].checked=false;
                        }
 
 
 /*
                    if( length > mylength)
                        {
                             var substr=name.substring(0,mylength);
                                if ( substr == myname)
                                      arrElements[i].checked=false;
                        }
                        
 */                                        
          }
    // time to unchek parents
           while(myname.length >= 3)
        {
            indexof_=myname.lastIndexOf('_'); 
            var parentName = myname.substring(0,indexof_);
            var parentElement = document.getElementById(parentName);
            parentElement.checked=false;
            myname=parentName;
        }



   }   

}
</script>
</head>
 
   <!--  Main Content Begins-->  
  <div class=" content">
      <form method="POST" action="modifyRole.ftl" name="formname">
    <div class="span-24 marginLeft30">
          <div class="span-22 bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<a href="manageRolesAndPermissions.ftl">[@spring.message "systemusers.managerolesandpermissions.rolesandpermissions"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "admin"/]</span></div>
        <div class="clear">&nbsp;</div>
        <p class="font15"><span class=" orangeheading">[@spring.message "manageRoles.modifyRole"/]</span></p>
        <div>[@spring.message "manageRoles.Checktheboxesbelowtoallowpermissionsforthespecifiedrole."/]<br />[@spring.message "manageRoles.rolesAndPermissionswithoutsavingchanges"/]</div>
        <div class="error">${errorMessage?if_exists}</div>
        <div><span>[@spring.message "manageRoles.roleName"/] </span><span><input type="text" name="roleName" value="${user}" /></span><span><input type="hidden" name="roleId" value="${roleId}" /></span></div>
        <div class="clear">&nbsp;</div>
        <div class="span-22 borders">
            <div class="span-22 bluediv">
                <span><input type="checkbox"  name="activity(0)" value="checkbox" id="0" onclick="doCheck(this)"/></span><span>[@spring.message "manageRoles.organizationManagement"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(1)" value="checkbox" id="0_0" onclick="doCheck(this)" /></span><span class="span-10 borderrt">[@spring.message "manageRoles.funds"/]</span>
                <span class="span-8">[@spring.message "manageRoles.funds"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(2)" value="3" id="0_0_0" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.cancreatefunds"/]</span>
                <span class="span-8">[@spring.message "manageRoles.cancreatefunds"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(3)" value="4" id="0_0_1" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canmodifyfunds"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canmodifyfunds"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(4)" value="checkbox" id="0_1" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.fees"/]</span>
                <span class="span-8">[@spring.message "manageRoles.fees"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(5)" value="6" id="0_1_0" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.candefinenewfeetype"/]</span>
                <span class="span-8">[@spring.message "manageRoles.candefinenewfeetype"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(6)" value="7"  id="0_1_1" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canmodifyfeeinformation"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canmodifyfeeinformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" id="0_2" name="activity(7)" value="checkbox" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.checklists"/]</span>
                <span class="span-8">[@spring.message "manageRoles.checklists"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(8)" value="9"  id="0_2_0" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.candefinenewchecklisttype"/]</span>
                <span class="span-8">[@spring.message "manageRoles.candefinenewchecklisttype"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"  name="activity(9)" value="10" id="0_2_1" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canmodifychecklistinformation"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canmodifychecklistinformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox"  name="activity(10)"  value="219" id="0_3" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.candefineAcceptedPaymentType"/]</span>
                <span class="span-8">[@spring.message "manageRoles.candefineAcceptedPaymentType"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(11)" value="235"  id="0_4" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.candefineHoliday"/]</span>
                <span class="span-8">[@spring.message "manageRoles.candefineHoliday"/]</span>
            </div>
        </div>
        <div class="clear">&nbsp;</div>
        <!--Second Table Starts-->
        <div class="span-22 borders">
            <div class="span-22 bluediv">
                <span><input type="checkbox" id="1" name="activity(12)"value="checkbox" onclick="doCheck(this)"/></span><span>[@spring.message "manageRoles.officemanagement"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(13)" value="checkbox"  id="1_0" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.offices"/]</span>
                <span class="span-8">[@spring.message "manageRoles.offices"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(14)" value="15" id="1_0_0" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.cancreatenewoffice"/]</span>
                <span class="span-8">[@spring.message "manageRoles.cancreatenewoffice"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(15)" value="16" id="1_0_1" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canmodifyofficeinformation"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canmodifyofficeinformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(16)" value="193" id="1_1" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canmodifyofficehierarchy"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canmodifyofficehierarchy"/]</span>
            </div></div>
        <!--Third Table Starts-->
        <div class="clear">&nbsp;</div>
        <div class="span-22 borders">
            <div class="span-22 bluediv">
                <span><input type="checkbox" id="2"  name="activity(17)" value="checkbox"  onclick="doCheck(this)"/></span><span>[@spring.message "manageRoles.usermanagement"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(18)" value="checkbox"  id="2_0" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.personnel"/]</span>
                <span class="span-8">[@spring.message "manageRoles.personnel"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(19)" value="19" id="2_0_0" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.cancreatenewsystemusers"/]</span>
                <span class="span-8">[@spring.message "manageRoles.cancreatenewsystemusers"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(20)" value="20"  id="2_0_1" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canmodifyuserinformation"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canmodifyuserinformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(21)" value="21"  id="2_0_2" onclick="doCheck(this)" /></span><span class="span-9 borderrt">[@spring.message "manageRoles.canunlockause"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canunlockause"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"  name="activity(22)" value="135"  id="2_0_3" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.caneditselfinformation"/]</span>
                <span class="span-8">[@spring.message "manageRoles.caneditselfinformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(23)" value="194" id="2_0_4" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canAddNotestoPersonnel"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canAddNotestoPersonnel"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(24)" value="checkbox" id="2_1" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.roles"/]</span>
                <span class="span-8">[@spring.message "manageRoles.roles"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"  name="activity(25)" value="23" id="2_1_0" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.cancreatenewrole"/]</span>
                <span class="span-8">[@spring.message "manageRoles.cancreatenewrole"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"  name="activity(26)" id="2_1_1" value="24" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canmodifyarole"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canmodifyarole"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"  name="activity(27)" id="2_1_2" value="25" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.candeletearole"/]</span>
                <span class="span-8">[@spring.message "manageRoles.candeletearole"/]</span>
            </div>
        </div>
        <!--Fourth Table Starts-->
        <div class="clear">&nbsp;</div>
        <div class="span-22 borders">
            <div class="span-22 bluediv">
                <span><input type="checkbox"  name="activity(28)" value="checkbox" id="3" onclick="doCheck(this)"/></span><span>[@spring.message "manageRoles.clientManagement"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(29)"  value="checkbox" id="3_0" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.clients"/]</span>
                <span class="span-8">[@spring.message "manageRoles.clients"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(30)" value="35" id="3_0_0" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.cancreatenewClientinsaveforlaterstate"/]</span>
                <span class="span-8">[@spring.message "manageRoles.cancreatenewClientinsaveforlaterstate"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(31)" value="36" id="3_0_1" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.cancreatenewClientinsubmitforapprovalstate"/]</span>
                <span class="span-8">[@spring.message "manageRoles.cancreatenewClientinsubmitforapprovalstate"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(32)" value="37" id="3_0_2" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canchangestatetopartialapplication"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canchangestatetopartialapplication"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(33)" value="38"  id="3_0_3" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canchangestatetoactive"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canchangestatetoactive"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"  name="activity(34)" value="39"  id="3_0_4" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canchangestatetoCancelled"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canchangestatetoCancelled"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(35)" value="40" id="3_0_5" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canchangestatetoOnHold"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canchangestatetoOnHold"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(36)" value="41" id="3_0_6" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canchangestatetoClosed"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canchangestatetoClosed"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(37)" value="42" id="3_0_7" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canchangestatetoapplicationpendingapproval"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canchangestatetoapplicationpendingapproval"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"  name="activity(38)" value="43" id="3_0_8" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canmakepaymentstoClientaccounts"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canmakepaymentstoClientaccounts"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"  name="activity(39)" value="44" id="3_0_9" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canmakeadjustmententriestoClientaccount"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canmakeadjustmententriestoClientaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"  name="activity(40)" value="46" id="3_0_10" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canwaiveadueamount"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canwaiveadueamount"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(41)" value="47" id="3_0_11" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canremovefeetypesfromClientaccount"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canremovefeetypesfromClientaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(42)" value="48" id="3_0_12" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canaddnotestoClient"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canaddnotestoClient"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"  name="activity(43)" value="49" id="3_0_13" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.caneditMFIinformation"/]</span>
                <span class="span-8">[@spring.message "manageRoles.caneditMFIinformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"  name="activity(44)" value="50" id="3_0_14" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.caneditgroupmembership"/]</span>
                <span class="span-8">[@spring.message "manageRoles.caneditgroupmembership"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(45)"value="51" id="3_0_15" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.caneditofficemembership"/]</span>
                <span class="span-8">[@spring.message "manageRoles.caneditofficemembership"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(46)" value="52" id="3_0_16" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.caneditmeetingschedule"/]</span>
                <span class="span-8">[@spring.message "manageRoles.caneditmeetingschedule"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(47)" value="53" id="3_0_17" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canadd/edithistoricadata"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canadd/edithistoricadata"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(48)" value="54" id="3_0_18" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.caneditfeeamountattachedtotheaccount"/]</span>
                <span class="span-8">[@spring.message "manageRoles.caneditfeeamountattachedtotheaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(49)" value="55" id="3_0_19" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canBlacklistaClient"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canBlacklistaClient"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(50)" value="121" id="3_0_20" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canspecifymeetingschedule"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canspecifymeetingschedule"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(51)"  value="126" id="3_0_21" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.caneditpersonalinformation"/]</span>
                <span class="span-8">[@spring.message "manageRoles.caneditpersonalinformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(52)" value="198" id="3_0_22" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canapplychargestoClientaccounts"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canapplychargestoClientaccounts"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(53)" value="206" id="3_0_23" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canremoveclientsfromgroups"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canremoveclientsfromgroups"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(54)" value="208" id="3_0_24" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canaddanexistingclienttoagroup"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canaddanexistingclienttoagroup"/]</span>
            </div>
            
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox"  name="activity(55)" value="checkbox"  id="3_1" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.groups"/]</span>
                <span class="span-8">[@spring.message "manageRoles.groups"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(56)" value="57" id="3_1_0" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.cancreatenewgroupinsaveforlaterstate"/]</span>
                <span class="span-8">[@spring.message "manageRoles.cancreatenewgroupinsaveforlaterstate"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(57)" value="58" id="3_1_1" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.cancreatenewgroupinsubmitforapprovalstate"/]</span>
                <span class="span-8">[@spring.message "manageRoles.cancreatenewgroupinsubmitforapprovalstate"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(58)" value="59" id="3_1_2" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canchangestatetopartialapplication"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canchangestatetopartialapplication"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"  name="activity(59)" value="60" id="3_1_3" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canchangestatetoactive"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canchangestatetoactive"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"  name="activity(60)" value="61" id="3_1_4" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canchangestatetoCancelled"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canchangestatetoCancelled"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"  name="activity(61)" value="62" id="3_1_5" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canchangestatetoOnHold"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canchangestatetoOnHold"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(62)" value="63" id="3_1_6" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canchangestatetoClosed"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canchangestatetoClosed"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(63)"  value="64" id="3_1_7" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canchangestatetoapplicationpendingapproval"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canchangestatetoapplicationpendingapproval"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(64)" value="65" id="3_1_8" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canmakepaymentstogroupaccounts"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canmakepaymentstogroupaccounts"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(65)" value="66" id="3_1_9" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canmakeadjustmententriestogroupaccount"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canmakeadjustmententriestogroupaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"  name="activity(66)" value="68" id="3_1_10" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canwaiveadueamount"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canwaiveadueamount"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(67)" value="69" id="3_1_11" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canremovefeetypesfromgroupaccount"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canremovefeetypesfromgroupaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(68)"  value="70" id="3_1_12" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canaddnotestogroup"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canaddnotestogroup"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"  name="activity(69)" value="71" id="3_1_13" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.caneditGroupinformation"/]</span>
                <span class="span-8">[@spring.message "manageRoles.caneditGroupinformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(70)"  value="72" id="3_1_14" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.caneditCenterClientship"/]</span>
                <span class="span-8">[@spring.message "manageRoles.caneditCenterClientship"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(71)" value="73" id="3_1_15" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.caneditofficemembership"/]</span>
                <span class="span-8">[@spring.message "manageRoles.caneditofficemembership"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(72)" value="74" id="3_1_16" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.caneditmeetingschedule"/]</span>
                <span class="span-8">[@spring.message "manageRoles.caneditmeetingschedule"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(73)" value="75" id="3_1_17" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canadd/edithistoricaldata"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canadd/edithistoricaldata"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(74)" value="76" id="3_1_18" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.caneditfeeamountattachedtotheaccount"/]</span>
                <span class="span-8">[@spring.message "manageRoles.caneditfeeamountattachedtotheaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(75)" value="77" id="3_1_19" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canBlacklistagroup"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canBlacklistagroup"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(76)" value="122" id="3_1_20" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canspecifymeetingschedule"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canspecifymeetingschedule"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(77)" value="129" id="3_1_21" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canapplychargestogroupaccounts"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canapplychargestogroupaccounts"/]</span>
            </div>
            
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(78)" value="checkbox" id="3_2" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.centers"/]</span>
                <span class="span-8">[@spring.message "manageRoles.centers"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(79)" id="3_2_0" value="79" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.cancreatenewCenter"/]</span>
                <span class="span-8">[@spring.message "manageRoles.cancreatenewCenter"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(80)" id="3_2_1" value="80" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canmodifyCenterinformation"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canmodifyCenterinformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(81)" id="3_2_2" value="81" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.caneditCenterstatus"/]</span>
                <span class="span-8">[@spring.message "manageRoles.caneditCenterstatus"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(82)" id="3_2_3" value="82" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canmakepaymentstoCenteraccounts"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canmakepaymentstoCenteraccounts"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(83)" id="3_2_4" value="83" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canmakeadjustmententriestoCenteraccount"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canmakeadjustmententriestoCenteraccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(84)" id="3_2_5" value="85" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canwaiveadueamount"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canwaiveadueamount"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(85)" id="3_2_6" value="86" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canremovefeetypesfromCenteraccount"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canremovefeetypesfromCenteraccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(86)" id="3_2_7" value="87" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canaddnotestoCenterrecords"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canaddnotestoCenterrecords"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"  name="activity(87)" id="3_2_8" value="88" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.caneditfeeamountattachedtotheaccount"/]</span>
                <span class="span-8">[@spring.message "manageRoles.caneditfeeamountattachedtotheaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"  name="activity(88)" id="3_2_9" value="127" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.caneditmeetingschedule"/]</span>
                <span class="span-8">[@spring.message "manageRoles.caneditmeetingschedule"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"  name="activity(89)" id="3_2_10" value="128" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canspecifymeetingschedule"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canspecifymeetingschedule"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(90)"id="3_2_11" value="200" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canapplychargestoCenteraccounts"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canapplychargestoCenteraccounts"/]</span>
            </div>
        </div>
        <div class="clear">&nbsp;</div>
        <!--Fifth Table Starts-->
        <div class="span-22 borders">
            <div class="span-22 bluediv">
                <span><input type="checkbox"  name="activity(91)" id="4" value="checkbox" onclick="doCheck(this)"/></span><span>[@spring.message "manageRoles.productdefinition"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" id="4_0"  name="activity(92)" value="checkbox" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.productCategories"/]</span>
                <span class="span-8">[@spring.message "manageRoles.productCategories"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(93)" value="91" id="4_0_0" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.candefinenewproductcategories"/]</span>
                <span class="span-8">[@spring.message "manageRoles.candefinenewproductcategories"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(94)"  value="92" id="4_0_1" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.caneditproductcategoryinformation"/]</span>
                <span class="span-8">[@spring.message "manageRoles.caneditproductcategoryinformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(95)" value="195"  id="4_0_2" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canmodifylateness/dormancydefinition"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canmodifylateness/dormancydefinition"/]</span>
            </div>
            
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox"  name="activity(96)" value="checkbox" id="4_1" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.loanProducts"/]</span>
                <span class="span-8">[@spring.message "manageRoles.loanProducts"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" id="4_1_0" name="activity(97)"  value="94"  onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.candefinenewloanproductinstance"/]</span>
                <span class="span-8">[@spring.message "manageRoles.candefinenewloanproductinstance"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" id="4_1_1" name="activity(98)"  value="95"  onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.caneditloanproductinstances"/]</span>
                <span class="span-8">[@spring.message "manageRoles.caneditloanproductinstances"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" value="checkbox" name="activity(99)"  id="4_2" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.savingsProducts"/]</span>
                <span class="span-8">[@spring.message "manageRoles.savingsProducts"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" id="4_2_0"  name="activity(100)"  value="97" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.candefinenewSavingsproductinstance"/]</span>
                <span class="span-8">[@spring.message "manageRoles.candefinenewSavingsproductinstance"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" id="4_2_1" name="activity(101)" value="98" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.caneditSavingsproductinstances"/]</span>
                <span class="span-8">[@spring.message "manageRoles.caneditSavingsproductinstances"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" id="4_3" name="activity(102)" value="checkbox" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.productMix"/]</span>
                <span class="span-8">[@spring.message "manageRoles.productMix"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" id="4_3_0" name="activity(103)"  value="210" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.candefineproductmix"/]</span>
                <span class="span-8">[@spring.message "manageRoles.candefineproductmix"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" id="4_3_1"  name="activity(104)" value="211" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.caneditproductmix"/]</span>
                <span class="span-8">[@spring.message "manageRoles.caneditproductmix"/]</span>
            </div>
            
        </div>
        <div class="clear">&nbsp;</div>
        <!--Sixth Table Starts-->
        <div class="span-22 borders">
            <div class="span-22 bluediv">
                <span><input type="checkbox"  id="5"  name="activity(105)" value="checkbox" onclick="doCheck(this)"/></span><span>[@spring.message "manageRoles.loanmanagement"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox"  name="activity(106)" value="checkbox"  id="5_0" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.loanProcessing"/]</span>
                <span class="span-8">[@spring.message "manageRoles.loanProcessing"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"  name="activity(107)" value="101" id="5_0_0" onclick="doCheck(this)" /></span><span class="span-9 borderrt">[@spring.message "manageRoles.cancreatenewloanaccountinSaveforlaterstate"/]</span>
                <span class="span-8">[@spring.message "manageRoles.cancreatenewloanaccountinSaveforlaterstate"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(108)" value="102" id="5_0_1"  onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.cancreatenewloanaccountinSubmitforapprovalstate"/]</span>
                <span class="span-9">[@spring.message "manageRoles.cancreatenewloanaccountinSubmitforapprovalstate"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(109)" value="103" id="5_0_2" onclick="doCheck(this)" /></span><span class="span-9 borderrt">[@spring.message "manageRoles.canchangestatetopartialapplication"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canchangestatetopartialapplication"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(110)" value="104" id="5_0_3"  onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canchangestatetoApproved"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canchangestatetoApproved"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(111)" value="105" id="5_0_4" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canchangestatetoCancelled"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canchangestatetoCancelled"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(112)" value="106" id="5_0_5" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canchangestatetoDisbursedtoLO"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canchangestatetoDisbursedtoLO"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(113)" value="108" id="5_0_6" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canchangestatetopendingapproval"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canchangestatetopendingapproval"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(114)"  value="109" id="5_0_7" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canchangestatetoClosed-Writtenoff"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canchangestatetoClosed-Writtenoff"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(115)" value="110" id="5_0_8" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canchangestatetoClosed-rescheduled"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canchangestatetoClosed-rescheduled"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(116)" value="129" id="5_0_9" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.caneditLoanaccountinformation"/]</span>
                <span class="span-8">[@spring.message "manageRoles.caneditLoanaccountinformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"name="activity(117)" value="179" id="5_0_10" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canaddnotestoloanaccount"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canaddnotestoloanaccount"/]</span>
            </div>
            
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(118)" id="5_1" value="checkbox"  onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.loanTransactions"/]</span>
                <span class="span-8">[@spring.message "manageRoles.loanTransactions"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(119)" value="115" id="5_1_0" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canmakepaymenttotheaccount"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canmakepaymenttotheaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(120)" value="116"  id="5_1_1" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canmakeadjustmententrytotheaccount"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canmakeadjustmententrytotheaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(121)" value="118" id="5_1_2" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canwaivepenalty"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canwaivepenalty"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(122)" value="119" id="5_1_3" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canwaiveafeeinstallment"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canwaiveafeeinstallment"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(123)"  value="120" id="5_1_4" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canremovefeetypesattachedtotheaccount"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canremovefeetypesattachedtotheaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"  name="activity(124)" value="131" id="5_1_5" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canapplychargestoloans"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canapplychargestoloans"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(125)" value="178" id="5_1_6" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canrepayloan"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canrepayloan"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(126)"  value="189" id="5_1_7" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.candisburseloan"/]</span>
                <span class="span-8">[@spring.message "manageRoles.candisburseloan"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"  name="activity(127)" value="217" id="5_1_8" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canadjustpaymentwhen"/]</span>
                <span class="span-9">[@spring.message "manageRoles.canadjustpaymentwhen"/]</span>
            </div>
            
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(128)" value="202" id="5_2" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canreverseLoandisbursals"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canreverseLoandisbursals"/]</span>
            </div>

            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox"  name="activity(129)" value="218" id="5_3" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canredoLoandisbursals"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canredoLoandisbursals"/]</span>
            </div>
        </div>
        <div class="clear">&nbsp;</div>
        <!--Seventh Table Starts-->
        <div class="span-22 borders">
            <div class="span-22 bluediv">
                <span><input type="checkbox"  name="activity(130)" value="checkbox"  id="6" onclick="doCheck(this)"/></span><span>[@spring.message "manageRoles.savingsManagement"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(131)" value="137"  id="6_0" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.cancreatenewSavingsaccountinSaveforlaterstate"/]</span>
                <span class="span-8">[@spring.message "manageRoles.cancreatenewSavingsaccountinSaveforlaterstate"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(132)" value="138"  id="6_1" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canupdateSavingsaccount"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canupdateSavingsaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(133)" value="139"  id="6_2"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.cancloseSavingsaccount"/]</span>
                <span class="span-8">[@spring.message "manageRoles.cancloseSavingsaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox"  name="activity(134)" value="140"  id="6_3" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canchangestatetopartialapplication"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canchangestatetopartialapplication"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(135)" value="180"  id="6_4" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canchangestatetopendingapproval"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canchangestatetopendingapproval"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(136)" value="181"  id="6_5" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canchangestatetocancel"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canchangestatetocancel"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox"  name="activity(137)" value="182" id="6_6" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canchangestatetoactive"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canchangestatetoactive"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(138)"  value="183"  id="6_7" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canchangestatetoinactive"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canchangestatetoinactive"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox"  name="activity(139)" value="184"  id="6_8" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canblacklistSavingsaccount"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canblacklistSavingsaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(140)"  value="185"  id="6_9" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.cancreatenewSavingsaccountinsubmitforapprovalstate"/]</span>
                <span class="span-9">[@spring.message "manageRoles.cancreatenewSavingsaccountinsubmitforapprovalstate"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(141)"  value="186"  id="6_10" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.candoadjustmentsforsavingsaccount"/]</span>
                <span class="span-8">[@spring.message "manageRoles.candoadjustmentsforsavingsaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(142)"  value="187"  id="6_11" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canwaiveduedepositsforSavingsaccount"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canwaiveduedepositsforSavingsaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(143)"  value="188"  id="6_12" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canwaiveoverduedepositsforSavingsaccount"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canwaiveoverduedepositsforSavingsaccount"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(144)"  value="190"  id="6_13" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canmakedeposit/withdrawaltosavingsaccounta"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canmakedeposit/withdrawaltosavingsaccounta"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox"  name="activity(145)" value="191"  id="6_14" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canaddnotestoSavingsaccount"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canaddnotestoSavingsaccount"/]</span>
            </div>
        </div>
        <div class="clear">&nbsp;</div>
        <!--Eight Table Starts-->
        <div class="span-22 borders">
            <div class="span-22 bluediv">
                <span><input type="checkbox" id="7" name="activity(146)" value="checkbox"  onclick="doCheck(this)"/></span><span>[@spring.message "manageRoles.reportsManagement"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(147)"  value="checkbox"  id="7_0" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.clientDetail"/]</span>
                <span class="span-8">[@spring.message "manageRoles.clientDetail"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(148)" value="229" id="7_0_0" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canviewCollectionSheetReport"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canviewCollectionSheetReport"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(149)" value="146"  id="7_1" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.center"/]</span>
                <span class="span-8">[@spring.message "manageRoles.center"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(150)"  value="147" id="7_2" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.status"/]</span>
                <span class="span-8">[@spring.message "manageRoles.status"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(151)" value="148" id="7_3" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.performance"/]</span>
                <span class="span-8">[@spring.message "manageRoles.performance"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox"  name="activity(152)" value="149" id="7_4" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.loanProductDetail"/]</span>
                <span class="span-8">[@spring.message "manageRoles.loanProductDetail"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(153)" value="checkbox" id="7_5" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.analysis"/]</span>
                <span class="span-8">[@spring.message "manageRoles.analysis"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(154)"  value="231" id="7_5_0" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.permissions.CanViewBranchCashConfirmationReport"/]</span>
                <span class="span-8">[@spring.message "manageRoles.permissions-CanViewBranchCashConfirmationReport"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox" name="activity(155)"  value="232" id="7_5_1" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.permissions.CanViewBranchProgressReport"/]</span>
                <span class="span-8">[@spring.message "manageRoles.permissions-CanViewBranchProgressReport"/]</span>
            </div>
             <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"  name="activity(156)" value="236" id="7_5_2" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canViewDetailedAgingOfPortfolioAtRisk"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canViewDetailedAgingOfPortfolioAtRisk"/]</span>
            </div>
             <div class="span-22 borderbtm">
                <span class="span-3 rightAlign"><input type="checkbox"  name="activity(157)"  value="237" id="7_5_3" onclick="doCheck(this)"/></span><span class="span-9 borderrt">[@spring.message "manageRoles.canViewGeneralLedgerReport"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canViewGeneralLedgerReport"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(158)" value="151" id="7_6" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.miscellaneous"/]</span>
                <span class="span-8">[@spring.message "manageRoles.miscellaneous"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(159)"  value="214" id="7_7" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canuploadreporttemplate"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canuploadreporttemplate"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(160)"  value="215" id="7_8" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canviewreports"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canviewreports"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(161)" value="216" id="7_9" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.caneditreportinformation"/]</span>
                <span class="span-8">[@spring.message "manageRoles.caneditreportinformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(162)"  value="220" id="7_10" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.candefinenewreportcategory"/]</span>
                <span class="span-8">[@spring.message "manageRoles.candefinenewreportcategory"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(163)"  value="221" id="7_11" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canviewreportcategory"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canviewreportcategory"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(164)"  value="222" id="7_12" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.candeletereportcategory"/]</span>
                <span class="span-8">[@spring.message "manageRoles.candeletereportcategory"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(165)"  value="223" id="7_13" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.candownloadreporttemplate"/]</span>
                <span class="span-8">[@spring.message "manageRoles.candownloadreporttemplate"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(166)"  value="225"  id="7_14" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canuploadadmindocuments"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canuploadadmindocuments"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(167)"  value="226" id="7_15" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canviewadmindocuments"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canviewadmindocuments"/]</span>
            </div>
        </div>
        <div class="clear">&nbsp;</div>
        <!--Ninth Table Starts-->
        <div class="span-22 borders">
            <div class="span-22 bluediv">
                <span><input type="checkbox"  name="activity(168)" value="checkbox"  id="8" onclick="doCheck(this)"/></span><span>[@spring.message "manageRoles.bulk"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(169)" value="192" id="8_0" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canapproveloansinbulk"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canapproveloansinbulk"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(170)" value="197" id="8_1" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canEnterCollectionSheetData"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canEnterCollectionSheetData"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(171)" value="201" id="8_2" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.cancreatemultipleLoanaccounts"/]</span>
                <span class="span-8">[@spring.message "manageRoles.cancreatemultipleLoanaccounts"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(172)" value="233" id="8_3" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canimporttransaction"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canimporttransaction"/]</span>
            </div>
        </div>
        <div class="clear">&nbsp;</div>
        <!--Tenth Table Starts-->
        <div class="span-22 borders">
            <div class="span-22 bluediv">
                <span><input type="checkbox" name="activity(173)"  value="checkbox"  id="9" onclick="doCheck(this)"/></span><span>[@spring.message "manageRoles.configurationManagement"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(174)" value="204" id="9_0" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.candefinelabels"/]</span>
                <span class="span-8">[@spring.message "manageRoles.candefinelabels"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox"  name="activity(175)" value="205"  id="9_1" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.candefinehidden/mandatoryfields"/]</span>
                <span class="span-8">[@spring.message "manageRoles.candefinehidden/mandatoryfields"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(176)" value="213" id="9_2" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canDefineLookupValues"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canDefineLookupValues"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(177)" value="224" id="9_3" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.candefinecustomfields"/]</span>
                <span class="span-8">[@spring.message "manageRoles.candefinecustomfields"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(178)"  id="9_4"  onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canvieworganizationsettings"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canvieworganizationsettings"/]</span>
            </div>
        </div>
        <div class="clear">&nbsp;</div>
        <!--Eleventh Table Starts-->
        <div class="span-22 borders">
            <div class="span-22 bluediv">
                <span><input type="checkbox" value="checkbox" name="activity(179)" id="10" onclick="doCheck(this)"/></span><span>[@spring.message "manageRoles.systeminformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox" name="activity(180)" value="228"  id="10_0" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canviewsysteminformation"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canviewsysteminformation"/]</span>
            </div>
            <div class="span-22 borderbtm">
                <span class="span-2 rightAlign"><input type="checkbox"value="239"  id="10_1" name="activity(181)" onclick="doCheck(this)"/></span><span class="span-10 borderrt">[@spring.message "manageRoles.canshutdownMifos"/]</span>
                <span class="span-8">[@spring.message "manageRoles.canshutdownMifos"/]</span>
            </div>
        </div>
        <div class="clear">&nbsp;</div>
        <div class="prepend-9">
            <input class="buttn " type="submit" name="SUBMIT" value="[@spring.message "submit"/]" id="managerole.button.submit"/>
            <input class="buttn2" type="submit" name="CANCEL" value="[@spring.message "cancel"/]" id="managerole.button.cancel"/>
        </div>
        <!--End of All Tables-->
    </div>
       </form> 
  </div><!--Main Content Ends-->
[/@adminLeftPaneLayout]