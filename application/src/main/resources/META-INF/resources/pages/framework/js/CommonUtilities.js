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

jQuery.expr[':'].regex = function(elem, index, match) {
    var matchParams = match[3].split(','),
        validLabels = /^(data|css):/,
        attr = {
            method: matchParams[0].match(validLabels) ?
                        matchParams[0].split(':')[0] : 'attr',
            property: matchParams.shift().replace(validLabels,'')
        },
        regexFlags = 'ig',
        regex = new RegExp(matchParams.join('').replace(/^\s+|\s+$/g,''), regexFlags);
    return regex.test(jQuery(elem)[attr.method](attr.property));
}
