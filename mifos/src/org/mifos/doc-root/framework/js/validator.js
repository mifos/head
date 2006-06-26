var i=0

function meetingpopup()
{
	window.open("schedulemeetingpopup.htm",null,"height=400,width=800,status=yes,scrollbars=yes,toolbar=no,menubar=no,location=no");
}


function valid()
{		

	if(document.getElementsByName('text')[0].value)	
	{
		var para = document.getElementById("myDiv");

		var newdiv  = document.createElement("TD"); 

		var divIdName = "my"+i+"Div";

		newdiv.setAttribute("id",divIdName);

		newdiv.innerHTML +="<input type='checkbox'  name='value("+i+")' value='"+document.getElementsByName('text')[0].value+"' >"+ document.getElementsByName('text')[0].value;
		
		para.appendChild(newdiv);
		
		i++;

		document.getElementsByName('text')[0].value="";
	}

	else
	{
		alert("Items are Mandatory");
		event.returnValue = false;
		document.forms[0].text.focus();
	}
	
}


function RemoveSelected()
{
	
	arrElements = document.getElementsByTagName("input");

	for( x = 0; x < arrElements.length ;x++)
	{

		element = arrElements[x];
		
		if(element.checked)
		{
			removeEvent(element.parentNode);
			x--;
		}
		
		
	}
}



function removeEvent(divNum)
{

	var d = document.getElementById("myDiv");
	
	d.removeChild(divNum);

}



function validateFields(form)
{	

	
	if(document.forms[0].type.value=="null" || document.forms[0].name.value=="")
	{
	alert("Name is Mandatory");
	event.returnValue = false;
	document.forms[0].name.focus();
	
		
	}

		
	else if(document.forms[0].type.value=="null" || document.forms[0].type.value=="")
	{
	alert("Type is Mandatory");
	event.returnValue=false;
	document.forms[0].type.focus();
	}


	else if (document.forms[0].status.value=="null" )
	{

	alert("Displayed When moving into Status is Mandatory");
	event.returnValue=false;
	document.forms[0].status.focus();
	}

 
	else if(document.forms[0].statusCheckList.value=="null" || document.forms[0].statusCheckList.value=="")
	{

	alert("Status of CheckList is Mandatory");
	event.returnValue=false;
	document.forms[0].statuschecklist.focus();
	}
	
	
}


function update_item_list ()
{

	var cat_selobj = document.forms[0]. type;
	var item_selobj = document.forms[0]. status;
	var choice = cat_selobj . options [cat_selobj . selectedIndex] . value;
	if (choice == "Client")
	{
		
		item_selobj . options [0] = new Option ("Partial Application");
		item_selobj . options [1] = new Option ("Pending Approval");
		item_selobj . options [2] = new Option ("Approved/Active");
		item_selobj . options [3] = new Option ("Closed");
		item_selobj . options [4] = new Option ("Hold");
		item_selobj . options [5] = new Option ("Cancel");
		delete_extra_options (item_selobj, 6);
		
		
	}
	else if (choice == "Group")
	{
		item_selobj . options [0] = new Option ("Partial Group Application");
		item_selobj . options [1] = new Option ("Pending Approval");
		item_selobj . options [2] = new Option ("Approved/Active");
		item_selobj . options [3] = new Option ("Hold");
		item_selobj . options [4] = new Option ("Closed")
		item_selobj . options [5] = new Option ("Cancel");
		delete_extra_options (item_selobj, 6);
	}

	else if (choice == "Savings")
	{
		item_selobj . options [0] = new Option ("Partial Application");
		item_selobj . options [1] = new Option ("pending Approval");
		item_selobj . options [2] = new Option ("Approved/Active in good Standing");
		item_selobj . options [3] = new Option ("Inactive");
		item_selobj . options [4] = new Option ("Closed")
		item_selobj . options [5] = new Option ("Cancel");
		delete_extra_options (item_selobj, 6);
		
	}

	else if (choice == "Loans")
	{
		item_selobj . options [0] = new Option ("Partial Application");
		item_selobj . options [1] = new Option ("Pending Approval");
		item_selobj . options [2] = new Option ("Approved");
		item_selobj . options [3] = new Option ("Disbursed to Loan Officer");
		item_selobj . options [4] = new Option ("Active in good Standing")
		item_selobj . options [5] = new Option ("Closed/Obligation met");
		item_selobj . options [6] = new Option ("Closed/Rescheduled");	
		item_selobj . options [7] = new Option ("Active-Bad Standing");
		item_selobj . options [8] = new Option ("Closed-Written Off ");
		item_selobj . options [9] = new Option ("Cancel");
		delete_extra_options (item_selobj, 10);
	}
}

function delete_extra_options (selobj, size)
{
		while (selobj . options . length > size)
		selobj . options [selobj . options . length - 1] = null;
}