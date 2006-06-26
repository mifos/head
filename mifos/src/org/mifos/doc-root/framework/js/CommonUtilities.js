/**
This js file holds java script functions which can be used across the application.
*/


/**
* This function is used to disable a button . It gets the button by doing
* a document.getElemntById on the button Name passed as parameter and then disables it.
*/
function func_disableSubmitBtn(buttonName){
	document.getElementsByName(buttonName)[0].disabled=true;
}