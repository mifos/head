/************************************************************************
* This is master file which contains all the javscript functions related to funds
*  author : Imtiyaz Baig
**************************************************************************/

function fnValidateOnCreate()
{
	document.forms[0].method.value="create";
	document.forms[0].action="fundAction.do";
	
}
function fnValidateOnPreview()
{
	document.forms[0].method.value="preview";
	document.forms[0].action="fundAction.do";
	
}
function fnPrevious(form)
{
	form.method.value="previous";	
	form.action="fundAction.do";
	form.submit();
	
}
function fnOnEdit(form)
{
	form.method.value="update";
	form.action="fundAction.do";
	
}

function fnCreateCancel()
{
	document.forms[0].method.value="load";
	document.forms[0].action="AdminAction.do";
	document.forms[0].submit();
	
}
function fnEditCancel(form)
{	
	form.action="fundAction.do?method=getAllFunds";
	form.submit();
	
}
function fnValidateOnEditPreview(form)
{
	form.method.value="preview";
	form.action="fundAction.do";
	form.submit();	
}
