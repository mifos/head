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
function addOption(theSel, theText, theValue)
{
	var newOpt = new Option(theText, theValue);
	var selLength = theSel.length;
	theSel.options[selLength] = newOpt;
}
function deleteOption(theSel, theIndex)
{
	var selLength = theSel.length;
	if(selLength>0)
	{
	theSel.options[theIndex] = null;
	}
}
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
