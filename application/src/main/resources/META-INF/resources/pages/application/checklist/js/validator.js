/************************************************************************
* This is master file which contains all the javscript functions related to checklist
*  author : Imtiyaz Baig
**************************************************************************/



/****************************************************************************
*  createCheckList function dynamically creates check boxes with values
*******************************************************************************/
var k=0;
var i=0;
var numberOfItems=0;
var count=0;
var flag=0;
var totalDetails = 0;
var detailTxt="";

function createCheckList()
{
	var arr=document.getElementsByName("mycheckBOx");
	if(arr)
	{
		for(;count<arr.length;)
		{
			count++;
			i=count;
			numberOfItems=i;
		}
		k = numberOfItems;
		while (true)
		{
			var chkBox = document.getElementsByName("detailsList["+k+"]");
			if (chkBox.length > 0)
			{
				k++;
			}
			else
			{
				break;
			}
		}
	}

	var re = /\s/g;; //Match any white space including space, tab, form-feed, etc.
	var aTextArea=document.getElementsByName('text')[0].value;
	var str = aTextArea.replace(re, "");
	if (str.length == 0 )
	{
		//alert("Items are Mandatory");
		event.returnValue = false;
		return false;
	}
	var val = document.getElementsByName("text")[0].value;
    var qt = "'";
    var value = val.replace(qt, "&#39;");
    for (i = 0; i < val.length; i++) {
        value = value.replace(qt, "&#39;");
    }
	if(document.getElementsByName('text')[0].value)
	{
		var para = document.getElementById("myDiv");
		var detailsTxt  = document.createElement("TD");
		detailsTxt.className = "fontnormal";
		var divIdName = "my"+k+"Div";
		detailsTxt.setAttribute("id",divIdName);
		var textArea=document.getElementsByName('text')[0].value;
		detailsTxt.innerHTML +="<input type='checkbox'  name='checkBox("+k+")' value='"+value+"'>";
		var incrementer=0;
		while(incrementer<textArea.length)
		{
			var temp=incrementer;
			detailsTxt.innerHTML+=textArea.substr(temp,80);
			incrementer=incrementer+80;
		}
		detailsTxt.innerHTML +="<input type='hidden'  name='detailsList["+k+"]' value='"+value+"' >";
		para.appendChild(detailsTxt);
		k++;
		numberOfItems++;
		document.getElementsByName('text')[0].value="";
	}

}

/****************************************************************************
*  RemoveSelected function removes the selected dynamically created check boxes
*******************************************************************************/


function RemoveSelected()
{
	var arr=document.getElementsByName("mycheckBOx");
	if(arr)
	{
		for(;count<arr.length;)
		{
			count++;
			i=count;
			numberOfItems=count;
		}

	}
	arrElements = document.getElementsByTagName("input");
	var iterator=i;
	for( x = 0; x < arrElements.length ;x++)
	{
		element = arrElements[x];
		if(element.checked)
		{
			element.parentNode.parentNode.removeChild(element.parentNode);
			x--;
			iterator--;
			numberOfItems--;
		}
	}

}


/****************************************************************************
*  validateFields function to do the client side validation of all the fields
*******************************************************************************/

function validateFields(form)
{
		if(flag!=1){
			var selectBox = document.getElementsByName("type")[0];
			var indexArray = document.getElementsByName("indexOfLevel");
			var typeArray=document.getElementsByName("typeOfLevel");
			document.CheckListForm.typeId.value=typeArray[selectBox.selectedIndex].value;
			document.CheckListForm.categoryId.value=indexArray[selectBox.selectedIndex].value;
			document.CheckListForm.typeName.value=selectBox.options[selectBox.selectedIndex].text;
			var displayedStatusBox=document.getElementsByName("status")[0];
			document.CheckListForm.displayedStatus.value=displayedStatusBox.options[displayedStatusBox.selectedIndex].text;
			document.CheckListForm.method.value="preview";
			return true;
		}

}

/****************************************************************************
*  fnEdit function sets the hidden variable method to go to edit page
*******************************************************************************/

function fnManageEdit(form)
{
	form.action="chkListAction.do?method=managePrevious";
	form.submit();
}

