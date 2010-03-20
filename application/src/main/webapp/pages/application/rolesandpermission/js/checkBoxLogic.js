/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

/* This function contains the checkbox logic related to the rols and permission creation 
 * and updation page 
 * arg -box takes the current selected element 
 *
 */
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
          {	//get pointer to current element:
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

  function goToCancelPage(){
	document.rolesPermissionsActionForm.action="rolesPermission.do?method=viewRoles";	
	document.rolesPermissionsActionForm.submit();
  }
  
    function  submitAdminLink()
	{
		document.rolesPermissionsActionForm.action="rolesPermission.do?method=cancel";
		document.rolesPermissionsActionForm.submit();
	}  
     function  submitRolesAndPermissionLink()
	{
		document.rolesPermissionsActionForm.action="rolesPermission.do?method=viewRoles";
		document.rolesPermissionsActionForm.submit();
	}



function submitMe()
{
	
	var arrElements = document.getElementsByTagName("input");
	         for (var i=0; i<arrElements.length; i++) 
	          {
				if ( arrElements[i].checked)
					{
						document.rolesPermissionsActionForm.action="rolesPermission.do?method=update";
						document.rolesPermissionsActionForm.submit();
             			return true;
             			
					}
	          }
	alert("Role Can not be entered with out activities");
	return false;
}
function submitCreate()
{
	
	var arrElements = document.getElementsByTagName("input");
	         for (var i=0; i<arrElements.length; i++) 
	          {
				if ( arrElements[i].checked)
					{
						document.rolesPermissionsActionForm.action="rolesPermission.do?method=create";
						document.rolesPermissionsActionForm.submit();
						return true;
             			
					}
	          }
	alert("Role Can not be entered with out activities");
	return false;
}

