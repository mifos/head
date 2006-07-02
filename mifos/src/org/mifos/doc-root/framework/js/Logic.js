/**

 * Logic.js    version: 1

 

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



/* ===================================start of logic for tool tip======================= */
       
/**
* This function shows the tooltips to the user when user moves the mouse
* over the listbox item
* @param current current element on which mouse is currently poined 
* @param e  event object contains the event information
* @param text  text to show to user
*/
   
     function showtip(current,e,text)
        { 
	if ((current.type == "select-one") )
	{
		if(current.length <1)
		{
			return;
		}

		if(current.selectedIndex >-1)
		{
			text=current.options[current.selectedIndex ].text;
		}
	}
	else if (current.type == "select-multiple")
	{
		if(current.length <1)
		{
			return;
		}

		if(current.selectedIndex >-1)
		{
			text="";

			var selLength = current.length;		
			var i;
			for(i=0; i <= selLength-1; i++)
			{
				if(current.options[i].selected)
				{
					if(text=="")
					{
 						text=current.options[i ].text;
					}
					else text=text +"<br>" + current.options[i ].text;
				}
			}
		}
	}

         if (document.layers) // Netscape 4.0+
            {
             theString="<DIV CLASS='ttip'>"+text+"</DIV>"
             document.tooltip.document.write(theString)
             document.tooltip.document.close()
             document.tooltip.left=e.pageX+5
             document.tooltip.top=e.pageY+5
             document.tooltip.visibility="show"
            }
         else
           {
            if(document.getElementById) // Netscape 6.0+ and Internet Explorer 5.0+
              {
               elm=document.getElementById("tooltip")
               elml=current
               elm.innerHTML=text
               elm.style.height=elml.style.height
               elm.style.top=parseInt(elml.offsetTop+elml.offsetHeight)
               elm.style.left=parseInt(elml.offsetLeft+elml.offsetWidth+10)
              elm.style.zIndex=100;
               elm.style.visibility = "visible"
              }
           }
        }

/**
* This function hide the tool tips
*/
function hidetip(){
if (document.layers) // Netscape 4.0+
   {
    document.tooltip.visibility="hidden"
   }
else
  {
   if(document.getElementById) // Netscape 6.0+ and Internet Explorer 5.0+
     {
     elm=document.getElementById("tooltip");	
      elm.style.visibility="hidden"
     }
  } 
}


/* ==================End of tooltip logic ========================*/

/* =================Start of text moveing logic===================== */

/**
* This  function  adds option to the listbox used as a helper function by move options
* @param theSel  the list box to which you want to add the text
* @param theText  the text you want to add
* @param theValue the value you want to associate with the text
*/
function addOption(theSel, theText, theValue)
{
	var newOpt = new Option(theText, theValue);
	var selLength = theSel.length;
	theSel.options[selLength] = newOpt;
}
/**
* This  function  delete option from the listbox
* @param theSel  listbox from which you want to delete the option
* @param theIndex the index of the text which you want top delete
*/
function deleteOption(theSel, theIndex)
{
	var selLength = theSel.length;
	if(selLength>0)
		{
			theSel.options[theIndex] = null;
		}
}

/**
* This  function  move the text from the one list box to other list box
* @param theSelFrom the list box from which you want to move the text
* @param  theSelTo the list box to which you want to add the text 
*/
function moveOptions(theSelFrom, theSelTo)
{
	var selLength = theSelFrom.length;
	var selectedText = new Array();
	var selectedValues = new Array();
	var selectedCount = 0;
	var i;
	for(i=selLength-1; i>=0; i--)
	{
		if(theSelFrom.options[i].selected)
		{
			selectedText[selectedCount] = theSelFrom.options[i].text;
			selectedValues[selectedCount] = theSelFrom.options[i].value;
			deleteOption(theSelFrom, i);
			selectedCount++;
		}
	}
	for(i=selectedCount-1; i>=0; i--)
	{
		addOption(theSelTo, selectedText[i], selectedValues[i]);
	}
} 

/**
* Function to select the all option seleted by the user 
* @param outSel the list box whole all values you want to select 
*/
 function transferData(outSel)
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