function fnEdit(form)
{
	form.action="chkListAction.do?method=previous";
	form.submit();
}
/****************************************************************************
*  fnUpdate function sets the hidden variable 'method' to update
*******************************************************************************/
function fnUpdate(form)
{

	document.CheckListForm.categoryId.value=document.getElementsByName("categoryId")[0].value;
	document.CheckListForm.typeId.value=document.getElementsByName("typeId")[0].value;
	document.CheckListForm.checklistId.value=document.getElementsByName("checklistId")[0].value;
	document.CheckListForm.statusOfCheckList.value=document.getElementsByName("statusOfCheckList")[0].value;
	form.method.value="update";
	form.action="checkListAction.do";
	func_disableSubmitBtn("submitbutton");
	form.submit();
}
/****************************************************************************
*  fnCancel function sets the hidden variable method to get on load values
*******************************************************************************/
function fnCancel(form)
{
	flag=1;

	form.action="chkListAction.do?method=cancelCreate";
	form.submit();
}

/****************************************************************************
*  populateParent function sets the hidden variable method to get the status of each product or client
*******************************************************************************/
function populateStates(form,selectBox)
{
		var isCustIndex=document.getElementsByName("isCustomers");
		var masterIdIndex=document.getElementsByName("masterIds");
		var masterNameIndex = document.getElementsByName("masterNames");

		var isCust=isCustIndex[selectBox.selectedIndex].value;
		var masterId=masterIdIndex[selectBox.selectedIndex].value;
		var masterName=masterNameIndex[selectBox.selectedIndex].value;

		form.action="chkListAction.do?method=getStates&isCustomer="+isCust+"&masterTypeName="+masterName+"&masterTypeId="+masterId;
		form.submit();
}

function populateStateName(form,selectBox)
{
		var stateNameIndex=document.getElementsByName("stateNames");
		if (selectBox.selectedIndex > 0) {
			if(stateNameIndex[selectBox.selectedIndex - 1] != null){
				var stateName=stateNameIndex[selectBox.selectedIndex - 1].value;
				document.getElementsByName("stateName")[0].value = stateName;
			}
		}
}


/****************************************************************************
*  getChecklist function gets a particular checklist record based on checklist id ,type and status
*******************************************************************************/

function getChecklist(id,type,status,categoryId)
{
		flag=1;
		document.CheckListForm.categoryId.value=categoryId;
		document.CheckListForm.typeId.value=type;
		document.CheckListForm.checklistId.value=id;
		document.CheckListForm.statusOfCheckList.value=status;
 		document.CheckListForm.method.value="get";
 		document.CheckListForm.submit();

}

/****************************************************************************
*  setPreviousStatusId function sets the previous status id when the incoming page is previous
*******************************************************************************/
function setPreviousStatusId(pre)
{
	var status="previous"
	if(pre == status)
	{
		var displayedStatusBox=document.getElementsByName("status")[0];
		document.CheckListForm.previousStatusId.value=displayedStatusBox.options[displayedStatusBox.selectedIndex].value;
	}
}

/****************************************************************************
*  isButtonRequired function check if the Remove Selected is required or not
*  if there are no items button is not shown to the user
*******************************************************************************/
function isButtonRequired()
{
	if(numberOfItems>0)
	{
	document.getElementById("removeButton").style.display = "block";
	}
	else if(numberOfItems==0)
	{
	document.getElementById("removeButton").style.display = "none";
	}
}

/****************************************************************************
*  setNumberOfPreviousItems function checks number of previous items
*******************************************************************************/
function setNumberOfPreviousItems()
{
	var array=document.getElementsByName("numberOfPreviousItems");
	var counter;
	if(array!=null)
	for(counter=0;counter<array.length-1;counter++)
	{}
	i=i+counter;
	numberOfItems=i;
}
/****************************************************************************
*  manage function gets a particular checklist record based on checklist id ,type and status
*******************************************************************************/
function manage(id,type,status,categoryId)
{
		document.CheckListForm.categoryId.value=categoryId;
		document.CheckListForm.typeId.value=type;
		document.CheckListForm.checklistId.value=id;
		document.CheckListForm.statusOfCheckList.value=status;
 		document.CheckListForm.method.value="manage";
 		document.CheckListForm.submit();

}


function populateStatesEdit(form,selectBox)
{
		var isCustIndex=document.getElementsByName("isCustomers");
		var masterIdIndex=document.getElementsByName("masterIds");
		var masterNameIndex = document.getElementsByName("masterNames");

		var isCust=isCustIndex[selectBox.selectedIndex].value;
		var masterId=masterIdIndex[selectBox.selectedIndex].value;
		var masterName=masterNameIndex[selectBox.selectedIndex].value;

		form.action="chkListAction.do?method=getEditStates&isCustomer="+isCust+"&masterTypeName="+masterName+"&masterTypeId="+masterId;
		form.submit();
}


function getChklist(form)
{
		flag=1;
 		form.action="chkListAction.do?method=cancelManage";
		form.submit();
}