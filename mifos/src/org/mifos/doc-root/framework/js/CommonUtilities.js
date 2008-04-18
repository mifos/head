function func_disableSubmitBtn(buttonName){
	document.getElementsByName(buttonName)[0].disabled=true;
}
function restrictScript(evt) {
	var keyCodePress = (window.event)?event.keyCode:evt.which;
	if((keyCodePress==60) || (keyCodePress==62))
		return false;
	else
		return true;
}